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
	face.viewVertices[0] = multiplyVertexByMatrix(face.realVertices[0], displayMatrix)
	face.viewVertices[1] = multiplyVertexByMatrix(face.realVertices[1], displayMatrix)
	face.viewVertices[2] = multiplyVertexByMatrix(face.realVertices[2], displayMatrix)

	for (i in face.viewVertices.indices) {
		face.viewVertices[i] divSelf face.viewVertices[i].w
	}

	drawLine(face.viewVertices[0], face.viewVertices[1], rgb)
	drawLine(face.viewVertices[1], face.viewVertices[2], rgb)
	drawLine(face.viewVertices[2], face.viewVertices[0], rgb)
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