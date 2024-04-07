package com.github.hummel.cga.lab4b

import javax.swing.JFrame
import javax.swing.JPanel

class Canvas(faces: List<Face?>?) : JFrame() {
	init {
		title = "My frame"
		defaultCloseOperation = EXIT_ON_CLOSE
		setSize(Main.width, Main.height)

		val drawingPanel: JPanel = RenderPanel(faces)
		drawingPanel.setSize(Main.width, Main.height)

		add(drawingPanel)
	}
}
