package com.github.hummel.cga.lab4b

import java.util.concurrent.atomic.AtomicInteger

object ParsingHelper {
	@JvmStatic
	fun extractVertex(line: String): Vector4 {
		val list: MutableCollection<Double> = ArrayList()
		for (s in line.replace("v ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val parseDouble = s.toDouble()
			list.add(parseDouble)
		}
		val dList = list.toTypedArray<Double>()
		return Vector4(dList[0], dList[1], dList[2])
	}

	@JvmStatic
	fun extractNormal(line: String): Vector4 {
		val list: MutableCollection<Double> = ArrayList()
		for (s in line.replace("vn ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val parseDouble = s.toDouble()
			list.add(parseDouble)
		}
		val dList = list.toTypedArray<Double>()
		return Vector4(dList[0], dList[1], dList[2])
	}

	@JvmStatic
	fun extractTexture(line: String): Vector4 {
		val list: MutableCollection<Double> = ArrayList()
		for (s in line.replace("vt ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val parseDouble = s.toDouble()
			list.add(parseDouble)
		}
		val dList = list.toTypedArray<Double>()
		return Vector4(dList[0], dList[1], 0.0)
	}

	@JvmStatic
	fun extractTriangle(
		line: String,
		vertices: List<Vector4?>,
		textures: List<Vector4?>,
		normals: List<Vector4>
	): Triangle {
		val result = Triangle()
		result.vertices = arrayOf(Vector4(), Vector4(), Vector4())
		result.textures = arrayOf(Vector4(), Vector4(), Vector4())
		result.normals = arrayOf(Vector4(), Vector4(), Vector4())

		val line1 = line.replace("f ", "")
		val vIndex = AtomicInteger(0)
		val nIndex = AtomicInteger(0)
		val tIndex = AtomicInteger(0)
		for (group in line1.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val list: MutableCollection<Int> = ArrayList()
			for (s in group.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
				val parseInt = s.toInt()
				list.add(parseInt)
			}
			val idList = list.toTypedArray<Int>()
			result.vertices[vIndex.getAndIncrement()] = vertices[idList[0] - 1]!!
			result.textures[tIndex.getAndIncrement()] = textures[idList[1] - 1]!!
			result.normals[nIndex.getAndIncrement()] = normals[idList[2] - 1].normalize().mul(-1.0)
		}
		return result
	}
}
