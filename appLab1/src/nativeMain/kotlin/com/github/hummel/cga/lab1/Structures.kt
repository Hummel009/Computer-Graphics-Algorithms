package com.github.hummel.cga.lab1

import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.tan

private var eye: Vertex = Vertex(0.0f, 0.0f, 10.0f)
private var target: Vertex = Vertex(0.0f, 10.0f, 0.0f)
private var up: Vertex = Vertex(0.0f, 1.0f, 0.0f)
private var fov: Float = PI.toFloat() / 4.0f
private var aspect = width.toFloat() / height.toFloat()
private var zNear: Float = 1.0f
private var zFar: Float = 100.0f
private var xMin: Float = 0.0f
private var yMin: Float = 0.0f

data class Vertex(var x: Float, var y: Float, var z: Float) {
	operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	operator fun times(other: Vertex): Vertex = Vertex(x * other.x, y * other.y, z * other.z)

	fun toView(): Vertex {
		val zAxis = (eye - target).normalize()
		val xAxis = (up * zAxis).normalize()
		val yAxis = up

		val matrix = arrayOf(
			floatArrayOf(xAxis.x, xAxis.y, xAxis.z, -(xAxis * eye).x),
			floatArrayOf(yAxis.x, yAxis.y, yAxis.z, -(yAxis * eye).y),
			floatArrayOf(zAxis.x, zAxis.y, zAxis.z, -(zAxis * eye).z),
			floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		)

		return multiplyVertexByMatrix(this, matrix)
	}

	fun toProjection(): Vertex {
		val matrix = arrayOf(
			floatArrayOf(2.0f / width.toFloat(), 0.0f, 0.0f, 0.0f),
			floatArrayOf(0.0f, 2.0f / width.toFloat(), 0.0f, 0.0f),
			floatArrayOf(0.0f, 0.0f, 1.0f / (zNear - zFar), zNear / (zNear - zFar)),
			floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		)

		return multiplyVertexByMatrix(this, matrix)
	}

	fun toPerspective(): Vertex {
		val matrix = arrayOf(
			floatArrayOf(1.0f / (aspect * tan(fov / 2.0f)), 0.0f, 0.0f, 0.0f),
			floatArrayOf(0.0f, 1.0f / (tan(fov / 2.0f)), 0.0f, 0.0f),
			floatArrayOf(0.0f, 0.0f, zFar / (zNear - zFar), (zNear * zFar) / (zNear - zFar)),
			floatArrayOf(0.0f, 0.0f, -1.0f, 0.0f)
		)

		return multiplyVertexByMatrix(this, matrix)
	}

	fun toViewport(): Vertex {
		val matrix = arrayOf(
			floatArrayOf(width.toFloat() / 2.0f, 0.0f, 0.0f, xMin + (width.toFloat() / 2.0f)),
			floatArrayOf(0.0f, -height.toFloat() / 2.0f, 0.0f, yMin + (height.toFloat() / 2.0f)),
			floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
			floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		)

		return multiplyVertexByMatrix(this, matrix)
	}

	private fun normalize(): Vertex {
		val magnitude = sqrt(x * x + y * y + z * z)
		return Vertex(x / magnitude, y / magnitude, z / magnitude)
	}
}

data class Face(val v1: Int, val v2: Int, val v3: Int)