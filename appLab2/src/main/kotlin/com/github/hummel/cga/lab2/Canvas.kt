package com.github.hummel.cga.lab2

import com.github.hummel.cga.lab2.math.Vertex
import javax.swing.JFrame
import javax.swing.JPanel

class Canvas(triangles: List<Array<Vertex?>?>) : JFrame() {
	init {
		title = "My frame"
		defaultCloseOperation = EXIT_ON_CLOSE
		setSize(Main.width, Main.height)

		val drawingPanel: JPanel = RenderPanel(triangles)
		drawingPanel.setSize(Main.width, Main.height)

		add(drawingPanel)
	}
}
