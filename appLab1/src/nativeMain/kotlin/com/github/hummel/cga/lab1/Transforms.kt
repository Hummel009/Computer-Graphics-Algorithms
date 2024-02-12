package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin

private const val angle: Float = 0.2f
private val cos = cos(angle)
private val sin = sin(angle)

fun multiplyVertexByMatrix(vertex: Vertex, matrix: Array<FloatArray>): Vertex {
	val result = FloatArray(4)

	for (i in 0 until 4) {
		result[i] = vertex.x * matrix[i][0] + vertex.y * matrix[i][1] + vertex.z * matrix[i][2] + matrix[i][3]
	}

	val w = result[3]
	return Vertex(result[0] / w, result[1] / w, result[2] / w)
}

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
	applyTransform(matrixRotateX)
}

fun rotateVerticesAxisY() {
	applyTransform(matrixRotateY)
}

fun rotateVerticesAxisZ() {
	applyTransform(matrixRotateZ)
}

private fun applyTransform(matrix: Array<FloatArray>) {
	for (vertex in vertices) {
		val tempVertex = multiplyVertexByMatrix(vertex, matrix)
		vertex.x = tempVertex.x
		vertex.y = tempVertex.y
		vertex.z = tempVertex.z
	}
}