package com.github.hummel.cga.lab2

import kotlinx.cinterop.*
import platform.windows.*

private val chunks: Int = faces.size / 100
private val splitFaces: Array<List<Face>> = split(faces, chunks)
private val white: Color = Color(255, 255, 255, 255)
private val zBuffer: FloatArray = FloatArray(width * height)

fun renderObject() {
	fillBackground(white)
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
	val parameter = lpParameter?.reinterpret<IntVar>()?.pointed?.value

	val filteredList = filterTriangles(splitFaces[parameter!!])
	val drawList = applyMatrix(filteredList, displayMatrix)
	for (i in filteredList.indices) {
		val t = filteredList[i]
		val drawT = drawList[i]
		val center = t.getCenter()
		val normal = t.vertices[3].normalize()
		val ray = (center - eye - up).normalize()
		val cosAngle = normal scalarMul ray
		drawRasterTriangle(drawT, zBuffer, cosAngle)
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