package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs

private val chunks: Int = faces.size / 1000
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val white: Color = Color(255, 255, 255, 255)
private val black: Color = Color(0, 0, 0, 255)

private val zBuffer: DoubleArray = DoubleArray(width * height)

fun renderObject() {
	fillBackground(white)


	val triangleList = addNormals(faces)
	val filteredList = filterTriangles(triangleList)
	val drawList = applyMatrix(filteredList, displayMatrix)

	zBuffer.fill(Double.POSITIVE_INFINITY)

	for (i in drawList.indices) {
		val t = filteredList[i]
		val drawT = drawList[i]
		val center = getCenter(t)
		val normal = t.vertices[3].normalize()
		val ray = center.subtract(eye).subtract(up).normalize()
		val cosAngle = normal.dot(ray)
		drawRasterTriangle(drawT, zBuffer, cosAngle)
	}
}

private fun drawerThread(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value

	for ((vertices, _, _) in splitFaces[parameter!!]) {
		if (vertices.size >= 3) {
			var previousVertex = displayTransform(vertices.last())

			for (i in vertices) {
				val currentVertex = displayTransform(i)
				drawLine(previousVertex, currentVertex, black)
				previousVertex = currentVertex
			}

			val firstVertex = displayTransform(vertices.first())
			drawLine(previousVertex, firstVertex, black)
		}
	}
	return 0u
}

private fun drawLine(v1: Vertex, v2: Vertex, color: Color) {
	var x1 = v1.x.toInt()
	val x2 = v2.x.toInt()
	var y1 = v1.y.toInt()
	val y2 = v2.y.toInt()

	val dx = abs(x2 - x1)
	val dy = abs(y2 - y1)
	val sx = if (x1 < x2) 1 else -1
	val sy = if (y1 < y2) 1 else -1
	var err = dx - dy

	while (true) {
		if (x1 in 0 until width && y1 in 0 until height) {
			val offset = (y1 * width + x1) shl 2

			bitmapData[offset + 0] = color.blue
			bitmapData[offset + 1] = color.green
			bitmapData[offset + 2] = color.red
			bitmapData[offset + 3] = color.alpha
		}

		if (x1 == x2 && y1 == y2) {
			break
		}

		val err2 = 2 * err

		if (err2 > -dy) {
			err -= dy
			x1 += sx
		}

		if (err2 < dx) {
			err += dx
			y1 += sy
		}
	}
}

private fun fillBackground(color: Color) {
	for (y in 0 until height) {
		for (x in 0 until width) {
			val offset = (y * width + x) * 4
			bitmapData[offset + 0] = color.blue
			bitmapData[offset + 1] = color.green
			bitmapData[offset + 2] = color.red
			bitmapData[offset + 3] = color.alpha
		}
	}
}

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}