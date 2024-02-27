package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs

private val chunks: Int = faces.size / 100
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val zBuffer: FloatArray = FloatArray(width * height)

fun renderObject() {
	bitmapData.fill(0)
	zBuffer.fill(Float.POSITIVE_INFINITY)

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

private val optiTemp: Vertex = eye + up
private fun drawerThread(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	for (face in splitFaces[parameter]) {
		drawTriangle(face, zBuffer)
	}

	return 0u
}

private inline fun drawTriangle(face: Face, zBuffer: FloatArray) {
	val viewDir = (face.vertices[0] - eye).normalize()
	val cos = face.vertices[3] scalarMul viewDir
	if (cos > 0) {
		val ray = (face.center - optiTemp).normalize()
		val cosAngle = face.vertices[3].normalize() scalarMul ray
		val colorVal = (0xff * abs(cosAngle)).toInt().toByte()
		val color = Color(colorVal, colorVal, colorVal)

		val drawFace = Face(
			arrayOf(
				multiplyVertexByMatrix(face.vertices[0], displayMatrix),
				multiplyVertexByMatrix(face.vertices[1], displayMatrix),
				multiplyVertexByMatrix(face.vertices[2], displayMatrix)
			)
		)

		var minY = Int.MAX_VALUE
		var maxY = Int.MIN_VALUE
		for (vertex in drawFace.vertices) {
			val y = vertex.y.toInt()
			if (y < minY) {
				minY = y
			}
			if (y > maxY) {
				maxY = y
			}
		}

		// Создать цикл по каждой строке изображения
		for (y in minY..maxY) {
			if (y in 0 until height) {
				// Найти пересечения текущей строки с каждой из сторон треугольника
				val xIntersections = IntArray(2)
				var intersectionCount = 0
				for (i in 0..2) {
					val v0 = drawFace.vertices[i]
					val v1 = drawFace.vertices[(i + 1) % 3]
					val y0 = v0.y.toInt()
					val y1 = v1.y.toInt()
					if (y in y0 until y1 || y in y1 until y0) {
						val t = (y - y0) / (y1 - y0).toFloat()
						val x = (v0.x + t * (v1.x - v0.x)).toInt()
						xIntersections[intersectionCount] = x
						intersectionCount++
					}
				}

				// Отсортировать пересечения по возрастанию
				if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
					val temp = xIntersections[0]
					xIntersections[0] = xIntersections[1]
					xIntersections[1] = temp
				}

				// Заполнить пиксели между пересечениями цветом треугольника
				if (intersectionCount == 2) {
					for (x in xIntersections[0]..xIntersections[1]) {
						if (x in 0 until width) {
							val v0 = drawFace.vertices[0]
							val v1 = drawFace.vertices[1]
							val v2 = drawFace.vertices[2]
							val alpha =
								((v1.y - v2.y) * (x - v2.x) + (v2.x - v1.x) * (y - v2.y)) / ((v1.y - v2.y) * (v0.x - v2.x) + (v2.x - v1.x) * (v0.y - v2.y))
							val beta =
								((v2.y - v0.y) * (x - v2.x) + (v0.x - v2.x) * (y - v2.y)) / ((v1.y - v2.y) * (v0.x - v2.x) + (v2.x - v1.x) * (v0.y - v2.y))
							val gamma = 1 - alpha - beta
							val zFragment = alpha * v0.z + beta * v1.z + gamma * v2.z

							// Проверка z-буфера
							if (zBuffer[x * height + y] > zFragment) {
								zBuffer[x * height + y] = zFragment
								setPixel(x, y, color)
							}
						}
					}
				}
			}
		}
	}
}

private inline fun setPixel(x: Int, y: Int, color: Color) {
	val offset = (y * width + x) shl 2
	bitmapData[offset + 0] = color.blue
	bitmapData[offset + 1] = color.green
	bitmapData[offset + 2] = color.red
	bitmapData[offset + 3] = -1
}

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}