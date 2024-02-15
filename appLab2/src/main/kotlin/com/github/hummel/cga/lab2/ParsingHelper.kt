package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.math.Vector4

object ParsingHelper {
	fun extractVertex(line: String): Vector4 {
		val list: MutableList<Double> = ArrayList()
		for (s in line.replace("v ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val parseDouble = s.toDouble()
			list.add(parseDouble)
		}
		val dList = list.toTypedArray<Double>()
		return Vector4(dList[0], dList[1], dList[2])
	}

	fun extractTriangle(line: String, vertices: List<Vector4?>): Array<Vector4?> {
		val f = line.replace("f ", "")
		val indexList: MutableCollection<Int> = ArrayList()
		for (vInfo in f.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val s = vInfo.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
			val parseInt = s.toInt()
			indexList.add(parseInt)
		}
		val list: MutableList<Vector4?> = ArrayList()
		for (id in indexList) {
			val vector4 = vertices[id - 1]
			list.add(vector4)
		}
		return list.toTypedArray<Vector4?>()
	}
}
