package com.github.hummel.cga.lab4

import kotlinx.cinterop.refTo
import platform.posix.*

const val kernels: Int = 8

val threadFaces: Array<List<Face>> = split(faces, kernels)

fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}

inline fun readBytesFromFile(filePath: String): ByteArray {
	val file = fopen(filePath, "rb") ?: throw Exception()

	fseek(file, 0, SEEK_END)
	val fileSize = ftell(file)
	fseek(file, 0, SEEK_SET)

	val buffer = ByteArray(fileSize)
	fread(buffer.refTo(0), fileSize.toULong(), 1u, file)

	fclose(file)

	return buffer
}