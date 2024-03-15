package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.cos
import kotlin.math.sin

private val cos: Float = cos(0.2f)
private val sin: Float = sin(0.2f)

private val matrixRotateX: Array<FloatArray> = arrayOf(
	floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f),
	floatArrayOf(0.0f, cos, -sin, 0.0f),
	floatArrayOf(0.0f, sin, cos, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

private val matrixRotateY: Array<FloatArray> = arrayOf(
	floatArrayOf(cos, 0.0f, sin, 0.0f),
	floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f),
	floatArrayOf(-sin, 0.0f, cos, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

private val matrixRotateZ: Array<FloatArray> = arrayOf(
	floatArrayOf(cos, -sin, 0.0f, 0.0f),
	floatArrayOf(sin, cos, 0.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

fun rotateVertices(type: String) {
	val threadFunction = when (type) {
		"x" -> staticCFunction(::tfRotateVerticesX)
		"y" -> staticCFunction(::tfRotateVerticesY)
		"z" -> staticCFunction(::tfRotateVerticesZ)
		else -> throw Exception("Wrong axis!")
	}

	memScoped {
		val params = Array(kernels) {
			alloc<IntVar>()
		}

		params.forEachIndexed { index, param -> param.value = index }

		val threads = Array(kernels) {
			CreateThread(null, 0u, threadFunction, params[it].ptr, 0u, null)
		}

		for (thread in threads) {
			WaitForSingleObject(thread, INFINITE)
			CloseHandle(thread)
		}
	}
}

private fun tfRotateVerticesX(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateX)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateX)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateX)
	}

	return 0u
}

private fun tfRotateVerticesY(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateY)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateY)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateY)
	}

	return 0u
}

private fun tfRotateVerticesZ(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateZ)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateZ)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateZ)
	}

	return 0u
}