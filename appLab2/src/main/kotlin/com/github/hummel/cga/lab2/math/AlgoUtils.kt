package com.github.hummel.cga.lab2.math

import com.github.hummel.cga.lab2.Main
import com.github.hummel.cga.lab2.hum.*
import java.awt.image.BufferedImage
import kotlin.math.abs

fun drawLine(image: BufferedImage, x1: Int, y1: Int, x2: Int, y2: Int) {
	var x11 = x1
	var y11 = y1
	val width = image.width
	val height = image.height

	val dx = abs((x2 - x11).toDouble()).toInt()
	val dy = abs((y2 - y11).toDouble()).toInt()
	val sx = if (x11 < x2) 1 else -1
	val sy = if (y11 < y2) 1 else -1
	var err = dx - dy

	while (true) {
		if (x11 >= 0 && x11 < width && y11 >= 0 && y11 < height) {
			image.setRGB(x11, y11, -0xff0100)
		}

		if (x11 == x2 && y11 == y2) {
			break
		}

		val err2 = 2 * err

		if (err2 > -dy) {
			err -= dy
			x11 += sx
		}

		if (err2 < dx) {
			err += dx
			y11 += sy
		}
	}
}

fun getCenter(face: Face): Vertex {
	var sum: Vertex = Vertex(0.0, 0.0, 0.0)
	for (i in 0..2) {
		sum = sum.add(face.vertices[i])
	}
	return sum.div(3.0)
}

fun applyMatrix(triangles: Iterable<Face>, matrix: Array<FloatArray>): List<Array<Vertex>> {
	val list: MutableList<Array<Vertex>> = ArrayList()
	for (triangle in triangles) {
		val list1: MutableList<Vertex> = ArrayList()
		for (Vertex1 in triangle.vertices) {
			var result = multiplyVertexByMatrix(Vertex1, matrix)
			val apply = result
			list1.add(apply)
		}
		val array = list1.toTypedArray<Vertex>()
		list.add(array)
	}
	return list
}

fun addNormals(triangles: Iterable<Face>): MutableList<Face> {
	val list: MutableList<Face> = ArrayList()
	for (face in triangles) {
		val vec1 = face.vertices[1].subtract(face.vertices[0])
		val vec2 = face.vertices[2].subtract(face.vertices[1])
		val normal = vec2.cross(vec1).normalize()
		val newFace = Face(
			mutableListOf(face.vertices[0], face.vertices[1], face.vertices[2], normal),
			mutableListOf(),
			mutableListOf()
		)
		list.add(newFace)
	}
	return list
}

fun filterTriangles(triangles: Iterable<Face>): List<Face> {
	val list: MutableList<Face> = ArrayList()
	for (face in triangles) {
		val viewDir = face.vertices[0].subtract(eye).normalize()
		val cos = face.vertices[3].dot(viewDir)
		if (cos > 0) {
			list.add(face)
		}
	}
	return list
}

fun drawRasterTriangle(
	bufferedImage: BufferedImage, triangle: Array<Vertex>, zBuffer: DoubleArray, cosAngle: Double
) {
	val colorVal = (0xff * abs(cosAngle)).toInt()
	val color = colorVal shl 16 or (colorVal shl 8) or colorVal

	var minY = Int.MAX_VALUE
	var maxY = Int.MIN_VALUE
	for (vertex in triangle) {
		val y = vertex[1].toInt()
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
			val y0 = v0[1].toInt()
			val y1 = v1[1].toInt()
			if (y in y0 until y1 || y in y1 until y0) {
				val t = (y - y0) / (y1 - y0).toDouble()
				val x = (v0[0] + t * (v1[0] - v0[0])).toInt()
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
				// Вычисление z-фрагмента
				val v0 = triangle[0]
				val v1 = triangle[1]
				val v2 = triangle[2]
				val alpha =
					((v1[1] - v2[1]) * (x - v2[0]) + (v2[0] - v1[0]) * (y - v2[1])) / ((v1[1] - v2[1]) * (v0[0] - v2[0]) + (v2[0] - v1[0]) * (v0[1] - v2[1]))
				val beta =
					((v2[1] - v0[1]) * (x - v2[0]) + (v0[0] - v2[0]) * (y - v2[1])) / ((v1[1] - v2[1]) * (v0[0] - v2[0]) + (v2[0] - v1[0]) * (v0[1] - v2[1]))
				val gamma = 1 - alpha - beta
				val zFragment = alpha * v0[2] + beta * v1[2] + gamma * v2[2]

				// Проверка z-буфера
				if (zBuffer[x * Main.height + y] > zFragment) {
					zBuffer[x * Main.height + y] = zFragment.toDouble()
					bufferedImage.setRGB(x, y, color)
				}
			}
		}
	}
}