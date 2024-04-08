package com.github.hummel.cga.lab4b

class MyMatrix {
	private val data = arrayOf(
		floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f),
		floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f),
		floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
		floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
	)

	operator fun set(i: Int, j: Int, `val`: Float) {
		data[i][j] = `val`
	}

	fun mul(arg: MyMatrix): MyMatrix {
		val res = MyMatrix()
		for (i in 0..3) {
			for (j in 0..3) {
				res.data[i][j] = 0.0f
				for (k in 0..3) {
					res.data[i][j] += data[i][k] * arg.data[k][j]
				}
			}
		}
		return res
	}

	fun mul(arg: Vertex): Vertex {
		val res = Vertex()
		res[3] = 0.0f
		for (i in 0..3) {
			for (j in 0..3) {
				res[i] = res[i] + data[i][j] * arg[j]
			}
		}
		return res
	}
}
