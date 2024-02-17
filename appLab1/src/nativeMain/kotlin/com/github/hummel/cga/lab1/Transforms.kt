package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.measureTime

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

private val times: MutableList<Long> = ArrayList()
private inline fun applyTransform(matrix: Array<FloatArray>) {
	val time = measureTime {
		for ((vertices) in faces) {
			for (i in vertices.indices) {
				vertices[i] = multiplyVertexByMatrix(vertices[i], matrix)
			}
		}
	}.inWholeMilliseconds

	times.add(time)

	println("Transform: ${times.average()}")
}