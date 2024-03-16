package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*

private val zBuffer: FloatArray = FloatArray(windowWidth * windowHeight)

lateinit var displayMatrix: Array<FloatArray>
lateinit var lightPos: Vertex
lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	bitmapData.fill(0)
	zBuffer.fill(Float.POSITIVE_INFINITY)

	memScoped {
		val params = Array(kernels) {
			alloc<IntVar>()
		}

		params.forEachIndexed { index, param -> param.value = index }

		val threads = Array(kernels) {
			CreateThread(null, 0u, staticCFunction(::tfDrawVertices), params[it].ptr, 0u, null)
		}

		threads.forEach {
			WaitForSingleObject(it, INFINITE)
			CloseHandle(it)
		}
	}
}

private fun tfDrawVertices(parameters: LPVOID?): DWORD {
	val id = parameters?.reinterpret<IntVar>()?.pointed?.value!!

	threadFaces[id].forEach { drawTriangle(it) }

	return 0u
}

private inline fun drawTriangle(face: Face) {
	val viewDir = -face.vertices[0] + eyePos
	val cosAngle = (face.poliNormal / face.normals.size.toFloat()) scalarMul viewDir

	if (cosAngle <= 0) {
		return
	}

	val drawFace = Face(
		arrayOf(
			multiplyVertexByMatrix(face.vertices[0], displayMatrix),
			multiplyVertexByMatrix(face.vertices[1], displayMatrix),
			multiplyVertexByMatrix(face.vertices[2], displayMatrix)
		), face.normals, face.textures, face.depthArr, face.poliNormal
	)

	val depthArr = FloatArray(3)
	val vertices = drawFace.vertices

	for (i in vertices.indices) {
		depthArr[i] = vertices[i].w
		vertices[i] divSelf vertices[i].w
	}

	drawFace.depthArr = depthArr

	var minY = Int.MAX_VALUE
	var maxY = Int.MIN_VALUE

	drawFace.vertices.forEach {
		val y = it.y.toInt()
		if (y < minY) {
			minY = y
		}
		if (y > maxY) {
			maxY = y
		}
	}

	// Создать цикл по каждой строке изображения
	for (y in minY..maxY) {
		if (y in 0 until windowHeight) {
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
					if (x in 0 until windowWidth) {
						val v0 = drawFace.vertices[0]
						val v1 = drawFace.vertices[1]
						val v2 = drawFace.vertices[2]

						val coords = drawFace.getBarycentricCoords(x, y)

						var alpha = coords[0]
						var beta = coords[1]
						var gamma = coords[2]

						drawFace.depthArr?.let { alpha /= it[0] }
						drawFace.depthArr?.let { beta /= it[1] }
						drawFace.depthArr?.let { gamma /= it[2] }

						val sum = alpha + beta + gamma

						alpha /= sum
						beta /= sum
						gamma /= sum

						val zFragment = alpha * v0.z + beta * v1.z + gamma * v2.z

						if (zBuffer[x * windowHeight + y] > zFragment) {
							zBuffer[x * windowHeight + y] = zFragment

							val rgb = getResultRgb(face)

							bitmapData.setRGB(x, y, rgb)
						}
					}
				}
			}
		}
	}
}