package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

private const val blue: Byte = 0.toByte()
private const val green: Byte = 0.toByte()
private const val red: Byte = 0.toByte()
private const val alpha: Byte = 255.toByte()

private val chunks: Int = faces.size / 1000
private val splitFaces: Array<List<Face>> = split(faces, chunks)

fun drawLines() {
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

fun drawerThread(lpParameter: LPVOID?): DWORD {
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value

	for (face in splitFaces[parameter!!]) {
		val faceVertices = face.vertices
		if (faceVertices.size >= 3) {
			var previousVertex = vertices[faceVertices.last() - 1].toView().toProjection().toViewport()

			for (i in faceVertices) {
				val currentVertex = vertices[i - 1].toView().toProjection().toViewport()
				drawLineDDA(previousVertex, currentVertex)
				previousVertex = currentVertex
			}

			val firstVertex = vertices[faceVertices.first() - 1].toView().toProjection().toViewport()
			drawLineDDA(previousVertex, firstVertex)
		}
	}
	return 0u
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

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}