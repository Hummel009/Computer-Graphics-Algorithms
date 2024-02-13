package com.github.hummel.cga.lab1

import kotlin.math.*

private var eye: Vertex = Vertex(0.0f, 0.0f, 10.0f)
private var target: Vertex = Vertex(0.0f, 0.0f, 0.0f)
private var up: Vertex = Vertex(0.0f, 1.0f, 0.0f)

private var fov: Float = PI.toFloat() / 4.0f
private var aspect = width.toFloat() / height.toFloat()

private var zNear: Float = 1.0f
private var zFar: Float = 100.0f

private val zAxis = (eye - target).normalize()
private val xAxis = (up vectorMul zAxis).normalize()
private val yAxis = xAxis vectorMul zAxis

private val matrixView: Array<FloatArray> = arrayOf(
	floatArrayOf(xAxis.x, xAxis.y, xAxis.z, -(xAxis scalarMul eye)),
	floatArrayOf(yAxis.x, yAxis.y, yAxis.z, -(yAxis scalarMul eye)),
	floatArrayOf(zAxis.x, zAxis.y, zAxis.z, -(zAxis scalarMul eye)),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

private val matrixProjection: Array<FloatArray> = arrayOf(
	floatArrayOf(1.0f / (aspect * tan(fov / 2.0f)), 0.0f, 0.0f, 0.0f),
	floatArrayOf(0.0f, 1.0f / (tan(fov / 2.0f)), 0.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, zFar / (zNear - zFar), (zNear * zFar) / (zNear - zFar)),
	floatArrayOf(0.0f, 0.0f, -1.0f, 0.0f)
)

private val matrixViewport: Array<FloatArray> = arrayOf(
	floatArrayOf(width.toFloat() / 2.0f, 0.0f, 0.0f, width.toFloat() / 2.0f),
	floatArrayOf(0.0f, height.toFloat() / 2.0f, 0.0f, height.toFloat() / 2.0f),
	floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f),
	floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
)

data class Vertex(var x: Float, var y: Float, var z: Float, var w: Float) {
	private val magnitude: Float = sqrt(x * x + y * y + z * z)

	constructor(x: Float, y: Float, z: Float) : this(x, y, z, 1.0f)

	operator fun minus(float: Float): Vertex = Vertex(x - float, y - float, z - float)

	operator fun plus(float: Float): Vertex = Vertex(x + float, y + float, z + float)

	operator fun times(float: Float): Vertex = Vertex(x * float, y * float, z * float)

	operator fun div(float: Float): Vertex = Vertex(x / float, y / float, z / float)

	operator fun minus(other: Vertex): Vertex = Vertex(x - other.x, y - other.y, z - other.z)

	operator fun plus(other: Vertex): Vertex = Vertex(x + other.x, y + other.y, z + other.z)

	infix fun vectorMul(other: Vertex): Vertex {
		val crossX = y * other.z - z * other.y
		val crossY = z * other.x - x * other.z
		val crossZ = x * other.y - y * other.x
		return Vertex(crossX, crossY, crossZ)
	}

	infix fun scalarMul(other: Vertex): Float = x * other.x + y * other.y + z * other.z

	fun toView(): Vertex = multiplyByMatrix(matrixView)

	fun toProjection(): Vertex = multiplyByMatrix(matrixProjection)

	fun toViewport(): Vertex = multiplyByMatrix(matrixViewport)

	fun normalize(): Vertex = Vertex(x / magnitude, y / magnitude, z / magnitude)

	fun getMagnitude(): Float = magnitude

	private fun multiplyByMatrix(matrix: Array<FloatArray>): Vertex {
		val result = FloatArray(4)

		for (i in 0 until 4) {
			result[i] = x * matrix[i][0] + y * matrix[i][1] + z * matrix[i][2] + matrix[i][3]
		}

		val w = result[3]
		return Vertex(result[0] / w, result[1] / w, result[2] / w)
	}
}

data class Face(val vertices: List<Int>, val textures: List<Int>, val normals: List<Int>)

data class LineSegment(val left: Vertex, val right: Vertex)

data class PointLight(var x: Float, var y: Float, var z: Float, var intency: Float) {
	fun calculateLight(point: Vertex, normal: Vertex): Float {
		val l = Vertex(x, y, z) - point

		var lightResult = 0.0f
		val angle = normal scalarMul l

		if (angle > 0) {
			lightResult = intency * angle / (l.getMagnitude() * normal.getMagnitude())
		}

		return lightResult
	}
}

data class Triangle(val a: Vertex, val b: Vertex, val c: Vertex) {
	fun getHorizontalLines(): Collection<LineSegment> {
		val minY = min(min(a.y, b.y), c.y)
		val maxY = max(max(a.y, b.y), c.y)

		val results = mutableListOf<LineSegment>()

		var y = minY
		while (y <= maxY) {
			results.add(findIntersectingSegmentY(a, b, c, y))
			y += 1.0f
		}

		return results
	}

	fun getVerticalLines(): Collection<LineSegment> {
		val minX = min(min(a.x, b.x), c.x)
		val maxX = max(max(a.x, b.x), c.x)

		val results = mutableListOf<LineSegment>()

		var x = minX
		while (x <= maxX) {
			results.add(findIntersectingSegmentX(a, b, c, x))
			x += 1.0f
		}

		return results
	}

	private fun findIntersectingSegmentX(point1: Vertex, point2: Vertex, point3: Vertex, x: Float): LineSegment {
		val trianglePoints = arrayOf(point1, point2, point3)
		var leftPoint = Vertex(0.0f, 0.0f, 0.0f)
		var rightPoint = Vertex(0.0f, 0.0f, 0.0f)

		for (i in 0 until 3) {
			val currentPoint = trianglePoints[i]
			val nextPoint = trianglePoints[(i + 1) % 3]

			if ((currentPoint.x <= x && nextPoint.x >= x) || (currentPoint.x >= x && nextPoint.x <= x)) {
				val t = (x - currentPoint.x) / (nextPoint.x - currentPoint.x)
				val intersectionPoint = currentPoint + (nextPoint - currentPoint) * t

				if (leftPoint == Vertex(0.0f, 0.0f, 0.0f)) {
					leftPoint = intersectionPoint
				} else {
					rightPoint = intersectionPoint
					break
				}
			}
		}

		return LineSegment(leftPoint, rightPoint)
	}

	private fun findIntersectingSegmentY(point1: Vertex, point2: Vertex, point3: Vertex, y: Float): LineSegment {
		val trianglePoints = arrayOf(point1, point2, point3)
		var leftPoint = Vertex(0.0f, 0.0f, 0.0f)
		var rightPoint = Vertex(0.0f, 0.0f, 0.0f)

		for (i in 0 until 3) {
			val currentPoint = trianglePoints[i]
			val nextPoint = trianglePoints[(i + 1) % 3]

			if ((currentPoint.y <= y && nextPoint.y >= y) || (currentPoint.y >= y && nextPoint.y <= y)) {
				val t = (y - currentPoint.y) / (nextPoint.y - currentPoint.y)
				val intersectionPoint = currentPoint + (nextPoint - currentPoint) * t

				if (leftPoint == Vertex(0.0f, 0.0f, 0.0f)) {
					leftPoint = intersectionPoint
				} else {
					rightPoint = intersectionPoint
					break
				}
			}
		}

		return LineSegment(leftPoint, rightPoint)
	}
}