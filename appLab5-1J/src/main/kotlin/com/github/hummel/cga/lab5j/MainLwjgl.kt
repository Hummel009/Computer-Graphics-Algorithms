package com.github.hummel.cga.lab5j

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.measureTime

private val execTimes: MutableList<Long> = mutableListOf()
private var min: Long = Long.MAX_VALUE
private var max: Long = Long.MIN_VALUE

const val windowWidth: Int = (1040.0f * 1.25f).toInt()
const val windowHeight: Int = (580.0f * 1.25f).toInt()

var faces: MutableList<Face> = mutableListOf()

private var dist: Float = 20.0f

fun main() {
	print("Enter model name (tie|mace|knight|car): ")

	val name = readln()

	dist = when (name) {
		"tie" -> 10.0f
		"knight" -> 20.0f
		"mace" -> 100.0f
		"car" -> 5.0f
		else -> 50.0f
	}

	parse("$name.obj")

	OpenGL.run()
}

object OpenGL {
	private var window: Long = 0
	private var prevMouseX = 0
	private var prevMouseY = 0
	private var rotateY = 0.0f
	private var rotateX = 0.0f

	fun run() {
		init()
		loop()

		Callbacks.glfwFreeCallbacks(window)
		glfwDestroyWindow(window)

		glfwTerminate()
	}

	private fun init() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

		check(glfwInit()) { "Unable to initialize GLFW" }

		glfwDefaultWindowHints()
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

		val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: return
		val screenWidth = videoMode.width()
		val screenHeight = videoMode.height()

		val xPos = (screenWidth - windowWidth) / 2
		val yPos = (screenHeight - windowHeight) / 2

		window =
			glfwCreateWindow(windowWidth, windowHeight, "GPU Renderer: Kotlin JVM", MemoryUtil.NULL, MemoryUtil.NULL)
		if (window == MemoryUtil.NULL) {
			throw RuntimeException("Failed to create the GLFW window")
		}

		glfwSetWindowPos(window, xPos, yPos)

		glfwSetKeyCallback(window) { window: Long, key: Int, _: Int, action: Int, _: Int ->
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true)
			}
		}

		glfwMakeContextCurrent(window)
		glfwSwapInterval(1)
		glfwShowWindow(window)

		GL.createCapabilities()
	}

	private fun loop() {
		var prevRotateX = rotateX
		var prevRotateY = rotateY

		while (!glfwWindowShouldClose(window)) {
			glfwSetCursorPosCallback(window) { _, posX, posY ->
				if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
					val deltaX = posX.toFloat() - prevMouseX
					val deltaY = posY.toFloat() - prevMouseY

					if (rotateX > Math.PI / 2 - 0.1f) {
						rotateX = Math.PI.toFloat() / 2 - 0.05f
					} else if (rotateX < -Math.PI / 2 + 0.1f) {
						rotateX = -Math.PI.toFloat() / 2 - 0.05f
					}
					rotateY += deltaX / 200.0f
					rotateX += deltaY / 200.0f
				}

				prevMouseX = posX.toInt()
				prevMouseY = posY.toInt()
			}

			if (rotateX != prevRotateX || rotateY != prevRotateY) {
				val time = measureTime {
					drawScene()
				}.inWholeNanoseconds

				val fps = (1_000_000_000.0 / time).toLong()

				execTimes.add(fps)

				min = min.coerceAtMost(fps)
				max = max.coerceAtLeast(fps)

				val avg = execTimes.average().toLong()

				println("$fps FPS, [$min; $max]; AVG: $avg; Lag: ${time / 1_000_000}ms")

				prevRotateX = rotateX
				prevRotateY = rotateY
			}

			glfwSwapBuffers(window)
			glfwPollEvents()
		}
	}

	private fun drawScene() {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		glOrtho(0.0, windowWidth.toDouble(), windowHeight.toDouble(), 0.0, -1.0, 1.0)

		glMatrixMode(GL_MODELVIEW)
		glLoadIdentity()

		glBegin(GL_POINTS)
		glColor3f(1.0f, 1.0f, 1.0f)

		val eye = Vertex(
			dist * cos(rotateX) * cos(rotateY), dist * sin(rotateX), dist * cos(rotateX) * sin(rotateY)
		)

		renderObject(eye)

		glEnd()
	}
}