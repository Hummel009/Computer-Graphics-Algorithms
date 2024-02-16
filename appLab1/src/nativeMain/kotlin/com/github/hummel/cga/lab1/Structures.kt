package com.github.hummel.cga.lab1

import kotlin.math.sqrt

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float = 1.0f) {
	private val magnitude: Float = sqrt(x * x + y * y + z * z)

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

	fun normalize(): Vertex = Vertex(x / magnitude, y / magnitude, z / magnitude)
}

data class Face(
	val vertices: MutableList<Vertex>
)

data class Color(val red: Byte, val green: Byte, val blue: Byte, val alpha: Byte) {
	constructor(red: Int, green: Int, blue: Int, alpha: Int) : this(
		red.toByte(), green.toByte(), blue.toByte(), alpha.toByte()
	)
}