package hummel

import java.awt.Color
import java.awt.Graphics
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

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
					val vertex = floatArrayOf(
						it[1].toFloat(), it[2].toFloat(), it[3].toFloat()
					)
					coords.add(vertex)
					updateBounds(vertex)
				}

				"f" -> { //полигон: вершины через запятую
					val face = intArrayOf(
						it[1].split("/")[0].toInt(), it[2].split("/")[0].toInt(), it[3].split("/")[0].toInt()
					)
					polygons.add(face)
				}
			}
		}
	}

	private fun updateBounds(vertex: FloatArray) {
		if (vertex[0] < minX) minX = vertex[0]
		if (vertex[0] > maxX) maxX = vertex[0]
		if (vertex[1] < minY) minY = vertex[1]
		if (vertex[1] > maxY) maxY = vertex[1]
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		g.color = Color.BLACK

		val scaleX = width.toFloat() / (maxX - minX)
		val scaleY = height.toFloat() / (maxY - minY)

		for (face in polygons) {
			val v1 = coords[face[0] - 1]
			val v2 = coords[face[1] - 1]
			val v3 = coords[face[2] - 1]

			val x1 = ((v1[0] - minX) * scaleX).toInt()
			val y1 = height - ((v1[1] - minY) * scaleY).toInt()
			val x2 = ((v2[0] - minX) * scaleX).toInt()
			val y2 = height - ((v2[1] - minY) * scaleY).toInt()
			val x3 = ((v3[0] - minX) * scaleX).toInt()
			val y3 = height - ((v3[1] - minY) * scaleY).toInt()

			g.drawLine(x1, y1, x2, y2)
			g.drawLine(x2, y2, x3, y3)
			g.drawLine(x3, y3, x1, y1)
		}
	}
}

fun main() {
	val frame = JFrame("OBJ Viewer")
	val objViewer = OBJViewer("test.obj")

	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
	frame.contentPane.add(objViewer)
	frame.setSize(800, 600)
	frame.isVisible = true
	frame.addWindowListener(object : WindowAdapter() {
		override fun windowClosing(e: WindowEvent) {
			exitProcess(0)
		}
	})
}