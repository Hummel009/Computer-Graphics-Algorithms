package com.github.hummel.cga.lab1

fun multiplyVertexByMatrix(vertex: Vertex, matrix: Array<FloatArray>): Vertex {
	val result = FloatArray(4)

	for (i in 0 until 4) {
		result[i] = vertex.x * matrix[i][0] + vertex.y * matrix[i][1] + vertex.z * matrix[i][2] + matrix[i][3]
	}

	val w = result[3]
	return Vertex(result[0] / w, result[1] / w, result[2] / w)
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