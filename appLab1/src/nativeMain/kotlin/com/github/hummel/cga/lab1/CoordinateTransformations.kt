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
	for (point in points) {
		point.x += shiftX
		point.y += shiftY
	}
}

fun scaleVectors(scale: Float) {
	for (point in points) {
		point.x *= scale
		point.y *= scale
		point.z *= scale
	}
}

fun rotateVectorsAroundX() {
	val cos = cos(rotationSpeedX)
	val sin = sin(rotationSpeedX)

	for (point in points) {
		val y = point.y
		val z = point.z
		point.y = y * cos - z * sin
		point.z = y * sin + z * cos
	}
	rotationAngleX += rotationSpeedX
}

fun rotateVectorsAroundY() {
	val cos = cos(rotationSpeedZ)
	val sin = sin(rotationSpeedZ)

	for (point in points) {
		val x = point.x
		val z = point.z
		point.x = x * cos + z * sin
		point.z = -x * sin + z * cos
	}
	rotationAngleY += rotationSpeedY
}

fun rotateVectorsAroundZ() {
	val cos = cos(rotationSpeedZ)
	val sin = sin(rotationSpeedZ)

	for (point in points) {
		val x = point.x
		val y = point.y
		point.x = x * cos - y * sin
		point.y = x * sin + y * cos
	}
	rotationAngleZ += rotationSpeedZ
}