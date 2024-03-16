package com.github.hummel.cga.lab1j

import kotlin.math.abs

private val rgb: Int = RGB(255, 255, 255).compose()

private lateinit var displayMatrix: Array<FloatArray>
private lateinit var lightPos: Vertex
private lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

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

	drawLine(drawFace.vertices[0], drawFace.vertices[1], rgb)
	drawLine(drawFace.vertices[1], drawFace.vertices[2], rgb)
	drawLine(drawFace.vertices[2], drawFace.vertices[0], rgb)
}

private fun drawLine(v1: Vertex, v2: Vertex, rgb: Int) {
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
		if (x1 in 0 until windowWidth && y1 in 0 until windowHeight) {
			bufferedImage.setRGB(x1, y1, rgb)
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