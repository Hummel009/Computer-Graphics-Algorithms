package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.math.Vertex

object ParsingHelper {
	fun extractVertex(line: String): Vertex {
		val list: MutableList<Double> = ArrayList()
		for (s in line.replace("v ", "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val parseDouble = s.toDouble()
			list.add(parseDouble)
		}
		val dList = list.toTypedArray<Double>()
		return Vertex(dList[0], dList[1], dList[2])
	}

	fun extractTriangle(line: String, vertices: List<Vertex?>): Array<Vertex?> {
		val f = line.replace("f ", "")
		val indexList: MutableCollection<Int> = ArrayList()
		for (vInfo in f.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			val s = vInfo.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
			val parseInt = s.toInt()
			indexList.add(parseInt)
		}
		val list: MutableList<Vertex?> = ArrayList()
		for (id in indexList) {
			val Vertex = vertices[id - 1]
			list.add(Vertex)
		}
		return list.toTypedArray<Vertex?>()
	}
}
