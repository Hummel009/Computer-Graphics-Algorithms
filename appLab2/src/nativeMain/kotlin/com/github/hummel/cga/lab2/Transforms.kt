package com.github.hummel.cga.lab2

import kotlin.math.cos
import kotlin.math.sin

fun translateVertices(shiftX: Float, shiftY: Float) {
	val vertex = Vertex(shiftX, shiftY, 0.0f)

	val matrix = arrayOf(
		floatArrayOf(1.0f, 0.0f, 0.0f, vertex.x),
		floatArrayOf(0.0f, 1.0f, 0.0f, vertex.y),
		floatArrayOf(0.0f, 0.0f, 1.0f, vertex.z),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	applyTransform(matrix)
}

fun scaleVertices(scale: Float) {
	val vertex = Vertex(scale, scale, scale)

	val matrix = arrayOf(
		floatArrayOf(vertex.x, 0.0f, 0.0f, 0.0f),
		floatArrayOf(0.0f, vertex.y, 0.0f, 0.0f),
		floatArrayOf(0.0f, 0.0f, vertex.z, 0.0f),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	applyTransform(matrix)
}

fun rotateVerticesAxisX() {
	val angle = 0.2f
	val cos = cos(angle)
	val sin = sin(angle)

	val matrixRotateX = arrayOf(
		floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f),
		floatArrayOf(0.0f, cos, -sin, 0.0f),
		floatArrayOf(0.0f, sin, cos, 0.0f),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	applyTransform(matrixRotateX)
}

fun rotateVerticesAxisY() {
	val angle = 0.2f
	val cos = cos(angle)
	val sin = sin(angle)

	val matrixRotateY = arrayOf(
		floatArrayOf(cos, 0.0f, sin, 0.0f),
		floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f),
		floatArrayOf(-sin, 0.0f, cos, 0.0f),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	applyTransform(matrixRotateY)
}

fun rotateVerticesAxisZ() {
	val angle = 0.2f
	val cos = cos(angle)
	val sin = sin(angle)

	val matrixRotateZ = arrayOf(
		floatArrayOf(cos, -sin, 0.0f, 0.0f),
		floatArrayOf(sin, cos, 0.0f, 0.0f),
		floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	applyTransform(matrixRotateZ)
}

private fun applyTransform(matrix: Array<FloatArray>) {
	for (vertex in vertices) {
		multiplyVertexByMatrix(vertex, matrix)
	}
}

private fun multiplyVertexByMatrix(vertex: Vertex, matrix: Array<FloatArray>) {
	val result = FloatArray(4)

	for (i in 0 until 4) {
		result[i] = vertex.x * matrix[i][0] + vertex.y * matrix[i][1] + vertex.z * matrix[i][2] + matrix[i][3]
	}

	val w = result[3]

	vertex.x = result[0] / w
	vertex.y = result[1] / w
	vertex.z = result[2] / w
}