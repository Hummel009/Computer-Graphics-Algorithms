package com.github.hummel.cga.lab2

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

private val vertices: MutableList<Vertex> = ArrayList()

fun parse(fileName: String) {
	val file = fopen(fileName, "r")
	val bufferLength = 1024
	val buffer = ByteArray(bufferLength)

	while (fgets(buffer.refTo(0), bufferLength, file) != null) {
		val line = buffer.toKString()
		val array = line.trim().split("\\s+".toRegex()).toTypedArray()

		when (array[0]) {
			"v" -> addVertex(array.drop(1).toTypedArray())
			"f" -> addFace(array.drop(1).toTypedArray())
		}
	}

	fclose(file)
}

private fun addVertex(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		3 -> Vertex(coords[0], coords[1], coords[2])
		4 -> Vertex(coords[0], coords[1], coords[2], coords[3])
		else -> throw Exception("Vertex error: ${array.joinToString(" ")}")
	}
	vertices.add(vertex)
}

private fun addFace(array: Array<String>) {
	val vs = mutableListOf<Vertex>()
	val vns = mutableListOf<Vertex>()

	val coords = array.filter { it.isNotBlank() }

	coords.forEach { coord ->
		val elem = coord.split('/')

		elem[0].toIntOrNull()?.let { vs.add(vertices[it - 1]) } ?: run {
			vs.add(vertices[vertices.lastIndex])
		}
	}

	val vec1 = vs[1] - vs[0]
	val vec2 = vs[2] - vs[1]
	val normal = (vec2 vectorMul vec1).normalize()

	vns.add(normal)

	faces.add(Face(vs.toTypedArray(), vns.toTypedArray()))
}