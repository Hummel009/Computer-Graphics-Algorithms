package com.github.hummel.cga.lab3j

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.UIManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.measureTime

private val execTimes: MutableList<Long> = mutableListOf()
private var min: Long = Long.MAX_VALUE
private var max: Long = Long.MIN_VALUE

const val windowWidth: Int = (1040.0f * 1.25f).toInt()
const val windowHeight: Int = (580.0f * 1.25f).toInt()

val bufferedImage: BufferedImage = BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB)

var faces: MutableList<Face> = mutableListOf()

private var dist: Float = 20.0f

fun main() {
	print("Enter model name (tie|knight|car): ")

	val name = readln()

	dist = when (name) {
		"tie" -> 10.0f
		"knight" -> 20.0f
		"mace" -> 100.0f
		"car" -> 5.0f
		else -> 50.0f
	}

	parse("$name.obj")

	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatMTGitHubDarkIJTheme())
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	init {
		title = "Renderer: Kotlin JVM"
		defaultCloseOperation = EXIT_ON_CLOSE
		setSize(windowWidth, windowHeight)

		val drawingPanel = object : JPanel() {
			private var prevMouseX = 0
			private var prevMouseY = 0
			private var rotateY = 0.0f
			private var rotateX = 0.0f

			private val imgGraphics: Graphics2D = bufferedImage.createGraphics()

			init {
				imgGraphics.background = Color(0, 0, 0, 0)

				addMouseMotionListener(object : MouseAdapter() {
					override fun mouseDragged(e: MouseEvent) {
						super.mouseDragged(e)

						if (rotateX > Math.PI / 2 - 0.1f) {
							rotateX = Math.PI.toFloat() / 2 - 0.05f
						} else if (rotateX < -Math.PI / 2 + 0.1f) {
							rotateX = -Math.PI.toFloat() / 2 - 0.05f
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
				val time = measureTime {
					super.paintComponent(g)

					val g2d = g as Graphics2D
					g.setColor(Color.GREEN)

					imgGraphics.clearRect(0, 0, windowWidth, windowHeight)

					val eye = Vertex(
						dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY)
					)

					renderObject(eye)

					g2d.drawImage(bufferedImage, 0, 0, windowWidth, windowHeight, null)
				}.inWholeNanoseconds

				val fps = (1_000_000_000.0 / time).toLong()

				execTimes.add(fps)

				min = min.coerceAtMost(fps)
				max = max.coerceAtLeast(fps)

				val avg = execTimes.average().toLong()

				println("$fps FPS, [$min; $max]; AVG: $avg; Lag: ${time / 1_000_000}ms")
			}
		}
		drawingPanel.setSize(windowWidth, windowHeight)

		add(drawingPanel)

		setLocationRelativeTo(null)
	}
}