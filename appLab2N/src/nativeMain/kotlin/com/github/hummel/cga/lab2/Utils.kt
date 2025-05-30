@file:Suppress("NOTHING_TO_INLINE")

package com.github.hummel.cga.lab2

const val kernels: Int = 8

val threadFaces: Array<List<Face>> = split(faces, kernels)

fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}

inline fun ByteArray.setRGB(x: Int, y: Int, rgb: RGB) {
	val offset = (y * windowWidth + x) shl 2
	this[offset + 0] = rgb.b.toByte()
	this[offset + 1] = rgb.g.toByte()
	this[offset + 2] = rgb.r.toByte()
	this[offset + 3] = -1
}