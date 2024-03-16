package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs

private val rgb: RGB = RGB(255, 255, 255)

lateinit var displayMatrix: Array<FloatArray>
lateinit var lightPos: Vertex
lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	bitmapData.fill(0)

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
	val drawFace = Face(
		arrayOf(
			multiplyVertexByMatrix(face.vertices[0], displayMatrix),
			multiplyVertexByMatrix(face.vertices[1], displayMatrix),
			multiplyVertexByMatrix(face.vertices[2], displayMatrix)
		), face.normals, face.textures, face.depthArr, face.poliNormal
	)

	drawLine(drawFace.vertices[0], drawFace.vertices[1], rgb)
	drawLine(drawFace.vertices[1], drawFace.vertices[2], rgb)
	drawLine(drawFace.vertices[2], drawFace.vertices[0], rgb)
}

private fun drawLine(v1: Vertex, v2: Vertex, rgb: RGB) {
	var x1 = v1.x.toInt()
	val x2 = v2.x.toInt()
	var y1 = v1.y.toInt()
	val y2 = v2.y.toInt()

	val dx = abs(x2 - x1)
	val dy = abs(y2 - y1)
	val sx = if (x1 < x2) 1 else -1
	val sy = if (y1 < y2) 1 else -1
	var err = dx - dy

	while (x1 != x2 || y1 != y2) {
		if (x1 in 0 until windowWidth && y1 in 0 until windowHeight) {
			bitmapData.setRGB(x1, y1, rgb)
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