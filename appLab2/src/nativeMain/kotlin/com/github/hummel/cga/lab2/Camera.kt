package com.github.hummel.cga.lab2

import kotlin.math.PI
import kotlin.math.tan

var eye: Vertex = Vertex(0.0f, 0.0f, 10.0f)
var target: Vertex = Vertex(0.0f, 0.0f, 0.0f)
var up: Vertex = Vertex(0.0f, 1.0f, 0.0f)

private var fov: Float = PI.toFloat() / 4.0f
private var aspect = width.toFloat() / height.toFloat()

private var zNear: Float = 1.0f
private var zFar: Float = 100.0f

private val zAxis = (eye - target).normalize()
private val xAxis = (up vectorMul zAxis).normalize()
private val yAxis = xAxis vectorMul zAxis

private val matrixView: Array<FloatArray> = arrayOf(
	floatArrayOf(xAxis.x, xAxis.y, xAxis.z, -(xAxis scalarMul eye)),
	floatArrayOf(yAxis.x, yAxis.y, yAxis.z, -(yAxis scalarMul eye)),
	floatArrayOf(zAxis.x, zAxis.y, zAxis.z, -(zAxis scalarMul eye)),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

private val matrixProjection: Array<FloatArray> = arrayOf(
	floatArrayOf(1.0f / (aspect * tan(fov / 2.0f)), 0.0f, 0.0f, 0.0f),
	floatArrayOf(0.0f, 1.0f / (tan(fov / 2.0f)), 0.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, zFar / (zNear - zFar), (zNear * zFar) / (zNear - zFar)),
	floatArrayOf(0.0f, 0.0f, -1.0f, 0.0f)
)

private val matrixViewport: Array<FloatArray> = arrayOf(
	floatArrayOf(width.toFloat() / 2.0f, 0.0f, 0.0f, width.toFloat() / 2.0f),
	floatArrayOf(0.0f, height.toFloat() / 2.0f, 0.0f, height.toFloat() / 2.0f),
	floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

val displayMatrix: Array<FloatArray> =
	multiplyMatrixByMatrix(multiplyMatrixByMatrix(matrixViewport, matrixProjection), matrixView)