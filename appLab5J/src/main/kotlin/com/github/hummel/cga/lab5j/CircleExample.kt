package com.github.hummel.cga.lab5j

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

class BlackWindow {
	private var window: Long = 0

	fun run() {
		init()
		loop()

		Callbacks.glfwFreeCallbacks(window)
		GLFW.glfwDestroyWindow(window)

		GLFW.glfwTerminate()
	}

	private fun init() {
		check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

		GLFW.glfwDefaultWindowHints()
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)

		window = GLFW.glfwCreateWindow(800, 600, "Black Window", MemoryUtil.NULL, MemoryUtil.NULL)

		if (window == MemoryUtil.NULL) {
			throw RuntimeException("Failed to create the GLFW window")
		}

		GLFW.glfwSetKeyCallback(
			window
		) { window: Long, key: Int, _: Int, action: Int, _: Int ->
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
			GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f) // Красный фон
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

			GLFW.glfwSwapBuffers(window)
			GLFW.glfwPollEvents()
		}
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			BlackWindow().run()
		}
	}
}