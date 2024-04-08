package com.github.hummel.cga.lab1j

import kotlin.math.sqrt

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float = 1.0f) {
	private val magnitude: Float = sqrt(x * x + y * y + z * z)

	operator fun minus(float: Float): Vertex = Vertex(x - float, y - float, z - float)

	operator fun plus(float: Float): Vertex = Vertex(x + float, y + float, z + float)

	operator fun times(float: Float): Vertex = Vertex(x * float, y * float, z * float)

	operator fun div(float: Float): Vertex = Vertex(x / float, y / float, z / float)

	operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y, z + other.z)

	operator fun unaryMinus(): Vertex = Vertex(-x, -y, -z)

	infix fun divSelf(float: Float) {
		x /= float
		y /= float
		z /= float
	}

	infix fun vectorMul(other: Vertex): Vertex {
		val crossX = y * other.z - z * other.y
		val crossY = z * other.x - x * other.z
		val crossZ = x * other.y - y * other.x
		return Vertex(crossX, crossY, crossZ)
	}

	infix fun scalarMul(other: Vertex): Float = x * other.x + y * other.y + z * other.z

	fun normalize(): Vertex = Vertex(x / magnitude, y / magnitude, z / magnitude)
}

data class Face(
	val realVertices: Array<Vertex>,
	val viewVertices: Array<Vertex> = Array(3) { Vertex(0.0f, 0.0f, 0.0f) },
	val normals: Array<Vertex>,
	val textels: Array<Vertex>
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (javaClass != other?.javaClass) {
			return false
		}

		other as Face

		if (!realVertices.contentEquals(other.realVertices)) {
			return false
		}
		if (!viewVertices.contentEquals(other.viewVertices)) {
			return false
		}
		if (!normals.contentEquals(other.normals)) {
			return false
		}
		if (!textels.contentEquals(other.textels)) {
			return false
		}

		return true
	}

	override fun hashCode(): Int {
		var result = realVertices.contentHashCode()
		result = 31 * result + viewVertices.contentHashCode()
		result = 31 * result + normals.contentHashCode()
		result = 31 * result + textels.contentHashCode()
		return result
	}
}

data class RGB(val r: Int, val g: Int, val b: Int)