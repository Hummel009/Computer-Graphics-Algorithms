package com.github.hummel.cga.lab1

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

private const val blue: Byte = 0.toByte()
private const val green: Byte = 0.toByte()
private const val red: Byte = 0.toByte()
private const val alpha: Byte = 255.toByte()

fun drawLines() {
	for (face in faces) {
		val verticesIndices = face.vertices

		if (verticesIndices.size < 3) {
			continue
		}

		var previousVertex = vertices[verticesIndices.last() - 1].toView().toProjection().toViewport()

		for (i in verticesIndices) {
			val currentVertex = vertices[i - 1].toView().toProjection().toViewport()
			drawLineDDA(previousVertex, currentVertex)
			previousVertex = currentVertex
		}

		val firstVertex = vertices[verticesIndices.first() - 1].toView().toProjection().toViewport()
		drawLineDDA(previousVertex, firstVertex)
	}
}

private fun drawLineDDA(v1: Vertex, v2: Vertex) {
	var x = v1.x
	var y = v1.y

	val dx = v2.x - x
	val dy = v2.y - y
	val steps = max(abs(dx), abs(dy)).toInt()

	val xIncrement = dx / steps
	val yIncrement = dy / steps

	for (i in 0..steps step 2) {
		if (!(x > width - 1 || x < 0 || y > height - 1 || y < 0)) {
			val index = (round(y).toInt() * width + round(x).toInt()) shl 2

			bitmapData[index + 0] = blue // BLUE
			bitmapData[index + 1] = green // GREEN
			bitmapData[index + 2] = red // RED
			bitmapData[index + 3] = alpha // ALPHA
		}

		x += xIncrement
		y += yIncrement
	}
}