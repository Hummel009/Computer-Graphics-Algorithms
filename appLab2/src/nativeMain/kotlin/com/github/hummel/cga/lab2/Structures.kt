package com.github.hummel.cga.lab2

import kotlin.math.sqrt

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float = 1.0f) {
	val magnitude: Float = sqrt(x * x + y * y + z * z)

	inline operator fun minus(float: Float): Vertex = Vertex(x - float, y - float, z - float)

	inline operator fun plus(float: Float): Vertex = Vertex(x + float, y + float, z + float)

	inline operator fun times(float: Float): Vertex = Vertex(x * float, y * float, z * float)

	inline operator fun div(float: Float): Vertex = Vertex(x / float, y / float, z / float)

	inline operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	inline operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y, z + other.z)

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
	val vertices: MutableList<Vertex>
) {
	val center: Vertex

	init {
		var sum = Vertex(0.0f, 0.0f, 0.0f)
		for (i in 0..2) {
			sum += vertices[i]
		}
		center = sum / 3.0f
	}
}

data class Color(val red: Byte, val green: Byte, val blue: Byte) {
	constructor(red: Int, green: Int, blue: Int) : this(
		red.toByte(), green.toByte(), blue.toByte()
	)
}