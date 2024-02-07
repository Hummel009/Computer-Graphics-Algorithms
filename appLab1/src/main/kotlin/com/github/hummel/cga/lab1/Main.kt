package com.github.hummel.cga.lab1

import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.swing.JFrame
import javax.swing.JPanel

fun main() {
	EventQueue.invokeLater {
		try {
			val objViewer = OBJViewer("teapot.obj")
			val frame = GUI(objViewer)
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI(objViewer: JPanel) : JFrame() {
	init {
		title = "Hummel009's OBJ Viewer"
		defaultCloseOperation = EXIT_ON_CLOSE
		setSize(800, 600)

		contentPane.add(objViewer)

		setLocationRelativeTo(null)
	}
}

class OBJViewer(private val objFilePath: String) : JPanel() {
	private val coords: MutableList<FloatArray> = mutableListOf()
	private val polygons: MutableList<IntArray> = mutableListOf()

	private var minX = Float.POSITIVE_INFINITY
	private var minY = Float.POSITIVE_INFINITY
	private var maxX = Float.NEGATIVE_INFINITY
	private var maxY = Float.NEGATIVE_INFINITY

	init {
		parseOBJFile()
	}

	private fun parseOBJFile() {
		val file = File(objFilePath)
		val reader = BufferedReader(FileReader(file))
		val lines = reader.readLines()

		lines.asSequence().map {
			it.trim().split("\\s+".toRegex()).toTypedArray()
		}.forEach {
			when (it[0]) {
				"v" -> { // координаты точки: x, y, z
					val coord = floatArrayOf(
						it[1].toFloat(), it[2].toFloat(), it[3].toFloat()
					)
					coords.add(coord)
					updateBounds(coord)
				}

				"f" -> { //полигон: вершины через запятую
					val polygon = intArrayOf(
						it[1].split("/")[0].toInt(), it[2].split("/")[0].toInt(), it[3].split("/")[0].toInt()
					)
					polygons.add(polygon)
				}
			}
		}
	}

	private fun updateBounds(coord: FloatArray) {
		if (coord[0] < minX) {
			minX = coord[0]
		}
		if (coord[0] > maxX) {
			maxX = coord[0]
		}
		if (coord[1] < minY) {
			minY = coord[1]
		}
		if (coord[1] > maxY) {
			maxY = coord[1]
		}
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		g.color = Color.BLACK

		val scaleX = width.toFloat() / (maxX - minX)
		val scaleY = height.toFloat() / (maxY - minY)

		//преобразование координат вершин полигона в координаты окна приложения
		for (polygon in polygons) {
			//индексы в obj начинаются с 1
			//поэтому вычитаем 1
			//получаем вершины полигона уже в виде координат
			val v1 = coords of polygon[0]
			val v2 = coords of polygon[1]
			val v3 = coords of polygon[2]

			//Преобразование в координаты пространства окна просмотра
			val x1 = v1[0] transformX (minX to scaleX)

			//Из-за особенностей JFrame нужно инвертировать низ и верх
			//Преобразование в координаты пространства окна просмотра
			val y1 = height invertAxisY (v1[1] transformY (minY to scaleY))

			//далее по аналогии
			val x2 = v2[0] transformX (minX to scaleX)
			val y2 = height invertAxisY (v2[1] transformY (minY to scaleY))
			val x3 = v3[0] transformX (minX to scaleX)
			val y3 = height invertAxisY (v3[1] transformY (minY to scaleY))

			//линии от вершины до вершины полигона, хвост замыкается на голове
			//итого - каёмка полигона
			g.drawLine(x1, y1, x2, y2)
			g.drawLine(x2, y2, x3, y3)
			g.drawLine(x3, y3, x1, y1)
		}
	}
}

