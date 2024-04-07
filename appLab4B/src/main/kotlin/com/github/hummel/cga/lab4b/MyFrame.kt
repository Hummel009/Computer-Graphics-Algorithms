package com.github.hummel.cga.lab4b

import javax.swing.JFrame
import javax.swing.JPanel

class MyFrame(faces: List<Face?>?) : JFrame() {
	init {
		title = "Renderer: Kotlin JVM"
		defaultCloseOperation = EXIT_ON_CLOSE
		setSize(Main.width, Main.height)

		val drawingPanel: JPanel = MyPanel(faces)
		drawingPanel.setSize(Main.width, Main.height)

		add(drawingPanel)

		setLocationRelativeTo(null)
	}
}
