package com.github.hummel.cga.lab2j

fun multiplyVertexByMatrix(vertex: Vertex, matrix: Array<FloatArray>): Vertex {
	val result = FloatArray(4)
	val vectorData = floatArrayOf(vertex.x, vertex.y, vertex.z, vertex.w)

	for (i in 0 until 4) {
		for (j in 0 until 4) {
			result[i] += matrix[i][j] * vectorData[j]
		}
	}

	return Vertex(result[0], result[1], result[2], result[3])
}

fun multiplyMatrixByMatrix(matrixA: Array<FloatArray>, matrixB: Array<FloatArray>): Array<FloatArray> {
	val result = Array(4) { FloatArray(4) }

	for (i in 0 until 4) {
		for (j in 0 until 4) {
			val sum = (0 until 4).asSequence().map { matrixA[i][it] * matrixB[it][j] }.sum()
			result[i][j] = sum
		}
	}

	return result
}