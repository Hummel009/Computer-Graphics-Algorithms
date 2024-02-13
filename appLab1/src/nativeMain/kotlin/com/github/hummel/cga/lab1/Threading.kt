package com.github.hummel.cga.lab1

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

fun drawLines() {
	for ((v11, v21, v31) in faces) {
		var v1 = vertices[v11 - 1]
		var v2 = vertices[v21 - 1]
		var v3 = vertices[v31 - 1]

		v1 = v1.toView().toProjection().toViewport()
		v2 = v2.toView().toProjection().toViewport()
		v3 = v3.toView().toProjection().toViewport()

		drawLineDDA(v1, v2)
		drawLineDDA(v2, v3)
		drawLineDDA(v3, v1)
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
		if (!(x <= width - 1 && x >= 0 && y <= height - 1 && y >= 0)) {
			val index = (round(y).toInt() * width + round(x).toInt()) * 4

			bitmapData[index + 0] = 0.toByte() // BLUE
			bitmapData[index + 1] = 0.toByte() // GREEN
			bitmapData[index + 2] = 0.toByte() // RED
			bitmapData[index + 3] = 255.toByte() // ALPHA
		}

		x += xIncrement
		y += yIncrement
	}
}