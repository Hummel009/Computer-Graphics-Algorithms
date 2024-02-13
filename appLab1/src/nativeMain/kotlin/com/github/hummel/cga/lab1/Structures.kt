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
private val xAxis = (up cross zAxis).normalize()
private val yAxis = xAxis cross zAxis

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

private val matrixView = arrayOf(
	floatArrayOf(xAxis.x, xAxis.y, xAxis.z, -(xAxis dot eye)),
	floatArrayOf(yAxis.x, yAxis.y, yAxis.z, -(yAxis dot eye)),
	floatArrayOf(zAxis.x, zAxis.y, zAxis.z, -(zAxis dot eye)),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

data class Vertex(var x: Float, var y: Float, var z: Float) {
	operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	infix fun cross(other: Vertex): Vertex {
		val crossX = y * other.z - z * other.y
		val crossY = z * other.x - x * other.z
		val crossZ = x * other.y - y * other.x
		return Vertex(crossX, crossY, crossZ)
	}

	infix fun dot(other: Vertex): Float = x * other.x + y * other.y + z * other.z

	fun toView(): Vertex = multiplyVertexByMatrix(this, matrixView)

	fun toProjection(): Vertex = multiplyVertexByMatrix(this, matrixProjection)

	fun toViewport(): Vertex = multiplyVertexByMatrix(this, matrixViewport)

	fun normalize(): Vertex {
		val magnitude = sqrt(x * x + y * y + z * z)
		return Vertex(x / magnitude, y / magnitude, z / magnitude)
	}

	private fun multiplyVertexByMatrix(vertex: Vertex, matrix: Array<FloatArray>): Vertex {
		val result = FloatArray(4)

		for (i in 0 until 4) {
			result[i] = vertex.x * matrix[i][0] + vertex.y * matrix[i][1] + vertex.z * matrix[i][2] + matrix[i][3]
		}

		val w = result[3]
		return Vertex(result[0] / w, result[1] / w, result[2] / w)
	}
}

data class Face(val v1: Int, val v2: Int, val v3: Int)