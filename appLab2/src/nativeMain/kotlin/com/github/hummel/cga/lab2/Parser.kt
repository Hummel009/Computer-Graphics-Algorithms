package com.github.hummel.cga.lab2

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

private val vertices: MutableList<Vertex> = mutableListOf()
private val textures: MutableList<Vertex> = mutableListOf()
private val normals: MutableList<Vertex> = mutableListOf()

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

private inline fun addVertex(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		3 -> Vertex(coords[0], coords[1], coords[2])
		4 -> Vertex(coords[0], coords[1], coords[2], coords[3])
		else -> throw Exception("Vertex error: ${array.joinToString(" ")}")
	}
	vertices.add(vertex)
}

private inline fun addVertexTexture(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		1 -> Vertex(coords[0], 0.0f, 0.0f)
		2 -> Vertex(coords[0], coords[1], 0.0f)
		3 -> Vertex(coords[0], coords[1], coords[2])
		else -> throw Exception("Vertex texture error: ${array.joinToString(" ")}")
	}
	textures.add(vertex)
}

private inline fun addVertexNormal(array: Array<String>) {
	val coords = array.map { it.toFloat() }

	val vertex = when (coords.size) {
		3 -> Vertex(coords[0], coords[1], coords[2])
		else -> throw Exception("Vertex normal error: ${array.joinToString(" ")}")
	}
	normals.add(vertex)
}

private inline fun addFace(array: Array<String>) {
	val vs = mutableListOf<Vertex>()
	val vns = mutableListOf<Vertex>()
	val vts = mutableListOf<Vertex>()

	val coords = array.filter { it.isNotBlank() }

	coords.forEach { coord ->
		val elem = coord.split('/')

		elem[0].toIntOrNull()?.let { vs.add(vertices[it - 1]) } ?: run {
			vs.add(vertices[vertices.lastIndex])
		}

		if (elem.size > 1) {
			elem[1].toIntOrNull()?.let { vts.add(textures[it - 1]) } ?: run {
				elem[2].toIntOrNull()?.let { vns.add(normals[it - 1]) } ?: run {
					vns.add(normals[normals.lastIndex])
				}
			}
		}

		if (elem.size > 2) {
			elem[2].toIntOrNull()?.let { vns.add(normals[it - 1]) } ?: run {
				vns.add(normals[normals.lastIndex])
			}
		}
	}

	var normal = Vertex(0.0f, 0.0f, 0.0f)

	vns.forEach { normal += it }

	if (vs.size > 3) {
		for (i in 1 until vs.size - 1) {
			faces.add(
				Face(
					arrayOf(vs[0], vs[i], vs[i + 1]),
					arrayOf(vns[0], vns[i], vns[i + 1]),
					arrayOf(vts[0], vts[i], vts[i + 1]),
					null,
					normal
				)
			)
		}
	} else {
		faces.add(
			Face(
				vs.toTypedArray(), vns.toTypedArray(), vts.toTypedArray(), null, normal
			)
		)
	}
}