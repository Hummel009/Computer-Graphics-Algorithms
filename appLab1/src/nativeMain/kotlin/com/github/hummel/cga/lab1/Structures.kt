package com.github.hummel.cga.lab1

import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.tan

private var eye: Vertex = Vertex(0.0f, 0.0f, 10.0f)
private var target: Vertex = Vertex(0.0f, 0.0f, 0.0f)
private var up: Vertex = Vertex(0.0f, 1.0f, 0.0f)

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

val resultMatrix: Array<FloatArray> =
	multiplyMatrixByMatrix(multiplyMatrixByMatrix(matrixViewport, matrixProjection), matrixView)

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float) {
	private val magnitude: Float = sqrt(x * x + y * y + z * z)

	constructor(x: Float, y: Float, z: Float) : this(x, y, z, 1.0f)

	operator fun minus(float: Float): Vertex = Vertex(x - float, y - float, z - float)

	operator fun plus(float: Float): Vertex = Vertex(x + float, y + float, z + float)

	operator fun times(float: Float): Vertex = Vertex(x * float, y * float, z * float)

	operator fun div(float: Float): Vertex = Vertex(x / float, y / float, z / float)

	operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y, z + other.z)

	infix fun vectorMul(other: Vertex): Vertex {
		val crossX = y * other.z - z * other.y
		val crossY = z * other.x - x * other.z
		val crossZ = x * other.y - y * other.x
		return Vertex(crossX, crossY, crossZ)
	}

	infix fun scalarMul(other: Vertex): Float = x * other.x + y * other.y + z * other.z

	@Deprecated("Use fastTransform() instead")
	fun toView(): Vertex = multiplyByMatrix(matrixView)

	@Deprecated("Use fastTransform() instead")
	fun toProjection(): Vertex = multiplyByMatrix(matrixProjection)

	@Deprecated("Use fastTransform() instead")
	fun toViewport(): Vertex = multiplyByMatrix(matrixViewport)

	fun fastTransform(): Vertex = multiplyByMatrix(resultMatrix)

	fun normalize(): Vertex = Vertex(x / magnitude, y / magnitude, z / magnitude)

	private fun multiplyByMatrix(matrix: Array<FloatArray>): Vertex {
		val result = FloatArray(4)

		for (i in 0 until 4) {
			result[i] = x * matrix[i][0] + y * matrix[i][1] + z * matrix[i][2] + matrix[i][3]
		}

		val w = result[3]
		return Vertex(result[0] / w, result[1] / w, result[2] / w)
	}
}

data class Face(val vertices: List<Int>, val textures: List<Int>, val normals: List<Int>)