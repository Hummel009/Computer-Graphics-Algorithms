package com.github.hummel.cga.lab5j

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
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
		GLFW.glfwDestroyWindow(window)

		GLFW.glfwTerminate()
	}

	private fun init() {
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

		check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

		GLFW.glfwDefaultWindowHints()
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)

		val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()) ?: return
		val screenWidth = videoMode.width()
		val screenHeight = videoMode.height()

		val xPos = (screenWidth - windowWidth) / 2
		val yPos = (screenHeight - windowHeight) / 2

		window = GLFW.glfwCreateWindow(windowWidth, windowHeight, "Black Window", MemoryUtil.NULL, MemoryUtil.NULL)
		if (window == MemoryUtil.NULL) {
			throw RuntimeException("Failed to create the GLFW window")
		}

		GLFW.glfwSetWindowPos(window, xPos, yPos)

		GLFW.glfwSetKeyCallback(window) { window: Long, key: Int, _: Int, action: Int, _: Int ->
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true)
			}
		}

		GLFW.glfwMakeContextCurrent(window)
		GLFW.glfwSwapInterval(1)
		GLFW.glfwShowWindow(window)

		GL.createCapabilities()
	}

	private fun loop() {
		while (!GLFW.glfwWindowShouldClose(window)) {
			drawSquare() // Рисуем квадрат
			GLFW.glfwSwapBuffers(window)
			GLFW.glfwPollEvents()
		}
	}

	private fun drawSquare() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

		GL11.glBegin(GL11.GL_QUADS)
		GL11.glColor3f(1.0f, 1.0f, 1.0f)
		GL11.glVertex2f(-0.5f, -0.5f)
		GL11.glVertex2f(0.5f, -0.5f)
		GL11.glVertex2f(0.5f, 0.5f)
		GL11.glVertex2f(-0.5f, 0.5f)
		GL11.glEnd()
	}
}