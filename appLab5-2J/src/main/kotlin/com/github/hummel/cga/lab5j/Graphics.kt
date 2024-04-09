package com.github.hummel.cga.lab5j

import org.lwjgl.opengl.GL11.*

lateinit var displayMatrix: Array<FloatArray>
lateinit var lightPos: Vertex
lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	faces.forEach { drawTriangle(it) }
}

private fun drawTriangle(face: Face) {
	val viewDir = -face.realVertices[0] + eyePos
	val cosAngle = face.poliNormal scalarMul viewDir

	if (cosAngle <= 0) {
		return
	}

	face.viewVertices[0] = multiplyVertexByMatrix(face.realVertices[0], displayMatrix)
	face.viewVertices[1] = multiplyVertexByMatrix(face.realVertices[1], displayMatrix)
	face.viewVertices[2] = multiplyVertexByMatrix(face.realVertices[2], displayMatrix)

	val rgb = getResultRgb(face).toGL()

	for (i in face.viewVertices.indices) {
		face.savedW[i] = face.viewVertices[i].w
		face.viewVertices[i] divSelf face.viewVertices[i].w
	}

	glColor3f(rgb.r, rgb.g, rgb.b)

	glBegin(GL_TRIANGLES)
	for ((x, y, z, _) in face.viewVertices) {
		glVertex3f(x, y, z)
	}
	glEnd()
}