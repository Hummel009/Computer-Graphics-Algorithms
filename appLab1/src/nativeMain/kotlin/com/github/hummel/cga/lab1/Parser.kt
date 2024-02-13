package com.github.hummel.cga.lab1

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
			"f" -> addFace(array.drop(1).toTypedArray())
			"v" -> addVertex(array.drop(1).toTypedArray())
			"vt" -> addVertexTexture(array.drop(1).toTypedArray())
			"vn" -> addVertexNormal(array.drop(1).toTypedArray())
		}
	}

	fclose(file)
}

private fun addFace(array: Array<String>) {
	val vertices = array.map { it.split("/")[0].toIntOrNull() }
	if (vertices.all { it != null }) {
		val face = Face(vertices.requireNoNulls().toSet())
		faces.add(face)
	} else {
		println("Error: Invalid vertex index in face definition - ${array.joinToString(" ")}")
	}
}

private fun addVertex(array: Array<String>) {
	val vertex = when (array.size) {
		3 -> Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat())
		4 -> Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat(), array[3].toFloat())
		else -> throw Exception("Vertex error!")
	}
	vertices.add(vertex)
}

private fun addVertexTexture(array: Array<String>) {
	val vertex = when (array.size) {
		1 -> Vertex(array[0].toFloat(), 0.0f, 0.0f)
		2 -> Vertex(array[0].toFloat(), array[1].toFloat(), 0.0f)
		3 -> Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat())
		else -> throw Exception("Vertex texture error!")
	}
	verticesTexture.add(vertex)
}

private fun addVertexNormal(array: Array<String>) {
	val vertex = when (array.size) {
		3 -> Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat())
		else -> throw Exception("Vertex normal error!")
	}
	verticesNormal.add(vertex)
}