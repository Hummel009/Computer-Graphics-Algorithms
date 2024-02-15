package com.github.hummel.cga.lab2.math

import com.github.hummel.cga.lab2.Camera
import com.github.hummel.cga.lab2.Main
import java.awt.image.BufferedImage
import kotlin.math.abs

class AlgoUtils {
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

	fun drawRasterTriangle1(bufferedImage: BufferedImage, triangle: Array<Vertex>, zBuffer: DoubleArray?) {
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
				if (y0 <= y && y < y1 || y1 <= y && y < y0) {
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
//                    if (zBuffer[Main.width*x+y] < )
					bufferedImage.setRGB(x, y, 0x0000ff00)
				}
			}
		}
	}

	companion object {
		fun getCenter(triangle: Array<Vertex?>?): Vertex? {
			var sum: Vertex? = Vertex(0.0, 0.0, 0.0)
			for (i in 0..2) {
				sum = sum!!.add(triangle!![i])
			}
			return sum!!.div(3.0)
		}

		fun applyMatrix(triangles: Iterable<Array<Vertex?>?>, matrix: Matrix4?): List<Array<Vertex?>> {
			val list: MutableList<Array<Vertex?>> = ArrayList()
			for (triangle in triangles) {
				val list1: MutableList<Vertex?> = ArrayList()
				for (Vertex1 in triangle!!) {
					var result = matrix!!.mul(Vertex1)
					result = result!!.div(result[3])
					val apply = result
					list1.add(apply)
				}
				val array = list1.toTypedArray<Vertex?>()
				list.add(array)
			}
			return list
		}

		fun addNormals(triangles: Iterable<Array<Vertex?>?>): MutableList<Array<Vertex?>?> {
			val list: MutableList<Array<Vertex?>?> = ArrayList()
			for (triangle in triangles) {
				val vec1 = triangle!![1]!!.subtract(triangle[0])
				val vec2 = triangle[2]!!.subtract(triangle[1])
				val normal = vec2!!.cross(vec1).normalize()
				val newArr = triangle.copyOf(4)
				newArr[3] = normal
				list.add(newArr)
			}
			return list
		}

		fun filterTriangles(triangles: Iterable<Array<Vertex?>?>, camera: Camera): List<Array<Vertex?>?> {
			val list: MutableList<Array<Vertex?>?> = ArrayList()
			for (triangle in triangles) {
				val viewDir = triangle!![0]!!.subtract(camera.eye).normalize()
				val cos = triangle[3]!!.dot(viewDir)
				if (cos > 0) {
					list.add(triangle)
				}
			}
			return list
		}

		fun drawRasterTriangle(
			bufferedImage: BufferedImage, triangle: Array<Vertex?>, zBuffer: DoubleArray, cosAngle: Double
		) {
			val colorVal = (0xff * abs(cosAngle)).toInt()
			val color = colorVal shl 16 or (colorVal shl 8) or colorVal

			var minY = Int.MAX_VALUE
			var maxY = Int.MIN_VALUE
			for (vertex in triangle) {
				val y = vertex!![1].toInt()
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
					val y0 = v0!![1].toInt()
					val y1 = v1!![1].toInt()
					if (y0 <= y && y < y1 || y1 <= y && y < y0) {
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
							((v1!![1] - v2!![1]) * (x - v2[0]) + (v2[0] - v1[0]) * (y - v2[1])) / ((v1[1] - v2[1]) * (v0!![0] - v2[0]) + (v2[0] - v1[0]) * (v0[1] - v2[1]))
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
	}
}
