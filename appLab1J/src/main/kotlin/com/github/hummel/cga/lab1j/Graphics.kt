package com.github.hummel.cga.lab1j

import kotlin.math.abs

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
	val drawFace = Face(
		arrayOf(
			multiplyVertexByMatrix(face.vertices[0], displayMatrix),
			multiplyVertexByMatrix(face.vertices[1], displayMatrix),
			multiplyVertexByMatrix(face.vertices[2], displayMatrix)
		), face.normals, face.textures, face.depthArr, face.poliNormal
	)

	val color = (255 shl 16) or (255 shl 8) or 255

	drawLine(drawFace.vertices[0], drawFace.vertices[1], color)
	drawLine(drawFace.vertices[1], drawFace.vertices[2], color)
	drawLine(drawFace.vertices[2], drawFace.vertices[0], color)
}

private fun drawLine(v1: Vertex, v2: Vertex, color: Int) {
	var x1 = v1.x.toInt()
	val x2 = v2.x.toInt()
	var y1 = v1.y.toInt()
	val y2 = v2.y.toInt()

	val dx = abs(x2 - x1)
	val dy = abs(y2 - y1)
	val sx = if (x1 < x2) 1 else -1
	val sy = if (y1 < y2) 1 else -1
	var err = dx - dy

	while (x1 != x2 || y1 != y2) {
		if (x1 in 0 until hWidth && y1 in 0 until hHeight) {
			bufferedImage.setRGB(x1, y1, color)
		}

		val err2 = 2 * err

		if (err2 > -dy) {
			err -= dy
			x1 += sx
		}

		if (err2 < dx) {
			err += dx
			y1 += sy
		}
	}
}