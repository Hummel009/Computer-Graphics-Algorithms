package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.ParsingHelper.extractTriangle
import com.github.hummel.cga.lab2.ParsingHelper.extractVertex
import com.github.hummel.cga.lab2.hum.Face
import com.github.hummel.cga.lab2.hum.Vertex
import com.github.hummel.cga.lab2.math.addNormals
import java.io.BufferedReader
import java.io.FileReader


object Main {
	const val width: Int = 1400
	const val height: Int = 800
	const val xMin: Double = 0.0
	const val yMin: Double = 0.0
	private const val modelPath = "teapot.obj"
	const val dist: Double = 6.0 // dist

	@JvmStatic
	fun main(args: Array<String>) {
		val vertexList: MutableList<Vertex> = ArrayList()
		var triangleList: MutableList<Face> = ArrayList()

		BufferedReader(FileReader(modelPath)).use { bufferedReader ->
			var line: String?
			while (bufferedReader.readLine().also { line = it } != null) {
				if (line!!.startsWith("v ")) {
					vertexList.add(extractVertex(line!!))
				} else if (line!!.startsWith("f ")) {
					triangleList.add(extractTriangle(line!!, vertexList))
				}
			}
		}

		triangleList = addNormals(triangleList)

		val canvas = Canvas(triangleList)
		canvas.isVisible = true

		while (true) {
			canvas.repaint()
		}
	}
}