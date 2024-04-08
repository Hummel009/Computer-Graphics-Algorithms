package com.github.hummel.cga.lab4b

import kotlin.math.sqrt

class Vertex {
	private val data: FloatArray

	constructor() {
		data = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	}

	constructor(x: Float, y: Float, z: Float) {
		data = floatArrayOf(x, y, z, 1.0f)
	}

	operator fun get(i: Int): Float = data[i]

	operator fun set(i: Int, `val`: Float) {
		data[i] = `val`
	}

	fun divSelf(`val`: Float) {
		for (i in 0..2) {
			data[i] /= `val`
		}
	}

	fun len(): Float = sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2])

	fun normalize(): Vertex {
		val len = len()
		return Vertex(data[0] / len, data[1] / len, data[2] / len)
	}

	fun add(arg: Vertex): Vertex = Vertex(data[0] + arg.data[0], data[1] + arg.data[1], data[2] + arg.data[2])

	fun subtract(arg: Vertex): Vertex = Vertex(data[0] - arg.data[0], data[1] - arg.data[1], data[2] - arg.data[2])

	fun dot(arg: Vertex): Float = data[0] * arg.data[0] + data[1] * arg.data[1] + data[2] * arg.data[2]

	fun mul(arg: Float): Vertex = Vertex(data[0] * arg, data[1] * arg, data[2] * arg)

	operator fun div(arg: Float): Vertex = Vertex(data[0] / arg, data[1] / arg, data[2] / arg)

	fun cross(arg: Vertex): Vertex {
		return Vertex(
			data[1] * arg.data[2] - data[2] * arg.data[1],
			data[2] * arg.data[0] - data[0] * arg.data[2],
			data[0] * arg.data[1] - data[1] * arg.data[0]
		)
	}
}
