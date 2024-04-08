package com.github.hummel.cga.lab4b

import kotlin.math.sqrt

class Vertex {
	private val data: DoubleArray

	constructor() {
		data = doubleArrayOf(0.0, 0.0, 0.0, 1.0)
	}

	constructor(x: Double, y: Double, z: Double) {
		data = doubleArrayOf(x, y, z, 1.0)
	}

	operator fun get(i: Int): Double = data[i]

	operator fun set(i: Int, `val`: Double) {
		data[i] = `val`
	}

	fun divSelf(`val`: Double) {
		for (i in 0..2) {
			data[i] /= `val`
		}
	}

	fun len(): Double = sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2])

	fun normalize(): Vertex {
		val len = len()
		return Vertex(data[0] / len, data[1] / len, data[2] / len)
	}

	fun add(arg: Vertex): Vertex = Vertex(data[0] + arg.data[0], data[1] + arg.data[1], data[2] + arg.data[2])

	fun subtract(arg: Vertex): Vertex = Vertex(data[0] - arg.data[0], data[1] - arg.data[1], data[2] - arg.data[2])

	fun dot(arg: Vertex): Double = data[0] * arg.data[0] + data[1] * arg.data[1] + data[2] * arg.data[2]

	fun mul(arg: Double): Vertex = Vertex(data[0] * arg, data[1] * arg, data[2] * arg)

	operator fun div(arg: Double): Vertex = Vertex(data[0] / arg, data[1] / arg, data[2] / arg)

	fun cross(arg: Vertex): Vertex {
		return Vertex(
			data[1] * arg.data[2] - data[2] * arg.data[1],
			data[2] * arg.data[0] - data[0] * arg.data[2],
			data[0] * arg.data[1] - data[1] * arg.data[0]
		)
	}
}
