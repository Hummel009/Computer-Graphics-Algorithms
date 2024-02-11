@file:Suppress("unused")

package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin

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

fun translateVectors(shiftX: Float, shiftY: Float) {
	for (vertex in vertices) {
		vertex.x += shiftX
		vertex.y += shiftY
	}
}

fun scaleVectors(scale: Float) {
	for (vertex in vertices) {
		vertex.x *= scale
		vertex.y *= scale
		vertex.z *= scale
	}
}

fun rotateVectorsAroundX() {
	val cos = cos(rotationSpeedX)
	val sin = sin(rotationSpeedX)

	for (vertex in vertices) {
		val y = vertex.y
		val z = vertex.z
		vertex.y = y * cos - z * sin
		vertex.z = y * sin + z * cos
	}
	rotationAngleX += rotationSpeedX
}

fun rotateVectorsAroundY() {
	val cos = cos(rotationSpeedZ)
	val sin = sin(rotationSpeedZ)

	for (vertex in vertices) {
		val x = vertex.x
		val z = vertex.z
		vertex.x = x * cos + z * sin
		vertex.z = -x * sin + z * cos
	}
	rotationAngleY += rotationSpeedY
}

fun rotateVectorsAroundZ() {
	val cos = cos(rotationSpeedZ)
	val sin = sin(rotationSpeedZ)

	for (vertex in vertices) {
		val x = vertex.x
		val y = vertex.y
		vertex.x = x * cos - y * sin
		vertex.y = x * sin + y * cos
	}
	rotationAngleZ += rotationSpeedZ
}