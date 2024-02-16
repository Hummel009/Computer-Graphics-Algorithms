package com.github.hummel.cga.lab2

import kotlin.math.abs

fun addNormals(faces: Collection<Face>): MutableList<Face> {
	val list = ArrayList<Face>()
	for ((vertices, _, _) in faces) {
		val vec1 = vertices[1] - vertices[0]
		val vec2 = vertices[2] - vertices[1]
		val normal = (vec2 vectorMul vec1).normalize()
		val newFace = Face(
			mutableListOf(vertices[0], vertices[1], vertices[2], normal), mutableListOf(), mutableListOf()
		)
		list.add(newFace)
	}
	return list
}

fun filterTriangles(faces: Collection<Face>): MutableList<Face> {
	val list = ArrayList<Face>()
	for (face in faces) {
		val viewDir = (face.vertices[0] - eye).normalize()
		val cos = face.vertices[3] scalarMul viewDir
		if (cos > 0) {
			list.add(face)
		}
	}
	return list
}

fun drawRasterTriangle(
	triangle: ArrayList<Vertex>, zBuffer: FloatArray, cosAngle: Float
) {
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

	// Создать цикл по каждой строке изображения
	for (y in minY..maxY) {
		// Найти пересечения текущей строки с каждой из сторон треугольника
		val xIntersections = IntArray(2)
		var intersectionCount = 0
		for (i in 0..2) {
			val v0 = triangle[i]
			val v1 = triangle[(i + 1) % 3]
			val y0 = v0.y.toInt()
			val y1 = v1.y.toInt()
			if (y in y0 until y1 || y in y1 until y0) {
				val t = (y - y0) / (y1 - y0).toDouble()
				val x = (v0.x + t * (v1.x - v0.x)).toInt()
				xIntersections[intersectionCount] = x
				intersectionCount++
			}
		}

		// Отсортировать пересечения по возрастанию
		if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
			val temp = xIntersections[0]
			xIntersections[0] = xIntersections[1]
			xIntersections[1] = temp
		}

		// Заполнить пиксели между пересечениями цветом треугольника
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

				// Проверка z-буфера
				if (zBuffer[x * height + y] > zFragment) {
					zBuffer[x * height + y] = zFragment

					if (x in 0 until width && y in 0 until height) {
						val offset = (y * width + x) shl 2

						bitmapData[offset + 0] = color.blue
						bitmapData[offset + 1] = color.green
						bitmapData[offset + 2] = color.red
						bitmapData[offset + 3] = color.alpha
					}
				}
			}
		}
	}
}