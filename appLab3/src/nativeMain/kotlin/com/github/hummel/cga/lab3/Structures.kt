package com.github.hummel.cga.lab3

import kotlin.math.sqrt

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float = 1.0f) {
	val magnitude: Float = sqrt(x * x + y * y + z * z)

	inline operator fun minus(float: Float): Vertex = Vertex(x - float, y - float, z - float)

	inline operator fun plus(float: Float): Vertex = Vertex(x + float, y + float, z + float)

	inline operator fun times(float: Float): Vertex = Vertex(x * float, y * float, z * float)

	inline operator fun div(float: Float): Vertex = Vertex(x / float, y / float, z / float)

	inline operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	inline operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y, z + other.z)

	inline operator fun unaryMinus(): Vertex = Vertex(-x, -y, -z)

	inline infix fun vectorMul(other: Vertex): Vertex {
		val crossX = y * other.z - z * other.y
		val crossY = z * other.x - x * other.z
		val crossZ = x * other.y - y * other.x
		return Vertex(crossX, crossY, crossZ)
	}

	inline infix fun scalarMul(other: Vertex): Float = x * other.x + y * other.y + z * other.z

	inline fun normalize(): Vertex = Vertex(x / magnitude, y / magnitude, z / magnitude)
}

data class Face(
	val vertices: Array<Vertex>,
	val normals: Array<Vertex>,
	val textures: Array<Vertex>,
	var depthArr: FloatArray?,
	var poliNormal: Vertex
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}

		other as Face

		if (!vertices.contentEquals(other.vertices)) {
			return false
		}
		if (!normals.contentEquals(other.normals)) {
			return false
		}
		if (!textures.contentEquals(other.textures)) {
			return false
		}
		if (!depthArr.contentEquals(other.depthArr)) {
			return false
		}
		if (poliNormal != other.poliNormal) {
			return false
		}

		return true
	}

	override fun hashCode(): Int {
		var result = vertices.contentHashCode()
		result = 31 * result + normals.contentHashCode()
		result = 31 * result + textures.contentHashCode()
		result = 31 * result + depthArr.contentHashCode()
		result = 31 * result + poliNormal.hashCode()
		return result
	}

	inline fun getBarycentricCoords(x: Int, y: Int): FloatArray {
		val barycentricCoordinates = FloatArray(3)

		val x1 = vertices[0].x
		val y1 = vertices[0].y
		val x2 = vertices[1].x
		val y2 = vertices[1].y
		val x3 = vertices[2].x
		val y3 = vertices[2].y

		val denominator = ((y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3))

		barycentricCoordinates[0] = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator
		barycentricCoordinates[1] = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator
		barycentricCoordinates[2] = 1 - barycentricCoordinates[0] - barycentricCoordinates[1]

		return barycentricCoordinates
	}
}

data class RGB(val r: Int, val g: Int, val b: Int)