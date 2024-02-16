package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*

private val chunks: Int = faces.size / 100
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val black: Color = Color(0, 0, 0, 255)
private val zBuffer: FloatArray = FloatArray(width * height)

fun renderObject() {
	fillBackground(black)
	zBuffer.fill(Float.POSITIVE_INFINITY)

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
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value!!

	val filteredFaces = filterFaces(splitFaces[parameter])

	for (face in filteredFaces) {
		val ray = (face.getCenter() - eye - up).normalize()
		val cosAngle = face.vertices[3].normalize() scalarMul ray

		val vertexList = ArrayList<Vertex>()
		face.vertices.mapTo(vertexList) { displayTransform(it) }

		drawRasterTriangle(vertexList, zBuffer, cosAngle)
	}
	return 0u
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