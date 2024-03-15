package com.github.hummel.cga.lab1

const val kernels: Int = 8

val threadFaces: Array<List<Face>> = split(faces, kernels)

fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}