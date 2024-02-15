package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

private val chunks: Int = faces.size / 1000
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val white: Color = Color(255, 255, 255, 255)
private val black: Color = Color(0, 0, 0, 255)

fun renderObject() {
	fillBackground(white)

	memScoped {
		val params = Array(chunks) {
			alloc<IntVar>()
		}

		params.forEachIndexed { index, param -> param.value = index }

		val threads = Array(chunks) {
			CreateThread(null, 0u, staticCFunction(::drawerThread), params[it].ptr, 0u, null)
		}

		for (thread in threads) {
			WaitForSingleObject(thread, INFINITE)
			CloseHandle(thread)
		}
	}
}

private fun drawerThread(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value

	for ((faceVertices, _, _) in splitFaces[parameter!!]) {
		if (faceVertices.size >= 3) {
			var previousVertex = vertices[faceVertices.last()].transform()

			for (i in faceVertices) {
				val currentVertex = vertices[i].transform()
				drawLineDDA(previousVertex, currentVertex, black)
				previousVertex = currentVertex
			}

			val firstVertex = vertices[faceVertices.first()].transform()
			drawLineDDA(previousVertex, firstVertex, black)
		}
	}
	return 0u
}

private fun drawLineDDA(v1: Vertex, v2: Vertex, color: Color) {
	var x = v1.x
	var y = v1.y

	val dx = v2.x - x
	val dy = v2.y - y
	val steps = max(abs(dx), abs(dy)).toInt()

	val xIncrement = dx / steps
	val yIncrement = dy / steps

	for (i in 0..steps step 2) {
		if (!(x > width - 1 || x < 0 || y > height - 1 || y < 0)) {
			val offset = (round(y).toInt() * width + round(x).toInt()) shl 2

			bitmapData[offset + 0] = color.blue
			bitmapData[offset + 1] = color.green
			bitmapData[offset + 2] = color.red
			bitmapData[offset + 3] = color.alpha
		}

		x += xIncrement
		y += yIncrement
	}
}

private fun fillBackground(color: Color) {
	for (y in 0 until height) {
		for (x in 0 until width) {
			val offset = (y * width + x) * 4
			bitmapData[offset + 0] = color.blue
			bitmapData[offset + 1] = color.green
			bitmapData[offset + 2] = color.red
			bitmapData[offset + 3] = color.alpha
		}
	}
}

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}