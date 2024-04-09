package com.github.hummel.cga.lab5j

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil

fun main() {
	OpenGL.run()
}

object OpenGL {
	private var window: Long = 0

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

		window = glfwCreateWindow(windowWidth, windowHeight, "Black Window", MemoryUtil.NULL, MemoryUtil.NULL)
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
		while (!glfwWindowShouldClose(window)) {
			drawSquare() // Рисуем квадрат
			glfwSwapBuffers(window)
			glfwPollEvents()
		}
	}

	private fun drawSquare() {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		glOrtho(0.0, windowWidth.toDouble(), windowHeight.toDouble(), 0.0, -1.0, 1.0)

		glMatrixMode(GL_MODELVIEW)
		glLoadIdentity()

		glBegin(GL_QUADS)
		glColor3f(1.0f, 1.0f, 1.0f)
		glVertex2f(100.0f, 100.0f)
		glVertex2f(200.0f, 100.0f)
		glVertex2f(200.0f, 200.0f)
		glVertex2f(100.0f, 200.0f)
		glEnd()
	}
}