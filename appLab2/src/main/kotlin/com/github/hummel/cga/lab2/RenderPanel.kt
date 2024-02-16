package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.hum.*
import com.github.hummel.cga.lab2.math.applyMatrix
import com.github.hummel.cga.lab2.math.drawRasterTriangle
import com.github.hummel.cga.lab2.math.filterTriangles
import com.github.hummel.cga.lab2.math.getCenter
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JPanel

class RenderPanel(private val triangles: List<Face>) : JPanel() {
	private val bufferedImage: BufferedImage
	private val zBuffer: DoubleArray
	private val imgGraphics: Graphics2D
	private var viewMatrix: Array<FloatArray>
	private lateinit var camMatrix: Array<FloatArray>
	private var prevMouseX = 0
	private var prevMouseY = 0
	private var rotateY = 0.0
	private var rotateX = 0.0

	init {
		viewMatrix = matrixView
		bufferedImage = BufferedImage(Main.width, Main.height, BufferedImage.TYPE_INT_RGB)
		imgGraphics = bufferedImage.createGraphics()
		zBuffer = DoubleArray(Main.width * Main.height)
		imgGraphics.background = Color(0, 0, 0, 0)

		setupCamMatrix()
		addMouseMotionListener(object : MouseAdapter() {
			override fun mouseDragged(e: MouseEvent) {
				super.mouseDragged(e)

				if (rotateX > Math.PI / 2 - 0.1) {
					rotateX = Math.PI / 2 - 0.05
				} else if (rotateX < -Math.PI / 2 + 0.1) {
					rotateX = -Math.PI / 2 - 0.05
				}
				rotateY -= (e.x - prevMouseX) / 200.0
				rotateX -= (e.y - prevMouseY) / 200.0

				prevMouseX = e.x
				prevMouseY = e.y
			}

			override fun mouseMoved(e: MouseEvent) {
				super.mouseMoved(e)
				prevMouseX = e.x
				prevMouseY = e.y
			}
		})
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		val g2d = g as Graphics2D
		g.setColor(Color.GREEN)

		viewMatrix = matrixView
		setupCamMatrix()

		val finalMatrix = camMatrix

		imgGraphics.clearRect(0, 0, Main.width, Main.height)
		val filteredList = filterTriangles(triangles)
		val drawList = applyMatrix(filteredList, finalMatrix)

		Arrays.fill(zBuffer, Double.POSITIVE_INFINITY)

		for (i in drawList.indices) {
			val t = filteredList[i]
			val drawT = drawList[i]
			val center = getCenter(t.toTriangle())
			val normal = t.toTriangle()[3].normalize()
			val ray = center.subtract(eye).subtract(up).normalize()
			val cosAngle = normal.dot(ray)
			drawRasterTriangle(bufferedImage, drawT, zBuffer, cosAngle.toDouble())
		}

		g2d.drawImage(bufferedImage, 0, 0, Main.width, Main.height, null)
	}

	private fun setupCamMatrix() {
		camMatrix = displayMatrix
	}
}
