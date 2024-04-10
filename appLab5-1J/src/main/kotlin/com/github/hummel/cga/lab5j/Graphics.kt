package com.github.hummel.cga.lab5j

import org.lwjgl.opengl.GL11.*

private val rgb: GLRGB = RGB(255, 255, 255).toGL()

private lateinit var displayMatrix: Array<FloatArray>
private lateinit var lightPos: Vertex
private lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	faces.forEach { drawTriangle(it) }
}

private fun drawTriangle(face: Face) {
	face.viewVertices[0] = multiplyVertexByMatrix(face.realVertices[0], displayMatrix)
	face.viewVertices[1] = multiplyVertexByMatrix(face.realVertices[1], displayMatrix)
	face.viewVertices[2] = multiplyVertexByMatrix(face.realVertices[2], displayMatrix)

	for (i in face.viewVertices.indices) {
		face.viewVertices[i].z *= -1
	}

	for (i in face.viewVertices.indices) {
		face.viewVertices[i] divSelf face.viewVertices[i].w
	}

	glBegin(GL_LINES)

	glColor3f(rgb.r, rgb.g, rgb.b)

	for ((x, y, z, _) in face.viewVertices) {
		glVertex3f(x, y, z)
	}

	glEnd()
}