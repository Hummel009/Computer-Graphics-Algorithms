package com.github.hummel.cga.lab4

import kotlin.concurrent.thread
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
		"x" -> ::tfRotateVerticesX
		"y" -> ::tfRotateVerticesY
		"z" -> ::tfRotateVerticesZ
		else -> throw Exception("Wrong axis!")
	}

	val threads = Array(kernels) { thread { threadFunction(it) } }

	threads.forEach { it.join() }
}

private fun tfRotateVerticesX(parameter: Int) {
	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateX)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateX)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateX)
	}
}

private fun tfRotateVerticesY(parameter: Int) {
	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateY)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateY)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateY)
	}
}

private fun tfRotateVerticesZ(parameter: Int) {
	for (face in threadFaces[parameter]) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrixRotateZ)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrixRotateZ)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrixRotateZ)
	}
}