@file:Suppress("unused")

package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin

const val angleX: Float = 0.2f
const val angleY: Float = 0.2f
const val angleZ: Float = 0.2f

fun multiplyMatrices(matrix1: Array<FloatArray>, matrix2: Array<FloatArray>): Array<FloatArray> {
	val result = Array(matrix1.size) { FloatArray(matrix2[0].size) }

	for (i in matrix1.indices) {
		for (j in matrix2[0].indices) {
			for (k in matrix2.indices) {
				result[i][j] += matrix1[i][k] * matrix2[k][j]
			}
		}
	}

	return result
}

fun translateVertices(shiftX: Float, shiftY: Float) {
	for (vertex in vertices) {
		vertex.x += shiftX
		vertex.y += shiftY
	}
}

fun scaleVertices(scale: Float) {
	for (vertex in vertices) {
		vertex.x *= scale
		vertex.y *= scale
		vertex.z *= scale
	}
}

fun rotateVerticesAroundX() {
	val cos = cos(angleX)
	val sin = sin(angleX)

	for (vertex in vertices) {
		val y = vertex.y
		val z = vertex.z
		vertex.y = y * cos - z * sin
		vertex.z = y * sin + z * cos
	}
}

fun rotateVerticesAroundY() {
	val cos = cos(angleY)
	val sin = sin(angleY)

	for (vertex in vertices) {
		val x = vertex.x
		val z = vertex.z
		vertex.x = x * cos + z * sin
		vertex.z = -x * sin + z * cos
	}
}

fun rotateVerticesAroundZ() {
	val cos = cos(angleZ)
	val sin = sin(angleZ)

	for (vertex in vertices) {
		val x = vertex.x
		val y = vertex.y
		vertex.x = x * cos - y * sin
		vertex.y = x * sin + y * cos
	}
}