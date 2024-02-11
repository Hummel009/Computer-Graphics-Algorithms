package com.github.hummel.cga.lab1

import platform.windows.DWORD
import platform.windows.LPVOID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

private var n: Int = 100

@Suppress("UNUSED_PARAMETER")
fun drawLines12(lpParameter: LPVOID?): DWORD {
	for ((v11, v21, _) in faces) {
		val v1 = vertices[v11 - 1]
		val v2 = vertices[v21 - 1]

		drawLineDDA(
			(v1.x * n + 500).toInt(),
			(680 - (v1.y * n) - 550 + (n)).toInt(),
			(v2.x * n + 500).toInt(),
			(680 - (v2.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines23(lpParameter: LPVOID?): DWORD {
	for ((_, v21, v31) in faces) {
		val v2 = vertices[v21 - 1]
		val v3 = vertices[v31 - 1]

		drawLineDDA(
			(v2.x * n + 500).toInt(),
			(680 - (v2.y * n) - 550 + (n)).toInt(),
			(v3.x * n + 500).toInt(),
			(680 - (v3.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines31(lpParameter: LPVOID?): DWORD {
	for ((v11, _, v31) in faces) {
		val v1 = vertices[v11 - 1]
		val v3 = vertices[v31 - 1]

		drawLineDDA(
			(v3.x * n + 500).toInt(),
			(680 - (v3.y * n) - 550 + (n)).toInt(),
			(v1.x * n + 500).toInt(),
			(680 - (v1.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}

private fun drawLineDDA(x1: Int, y1: Int, x2: Int, y2: Int) {
	val dx = x2 - x1
	val dy = y2 - y1
	val steps = max(abs(dx), abs(dy))

	val xIncrement = dx / steps.toFloat()
	val yIncrement = dy / steps.toFloat()

	var x = x1.toFloat()
	var y = y1.toFloat()

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