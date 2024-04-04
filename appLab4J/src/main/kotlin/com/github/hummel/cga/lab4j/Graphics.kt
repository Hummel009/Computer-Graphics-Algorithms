package com.github.hummel.cga.lab4j

private val zBuffer: FloatArray = FloatArray(windowWidth * windowHeight)

lateinit var displayMatrix: Array<FloatArray>
lateinit var lightPos: Vertex
lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	zBuffer.fill(Float.POSITIVE_INFINITY)

	faces.forEach { drawTriangle(it) }
}

private fun drawTriangle(face: Face) {
	val viewDir = -face.realVertices[0] + eyePos
	val cosAngle = face.poliNormal scalarMul viewDir

	if (cosAngle <= 0) {
		return
	}

	face.viewVertices[0] = multiplyVertexByMatrix(face.realVertices[0], displayMatrix)
	face.viewVertices[1] = multiplyVertexByMatrix(face.realVertices[1], displayMatrix)
	face.viewVertices[2] = multiplyVertexByMatrix(face.realVertices[2], displayMatrix)

	val depthArr = FloatArray(3)
	val vertices = face.viewVertices

	for (i in vertices.indices) {
		depthArr[i] = vertices[i].w
		vertices[i] divSelf vertices[i].w
	}

	face.depthArr = depthArr

	var minY = Int.MAX_VALUE
	var maxY = Int.MIN_VALUE

	face.viewVertices.forEach {
		val y = it.y.toInt()
		if (y < minY) {
			minY = y
		}
		if (y > maxY) {
			maxY = y
		}
	}

	// Создать цикл по каждой строке изображения
	for (y in minY..maxY) {
		if (y in 0 until windowHeight) {
			// Найти пересечения текущей строки с каждой из сторон треугольника
			val xIntersections = IntArray(2)
			var intersectionCount = 0
			for (i in 0..2) {
				val v0 = face.viewVertices[i]
				val v1 = face.viewVertices[(i + 1) % 3]
				val y0 = v0.y.toInt()
				val y1 = v1.y.toInt()
				if (y in y0 until y1 || y in y1 until y0) {
					val t = (y - y0) / (y1 - y0).toFloat()
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
					if (x in 0 until windowWidth) {
						val v0 = face.viewVertices[0]
						val v1 = face.viewVertices[1]
						val v2 = face.viewVertices[2]

						val coords = face.getBarycentricCoords(x, y)

						var alpha = coords[0]
						var beta = coords[1]
						var gamma = coords[2]

						face.depthArr?.let { alpha /= it[0] }
						face.depthArr?.let { beta /= it[1] }
						face.depthArr?.let { gamma /= it[2] }

						val sum = alpha + beta + gamma

						alpha /= sum
						beta /= sum
						gamma /= sum

						val zFragment = alpha * v0.z + beta * v1.z + gamma * v2.z

						if (zBuffer[x * windowHeight + y] > zFragment) {
							zBuffer[x * windowHeight + y] = zFragment

							val rgb = getResultRgb(face, alpha, beta, gamma).compose()

							bufferedImage.setRGB(x, y, rgb)
						}
					}
				}
			}
		}
	}
}