package com.github.hummel.cga.lab3j

val zBuffer: FloatArray = FloatArray(hWidth * hHeight)

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
	val viewDir = -face.vertices[0] + eyePos
	val cosAngle = (face.poliNormal / face.normals.size.toFloat()) scalarMul viewDir

	if (cosAngle <= 0) {
		return
	}

	val drawFace = Face(
		arrayOf(
			multiplyVertexByMatrix(face.vertices[0], displayMatrix),
			multiplyVertexByMatrix(face.vertices[1], displayMatrix),
			multiplyVertexByMatrix(face.vertices[2], displayMatrix)
		), face.normals, face.textures, face.depthArr, face.poliNormal
	)

	var minY = Int.MAX_VALUE
	var maxY = Int.MIN_VALUE

	for (vertex in drawFace.vertices) {
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
		if (y in 0 until hHeight) {
			// Найти пересечения текущей строки с каждой из сторон треугольника
			val xIntersections = IntArray(2)
			var intersectionCount = 0
			for (i in 0..2) {
				val v0 = drawFace.vertices[i]
				val v1 = drawFace.vertices[(i + 1) % 3]
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
					if (x in 0 until hWidth) {
						val v0 = drawFace.vertices[0]
						val v1 = drawFace.vertices[1]
						val v2 = drawFace.vertices[2]

						val barycCords = drawFace.getBarycentricCoordinates(x, y)

						var alpha = barycCords[0]
						var beta = barycCords[1]
						var gamma = barycCords[2]
						drawFace.depthArr?.let { alpha /= it[0] }
						drawFace.depthArr?.let { beta /= it[1] }
						drawFace.depthArr?.let { gamma /= it[2] }
						val sum = alpha + beta + gamma
						alpha /= sum
						beta /= sum
						gamma /= sum

						val zFragment = alpha * v0.z + beta * v1.z + gamma * v2.z

						if (zBuffer[x * hHeight + y] > zFragment) {
							zBuffer[x * hHeight + y] = zFragment

							val color = getShading(face, alpha, beta, gamma)

							bufferedImage.setRGB(x, y, color)
						}
					}
				}
			}
		}
	}
}