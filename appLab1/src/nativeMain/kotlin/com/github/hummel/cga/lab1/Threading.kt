package com.github.hummel.cga.lab1

import platform.windows.DWORD
import platform.windows.LPVOID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

@Suppress("UNUSED_PARAMETER")
fun drawLines12(lpParameter: LPVOID?): DWORD {
	for ((v11, v21, _) in faces) {
		var v1 = vertices[v11 - 1]
		var v2 = vertices[v21 - 1]

		v1 = v1.toView().toProjection().toViewport()
		v2 = v2.toView().toProjection().toViewport()

		drawLineDDA(v1, v2)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines23(lpParameter: LPVOID?): DWORD {
	for ((_, v21, v31) in faces) {
		var v2 = vertices[v21 - 1]
		var v3 = vertices[v31 - 1]

		v2 = v2.toView().toProjection().toViewport()
		v3 = v3.toView().toProjection().toViewport()

		drawLineDDA(v2, v3)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines31(lpParameter: LPVOID?): DWORD {
	for ((v11, _, v31) in faces) {
		var v1 = vertices[v11 - 1]
		var v3 = vertices[v31 - 1]

		v1 = v1.toView().toProjection().toViewport()
		v3 = v3.toView().toProjection().toViewport()

		drawLineDDA(v3, v1)
	}
	return 0u
}

private fun drawLineDDA(v1: Vertex, v2: Vertex) {
	val dx = v2.x - v1.x
	val dy = v2.y - v1.y
	val steps = max(abs(dx), abs(dy)).toInt()

	val xIncrement = dx / steps
	val yIncrement = dy / steps

	var x = v1.x
	var y = v1.y

	for (i in 0..steps step 2) {
		//IF THE OBJECT IS OUT OF BOUNDS, IT SHOULDN'T BE DISPLAYED
		if (x > width - 1 || x < 0 || y > height - 1 || y < 0) {
			x += xIncrement
			y += yIncrement
		} else {
			val index = (round(y).toInt() * width + round(x).toInt()) * 4

			bitmapData[index + 0] = 0.toByte() // BLUE
			bitmapData[index + 1] = 0.toByte() // GREEN
			bitmapData[index + 2] = 0.toByte() // RED
			bitmapData[index + 3] = 255.toByte() // ALPHA

			x += xIncrement
			y += yIncrement
		}
	}
}