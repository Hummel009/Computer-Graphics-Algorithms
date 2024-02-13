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
			"v" -> addVertex(array.drop(1).toTypedArray())
			"f" -> addFace(array.drop(1).toTypedArray())
		}
	}

	fclose(file)
}

private fun addVertex(array: Array<String>) {
	val vertex = if (array.size == 4) {
		Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat(), array[3].toFloat())
	} else {
		Vertex(array[0].toFloat(), array[1].toFloat(), array[2].toFloat(), 1.0f)
	}
	vertices.add(vertex)
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