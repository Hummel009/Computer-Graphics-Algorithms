package com.github.hummel.cga.lab4b

class Matrix4 {
	private val data = arrayOf(
		doubleArrayOf(1.0, 0.0, 0.0, 0.0),
		doubleArrayOf(0.0, 1.0, 0.0, 0.0),
		doubleArrayOf(0.0, 0.0, 1.0, 0.0),
		doubleArrayOf(0.0, 0.0, 0.0, 1.0)
	)

	fun set(i: Int, j: Int, `val`: Double) {
		data[i][j] = `val`
	}

	fun mul(arg: Matrix4): Matrix4 {
		val res = Matrix4()
		for (i in 0..3) {
			for (j in 0..3) {
				res.data[i][j] = 0.0
				for (k in 0..3) {
					res.data[i][j] += data[i][k] * arg.data[k][j]
				}
			}
		}
		return res
	}

	fun mul(arg: Vector4): Vector4 {
		val res = Vector4()
		res[3] = 0.0
		for (i in 0..3) {
			for (j in 0..3) {
				res[i] = res[i] + data[i][j] * arg[j]
			}
		}
		return res
	}
}
