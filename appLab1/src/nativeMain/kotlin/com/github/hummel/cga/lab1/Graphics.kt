package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs

private val zBuffer: FloatArray = FloatArray(width * height)

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

		for (thread in threads) {
			WaitForSingleObject(thread, INFINITE)
			CloseHandle(thread)
		}
	}
}

private fun tfDrawVertices(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	for (face in threadFaces[parameter]) {
		drawTriangle(face)
	}

	return 0u
}

private inline fun drawTriangle(face: Face) {
	val drawFace = Face(
		arrayOf(
			multiplyVertexByMatrix(face.vertices[0], displayMatrix),
			multiplyVertexByMatrix(face.vertices[1], displayMatrix),
			multiplyVertexByMatrix(face.vertices[2], displayMatrix)
		), face.normals, face.poliNormal
	)

	val color = Color(-1, -1, -1)

	drawLine(drawFace.vertices[0], drawFace.vertices[1], color)
	drawLine(drawFace.vertices[1], drawFace.vertices[2], color)
	drawLine(drawFace.vertices[2], drawFace.vertices[0], color)
}

private inline fun drawLine(v1: Vertex, v2: Vertex, color: Color) {
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
		if (x1 in 0 until width && y1 in 0 until height) {
			setPixel(x1, y1, color)
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

private inline fun setPixel(x: Int, y: Int, color: Color) {
	val offset = (y * width + x) shl 2
	bitmapData[offset + 0] = color.blue
	bitmapData[offset + 1] = color.green
	bitmapData[offset + 2] = color.red
	bitmapData[offset + 3] = -1
}