package com.github.hummel.cga.lab4b

import com.github.hummel.cga.lab4b.MyMath.buildProjection
import com.github.hummel.cga.lab4b.MyMath.buildView
import com.github.hummel.cga.lab4b.MyMath.buildViewport
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JPanel
import kotlin.math.cos
import kotlin.math.sin

class MyPanel(private val faces: List<Face?>?) : JPanel() {
	private val viewportMatrix = buildViewport(Main.width, Main.height)
	private val projectionMatrix = buildProjection(1.75, 90.0)
	private val camera: Camera
	private val bufferedImage: BufferedImage
	private val zBuffer: DoubleArray
	private val imgGraphics: Graphics2D
	private var viewMatrix: MyMatrix
	private var camMatrix: MyMatrix? = null
	private var prevMouseX = 0
	private var prevMouseY = 0
	private var rotateY = 0.0
	private var rotateX = 0.0

	init {
		val dist = 5.0
		camera = Camera()
		camera.eye =
			Vertex(dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY))
		camera.target = Vertex(0.0, 0.0, 0.0)
		camera.up = Vertex(0.0, 1.0, 0.0)
		viewMatrix = buildView(camera)
		bufferedImage = BufferedImage(Main.width, Main.height, BufferedImage.TYPE_INT_RGB)
		imgGraphics = bufferedImage.createGraphics()
		zBuffer = DoubleArray(Main.width * Main.height)
		imgGraphics.background = Color(0, 0, 0, 0)

		setupCamMatrix()
		addMouseMotionListener(object : MouseAdapter() {
			override fun mouseDragged(e: MouseEvent) {
				super.mouseDragged(e)

				if (rotateX > Math.PI / 2 - 0.1f) {
					rotateX = Math.PI / 2 - 0.05f
				} else if (rotateX < -Math.PI / 2 + 0.1f) {
					rotateX = -Math.PI / 2 - 0.05f
				}
				rotateY += (e.x - prevMouseX) / 200.0f
				rotateX += (e.y - prevMouseY) / 200.0f

				prevMouseX = e.x
				prevMouseY = e.y

				repaint()
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
			Vertex(dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY))
		viewMatrix = buildView(camera)
		setupCamMatrix()

		val finalMatrix = camMatrix

		imgGraphics.clearRect(0, 0, Main.width, Main.height)
		val filteredList = MyGraphics.filterTriangles(faces, camera)
		val drawList = MyGraphics.applyMatrix(filteredList, finalMatrix!!)
		for (face in drawList) {
			val i = AtomicInteger(0)
			val depthArr = DoubleArray(3)
			for (vertex in face.vertices) {
				depthArr[i.getAndIncrement()] = vertex[3]
				vertex.divSelf(vertex[3])
			}
			face.depthArr = depthArr
		}

		val newList: MutableCollection<Array<Face?>> = ArrayList()
		for (i in filteredList.indices) {
			newList.add(arrayOf(filteredList[i], drawList[i]))
		}
		Arrays.fill(zBuffer, Double.POSITIVE_INFINITY)

		newList.parallelStream().forEach { facePair: Array<Face?> ->
			MyGraphics.drawRasterTriangle(
				bufferedImage,
				facePair[0]!!,
				facePair[1]!!,
				zBuffer,
				camera
			)
		}

		g2d.drawImage(bufferedImage, 0, 0, Main.width, Main.height, null)
	}

	private fun setupCamMatrix() {
		camMatrix = viewportMatrix.mul(projectionMatrix).mul(viewMatrix)
	}
}
