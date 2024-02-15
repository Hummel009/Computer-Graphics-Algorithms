package com.github.hummel.cga.lab2

import kotlin.math.abs

fun getCenter(triangle: Array<Vertex>): Vertex {
	var sum = Vertex(0.0f, 0.0f, 0.0f)
	for (i in 0..2) {
		sum += triangle[i]
	}
	return sum.div(3.0f)
}

fun applyMatrix(triangles: List<Array<Vertex>>, matrix: Array<FloatArray>): List<Array<Vertex>> {
	return triangles.map { triangle ->
		triangle.map { vertex ->
			val result = multiplyVertexByMatrix(vertex, matrix)
			result
		}.toTypedArray()
	}
}

fun addNormals(triangles: List<Array<Vertex>>): List<Array<Vertex?>> {
	return triangles.map { t ->
		val vec1 = t[1] - t[0]
		val vec2 = t[2] - t[1]
		val normal = (vec2 vectorMul vec1).normalize()
		val newArr = t.copyOf(4)
		newArr[3] = normal
		newArr
	}
}

fun filterTriangles(triangles: List<Array<Vertex>>): List<Array<Vertex>> {
	return triangles.filter { t ->
		val viewDir = (t[0] - eye).normalize()
		val cos = t[3] scalarMul viewDir
		cos > 0
	}
}

fun drawRasterTriangle(triangle: Array<Vertex>, zBuffer: FloatArray, cosAngle: Float) {
	val colorVal = (0xff * abs(cosAngle)).toInt()
	val color = Color(colorVal, colorVal, colorVal, 255)

	var minY = Int.MAX_VALUE
	var maxY = Int.MIN_VALUE
	for (vertex in triangle) {
		val y = vertex.y.toInt()
		if (y < minY) {
			minY = y
		}
		if (y > maxY) {
			maxY = y
		}
	}

	for (y in minY..maxY) {
		val xIntersections = IntArray(2)
		var intersectionCount = 0
		for (i in 0..2) {
			val v0 = triangle[i]
			val v1 = triangle[(i + 1) % 3]
			val y0 = v0.y.toInt()
			val y1 = v1.y.toInt()
			if ((y0 <= y && y < y1) || (y1 <= y && y < y0)) {
				val t = (y - y0) / (y1 - y0).toDouble()
				val x = (v0.x + t * (v1.x - v0.x)).toInt()
				xIntersections[intersectionCount] = x
				intersectionCount++
			}
		}

		if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
			val temp = xIntersections[0]
			xIntersections[0] = xIntersections[1]
			xIntersections[1] = temp
		}

		if (intersectionCount == 2) {
			for (x in xIntersections[0]..xIntersections[1]) {
				val v0 = triangle[0]
				val v1 = triangle[1]
				val v2 = triangle[2]
				val alpha =
					((v1.y - v2.y) * (x - v2.x) + (v2.x - v1.x) * (y - v2.y)) / ((v1.y - v2.y) * (v0.x - v2.x) + (v2.x - v1.x) * (v0.y - v2.y))
				val beta =
					((v2.y - v0.y) * (x - v2.x) + (v0.x - v2.x) * (y - v2.y)) / ((v1.y - v2.y) * (v0.x - v2.x) + (v2.x - v1.x) * (v0.y - v2.y))
				val gamma = 1 - alpha - beta
				val zFragment = alpha * v0.z + beta * v1.z + gamma * v2.z

				if (zBuffer[x * height + y] > zFragment) {
					zBuffer[x * height + y] = zFragment
					val offset = (y * width + x) * 4
					bitmapData[offset + 0] = color.blue
					bitmapData[offset + 1] = color.green
					bitmapData[offset + 2] = color.red
					bitmapData[offset + 3] = color.alpha
				}
			}
		}
	}
}