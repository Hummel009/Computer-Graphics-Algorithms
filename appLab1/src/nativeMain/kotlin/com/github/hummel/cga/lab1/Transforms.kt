package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin

fun rotateVerticesAxisX(angle: Float = 0.2f) {
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

fun rotateVerticesAxisY(angle: Float = 0.2f) {
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

fun rotateVerticesAxisZ(angle: Float = 0.2f) {
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

private inline fun applyTransform(matrix: Array<FloatArray>) {
	for (face in faces) {
		for (i in face.vertices.indices) {
			face.vertices[i] = multiplyVertexByMatrix(face.vertices[i], matrix)
		}
		for (i in face.normals.indices) {
			face.normals[i] = multiplyVertexByMatrix(face.normals[i], matrix)
		}
		face.poliNormal = multiplyVertexByMatrix(face.poliNormal, matrix)
	}
}