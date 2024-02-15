package com.github.hummel.cga.lab2.math

import kotlin.math.sqrt

class Vector4 {
	private val data: DoubleArray

	constructor() {
		data = doubleArrayOf(0.0, 0.0, 0.0, 1.0)
	}

	constructor(x: Double, y: Double, z: Double) {
		data = doubleArrayOf(x, y, z, 1.0)
	}

	constructor(data: DoubleArray) {
		this.data = data.copyOf(data.size)
	}

	operator fun get(i: Int): Double {
		return data[i]
	}

	operator fun set(i: Int, `val`: Double) {
		data[i] = `val`
	}

	fun divSelf(`val`: Double) {
		for (i in 0..3) {
			data[i] /= `val`
		}
	}

	private fun len(): Double {
		return sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2])
	}

	fun normalize(): Vector4 {
		val len = len()
		return Vector4(data[0] / len, data[1] / len, data[2] / len)
	}

	fun add(arg: Vector4?): Vector4 {
		return Vector4(data[0] + arg!!.data[0], data[1] + arg.data[1], data[2] + arg.data[2])
	}

	fun subtract(arg: Vector4?): Vector4 {
		return Vector4(data[0] - arg!!.data[0], data[1] - arg.data[1], data[2] - arg.data[2])
	}

	fun dot(arg: Vector4?): Double {
		return data[0] * arg!!.data[0] + data[1] * arg.data[1] + data[2] * arg.data[2]
	}

	fun mul(arg: Double): Vector4 {
		return Vector4(data[0] * arg, data[1] * arg, data[2] * arg)
	}

	fun div(arg: Double): Vector4 {
		return Vector4(data[0] / arg, data[1] / arg, data[2] / arg)
	}

	fun cross(arg: Vector4?): Vector4 {
		return Vector4(
			data[1] * arg!!.data[2] - data[2] * arg.data[1],
			data[2] * arg.data[0] - data[0] * arg.data[2],
			data[0] * arg.data[1] - data[1] * arg.data[0]
		)
	}
}
