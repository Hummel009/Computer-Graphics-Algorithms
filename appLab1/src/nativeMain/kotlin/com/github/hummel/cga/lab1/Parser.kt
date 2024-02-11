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
			"v" -> {
				val vertex = Vertex(
					array[1].toFloat(), array[2].toFloat() - 1.5f, array[3].toFloat()
				)
				vertices.add(vertex)
			}

			"f" -> {
				val face = Face(
					array[1].split("/")[0].toInt(), array[2].split("/")[0].toInt(), array[3].split("/")[0].toInt()
				)
				faces.add(face)
			}
		}
	}

	fclose(file)
}