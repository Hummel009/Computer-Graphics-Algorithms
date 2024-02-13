package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.*

private val chunks: Int = faces.size / 1000
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val zBuffer: List<MutableList<Float>> = List(height) { MutableList(width) { Float.MAX_VALUE } }

fun drawLines() {
	memScoped {
		val params = Array(chunks) {
			alloc<IntVar>()
		}

		params.forEachIndexed { index, param -> param.value = index }

		val threads = Array(chunks) {
			CreateThread(null, 0u, staticCFunction(::drawerThread), params[it].ptr, 0u, null)
		}

		for (thread in threads) {
			WaitForSingleObject(thread, INFINITE)
			CloseHandle(thread)
		}
	}
}

private fun drawerThread(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value

	for ((faceVertices, _, _) in splitFaces[parameter!!]) {
		if (faceVertices.size >= 3) {
			var previousVertex = vertices[faceVertices.last()].toView().toProjection().toViewport()

			for (i in faceVertices) {
				val currentVertex = vertices[i].toView().toProjection().toViewport()
				drawLineDDA(previousVertex, currentVertex, Color(0, 0, 0, 255))
				previousVertex = currentVertex
			}

			val firstVertex = vertices[faceVertices.first()].toView().toProjection().toViewport()
			drawLineDDA(previousVertex, firstVertex, Color(0, 0, 0, 255))
		}
	}
	return 0u
}

private fun drawLineDDA(v1: Vertex, v2: Vertex, color: Color) {
	var x = v1.x
	var y = v1.y

	val dx = v2.x - x
	val dy = v2.y - y
	val steps = max(abs(dx), abs(dy)).toInt()

	val xIncrement = dx / steps
	val yIncrement = dy / steps

	for (i in 0..steps step 2) {
		if (!(x > width - 1 || x < 0 || y > height - 1 || y < 0)) {
			val index = (round(y).toInt() * width + round(x).toInt()) shl 2

			bitmapData[index + 0] = color.blue // BLUE
			bitmapData[index + 1] = color.green // GREEN
			bitmapData[index + 2] = color.red // RED
			bitmapData[index + 3] = color.alpha // ALPHA
		}

		x += xIncrement
		y += yIncrement
	}
}

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}

private fun rasterize(vertices: List<Vertex>, color: Color) {
	for (i in 1 until vertices.size - 1) {
		myRasterizeTriangle(
			Triangle(
				Vertex(vertices[0].x, vertices[0].y, vertices[0].z),
				Vertex(vertices[i].x, vertices[i].y, vertices[i].z),
				Vertex(vertices[i + 1].x, vertices[i + 1].y, vertices[i + 1].z)
			), color
		)
	}
}

private fun interpolate(i0: Float, d0: Float, i1: Float, d1: Float): MutableList<Float> {
	if (i0 == i1) {
		return mutableListOf(d0)
	}

	val values = mutableListOf<Float>()

	val a = (d1 - d0) / (i1 - i0)
	var d = d0

	var i = i0.toInt()
	while (i < i1) {
		values.add(d)
		d += a
		i++
	}

	return values
}

private fun rasterizeTriangle(triangle: Triangle) {
	triangle.getHorizontalLines().asSequence().filter {
		it.left.x > 0 && it.left.y > 0 && it.right.x > 0 && it.right.y > 0 && it.left.x < width && it.left.y < height && it.right.x < width && it.right.y < height
	}.forEach {
		drawLineDDA(it.left, it.right, Color(255, 255, 255, 255))
	}
}

private fun myRasterizeTriangle(triangle: Triangle, color: Color) {
	if (triangle.a.x > 0 && triangle.b.x > 0 && triangle.c.x > 0 && triangle.a.y > 0 && triangle.b.y > 0 && triangle.c.y > 0 && triangle.a.x < width && triangle.b.x < width && triangle.c.x < width && triangle.a.y < height && triangle.b.y < height && triangle.c.y < height) {

		if (triangle.b.y < triangle.a.y) {
			val temp = triangle.b
			triangle.b = triangle.a
			triangle.a = temp
		}
		if (triangle.c.y < triangle.a.y) {
			val temp = triangle.c
			triangle.c = triangle.a
			triangle.a = temp
		}
		if (triangle.c.y < triangle.b.y) {
			val temp = triangle.c
			triangle.c = triangle.b
			triangle.b = temp
		}

		val x01 = interpolate(triangle.a.y, triangle.a.x, triangle.b.y, triangle.b.x)
		val x12 = interpolate(triangle.b.y, triangle.b.x, triangle.c.y, triangle.c.x)
		val x02 = interpolate(triangle.a.y, triangle.a.x, triangle.c.y, triangle.c.x)

		val z01 = interpolate(triangle.a.y, triangle.a.z, triangle.b.y, triangle.b.z)
		val z12 = interpolate(triangle.b.y, triangle.b.z, triangle.c.y, triangle.c.z)
		val z02 = interpolate(triangle.a.y, triangle.a.z, triangle.c.y, triangle.c.z)

		x01.removeAt(x01.size - 1)
		val x012 = (x01 + x12).toMutableList()
		z01.removeAt(z01.size - 1)
		val z012 = (z01 + z12).toMutableList()

		val m = floor(x012.size / 2.0).toInt()

		val xLeft: MutableList<Float>
		val xRight: MutableList<Float>
		val zLeft: MutableList<Float>
		val zRight: MutableList<Float>

		if (x02[m] < x012[m]) {
			xLeft = x02
			xRight = x012
			zLeft = z02
			zRight = z012
		} else {
			xLeft = x012
			xRight = x02
			zLeft = z012
			zRight = z02
		}

		val topY = maxOf(0, ceil(triangle.a.y).toInt())
		val bottomY = minOf(height, ceil(triangle.c.y).toInt())

		for (y in topY until bottomY) {
			val index = (y - triangle.a.y).toInt()

			val leftX = maxOf(0, ceil(xLeft[index]).toInt())
			val rightX = minOf(width, ceil(xRight[index]).toInt())

			val zl = zLeft[index]
			val zr = zRight[index]
			val zscan = interpolate(leftX.toFloat(), zl, rightX.toFloat(), zr)

			for (x in leftX until rightX) {
				val z = zscan[x - leftX]
				if (z < zBuffer[y][x]) {
					zBuffer[y][x] = z

					val pixelPtr = (y * width + x) shl 2
					bitmapData[pixelPtr + 0] = color.blue // BLUE
					bitmapData[pixelPtr + 1] = color.green // GREEN
					bitmapData[pixelPtr + 2] = color.red // RED
					bitmapData[pixelPtr + 3] = color.alpha // ALPHA
				}
			}
		}
	}
}