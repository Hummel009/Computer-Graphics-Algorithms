package com.github.hummel.cga.lab2

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

fun parse(fileName: String) {
	val file = fopen(fileName, "r")
	val bufferLength = 1024
	val buffer = ByteArray(bufferLength)

	while (fgets(buffer.refTo(0), bufferLength, file) != null) {
		val line = buffer.toKString()
		val array = line.trim().split("\\s+".toRegex()).toTypedArray()

		when (array[0]) {
			"v" -> addVertex(array.drop(1).toTypedArray())
			"vt" -> addVertexTexture(array.drop(1).toTypedArray())
			"vn" -> addVertexNormal(array.drop(1).toTypedArray())
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

private fun addVertexTexture(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		1 -> Vertex(coords[0], 0.0f, 0.0f)
		2 -> Vertex(coords[0], coords[1], 0.0f)
		3 -> Vertex(coords[0], coords[1], coords[2])
		else -> throw Exception("Vertex texture error: ${array.joinToString(" ")}")
	}
	verticesTexture.add(vertex)
}

private fun addVertexNormal(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		3 -> Vertex(coords[0], coords[1], coords[2])
		else -> throw Exception("Vertex normal error: ${array.joinToString(" ")}")
	}
	verticesNormal.add(vertex)
}

private fun addFace(array: Array<String>) {
	val vs = mutableListOf<Int>()
	val vns = mutableListOf<Int>()
	val vts = mutableListOf<Int>()

	val coords = array.filter { it.isNotBlank() }

	coords.forEach { coord ->
		val elem = coord.split('/')

		elem[0].toIntOrNull()?.let { vs.add(it - 1) } ?: run {
			vs.add(vertices.lastIndex)
		}

		if (elem.size > 1) {
			elem[1].toIntOrNull()?.let { vts.add(it - 1) } ?: run {
				elem[2].toIntOrNull()?.let { vns.add(it - 1) } ?: run {
					vns.add(verticesNormal.lastIndex)
				}
			}
		}

		if (elem.size > 2) {
			elem[2].toIntOrNull()?.let { vns.add(it - 1) } ?: run {
				vns.add(verticesNormal.lastIndex)
			}
		}
	}

	faces.add(Face(vs, vts, vns))
}