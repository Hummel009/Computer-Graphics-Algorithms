package com.github.hummel.cga.lab4b

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

object Parser {
	@JvmStatic
	fun extractVertex(line: String): Vertex {
		val list: MutableCollection<Double> = ArrayList()
		line.replace("v ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			.mapTo(list) { it.toDouble() }
		val dList = list.toTypedArray<Double>()
		return Vertex(dList[0], dList[1], dList[2])
	}

	@JvmStatic
	fun extractNormal(line: String): Vertex {
		val list: MutableCollection<Double> = ArrayList()
		line.replace("vn ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			.mapTo(list) { it.toDouble() }
		val dList = list.toTypedArray<Double>()
		return Vertex(dList[0], dList[1], dList[2])
	}

	@JvmStatic
	fun extractTexture(line: String): Vertex {
		val list: MutableCollection<Double> = ArrayList()
		line.replace("vt ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			.mapTo(list) { it.toDouble() }
		val dList = list.toTypedArray<Double>()
		return Vertex(dList[0], dList[1], 0.0)
	}

	@JvmStatic
	fun extractTriangle(
		line: String,
		vertices: List<Vertex?>,
		textures: List<Vertex?>,
		normals: List<Vertex>
	): Face {
		val result = Face()
		result.vertices = arrayOf(Vertex(), Vertex(), Vertex())
		result.textures = arrayOf(Vertex(), Vertex(), Vertex())
		result.normals = arrayOf(Vertex(), Vertex(), Vertex())

		val line1 = line.replace("f ", "")
		val vIndex = AtomicInteger(0)
		val nIndex = AtomicInteger(0)
		val tIndex = AtomicInteger(0)
		for (group in line1.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val list: MutableCollection<Int> = ArrayList()
			group.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().mapTo(list) { it.toInt() }
			val idList = list.toTypedArray<Int>()
			result.vertices[vIndex.getAndIncrement()] = vertices[idList[0] - 1]!!
			result.textures[tIndex.getAndIncrement()] = textures[idList[1] - 1]!!
			result.normals[nIndex.getAndIncrement()] = normals[idList[2] - 1].normalize().mul(-1.0)
		}
		return result
	}

	fun parse(modelPath: String): MutableList<Face?> {
		val vertexList: MutableList<Vertex?> = java.util.ArrayList()
		val normalList: MutableList<Vertex> = java.util.ArrayList()
		val textureList: MutableList<Vertex?> = java.util.ArrayList()
		val faceList: MutableList<Face?> = java.util.ArrayList()

		File(modelPath).bufferedReader().use { reader ->
			var line = reader.readLine()
			while (line != null) {
				if (line.startsWith("v ")) {
					vertexList.add(extractVertex(line))
				} else if (line.startsWith("vn ")) {
					normalList.add(extractNormal(line))
				} else if (line.startsWith("vt ")) {
					textureList.add(extractTexture(line))
				} else if (line.startsWith("f ")) {
					faceList.add(extractTriangle(line, vertexList, textureList, normalList))
				}

				line = reader.readLine()
			}
		}

		return faceList
	}
}
