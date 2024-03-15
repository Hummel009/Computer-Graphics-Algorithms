package com.github.hummel.cga.lab1

import kotlin.math.PI
import kotlin.math.tan

private val target: Vertex = Vertex(0.0f, 0.0f, 0.0f)
private val up: Vertex = Vertex(0.0f, 1.0f, 0.0f)

private var fov: Float = PI.toFloat() / 4.0f
private var aspect = hWidth.toFloat() / hHeight.toFloat()

private var zNear: Float = 1.0f
private var zFar: Float = 100.0f

private val matrixProjection: Array<FloatArray> = arrayOf(
	floatArrayOf(1.0f / (aspect * tan(fov / 2.0f)), 0.0f, 0.0f, 0.0f),
	floatArrayOf(0.0f, 1.0f / (tan(fov / 2.0f)), 0.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, zFar / (zNear - zFar), (zNear * zFar) / (zNear - zFar)),
	floatArrayOf(0.0f, 0.0f, -1.0f, 0.0f)
)

private val matrixViewport: Array<FloatArray> = arrayOf(
	floatArrayOf(hWidth.toFloat() / 2.0f, 0.0f, 0.0f, hWidth.toFloat() / 2.0f),
	floatArrayOf(0.0f, hHeight.toFloat() / 2.0f, 0.0f, hHeight.toFloat() / 2.0f),
	floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

private val staticMultiplier: Array<FloatArray> = multiplyMatrixByMatrix(matrixViewport, matrixProjection)

fun getDisplayMatrix(eye: Vertex): Array<FloatArray> {
	val zAxis = (eye - target).normalize()
	val xAxis = (up vectorMul zAxis).normalize()
	val yAxis = xAxis vectorMul zAxis

	val matrixView = arrayOf(
		floatArrayOf(xAxis.x, xAxis.y, xAxis.z, -(xAxis scalarMul eye)),
		floatArrayOf(yAxis.x, yAxis.y, yAxis.z, -(yAxis scalarMul eye)),
		floatArrayOf(zAxis.x, zAxis.y, zAxis.z, -(zAxis scalarMul eye)),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	return multiplyMatrixByMatrix(staticMultiplier, matrixView)
}

fun getLightPos(eye: Vertex): Vertex = eye + up