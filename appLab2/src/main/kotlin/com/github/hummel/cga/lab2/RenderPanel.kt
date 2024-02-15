package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.math.AlgoUtils
import com.github.hummel.cga.lab2.math.Matrix4
import com.github.hummel.cga.lab2.math.MatrixBuilder
import com.github.hummel.cga.lab2.math.Vector4
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JPanel
import kotlin.math.cos
import kotlin.math.sin

class RenderPanel(private val triangles: List<Array<Vector4?>?>) : JPanel() {
	private val viewportMatrix: Matrix4 =
		MatrixBuilder.Companion.buildViewport(Main.width, Main.height)
	private val projectionMatrix: Matrix4 = MatrixBuilder.Companion.buildProjection(1.75, 90.0)
	private val camera: Camera
	private val bufferedImage: BufferedImage
	private val zBuffer: DoubleArray
	private val imgGraphics: Graphics2D
	private var viewMatrix: Matrix4
	private val modelMatrix: Matrix4? = null
	private var camMatrix: Matrix4? = null
	private val mousePressed = false
	private var prevMouseX = 0
	private var prevMouseY = 0
	private var rotateY = 0.0
	private var rotateX = 0.0

	init {
		val dist = 5.0
		camera = Camera()
		camera.eye =
			Vector4(dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY))
		camera.target = Vector4(0.0, 0.0, 0.0)
		camera.up = Vector4(0.0, 1.0, 0.0)
		viewMatrix = MatrixBuilder.Companion.buildView(camera)
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

		val dist = Main.dist
		camera.eye =
			Vector4(dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY))
		viewMatrix = MatrixBuilder.Companion.buildView(camera)
		setupCamMatrix()

		val finalMatrix = camMatrix

		imgGraphics.clearRect(0, 0, Main.width, Main.height)
		val filteredList: List<Array<Vector4?>?> = AlgoUtils.Companion.filterTriangles(triangles, camera)
		val drawList: List<Array<Vector4?>> = AlgoUtils.Companion.applyMatrix(filteredList, finalMatrix)

		Arrays.fill(zBuffer, Double.POSITIVE_INFINITY)

		for (i in drawList.indices) {
			val t = filteredList[i]
			val drawT = drawList[i]
			val center = AlgoUtils.getCenter(t)
			val normal = t!![3]!!.normalize()
			val ray = center?.subtract(camera.eye)?.subtract(camera.up)?.normalize()
			val cosAngle = normal!!.dot(ray)
			AlgoUtils.Companion.drawRasterTriangle(bufferedImage, drawT, zBuffer, cosAngle)
		}

		g2d.drawImage(bufferedImage, 0, 0, Main.width, Main.height, null)
	}

	private fun setupCamMatrix() {
		camMatrix = viewportMatrix.mul(projectionMatrix).mul(viewMatrix)
	}
}
