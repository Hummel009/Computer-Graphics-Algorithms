package com.github.hummel.cga.lab5j

import org.lwjgl.opengl.GL30.*

lateinit var displayMatrix: Array<FloatArray>
lateinit var lightPos: Vertex
lateinit var eyePos: Vertex

fun renderObject(eye: Vertex) {
	displayMatrix = getDisplayMatrix(eye)
	lightPos = getLightPos(eye)
	eyePos = eye

	glEnable(GL_DEPTH_TEST)
	glDepthFunc(GL_LEQUAL)

	glEnable(GL_CULL_FACE)
	glCullFace(GL_BACK)

	glBegin(GL_TRIANGLES)

	faces.forEach { drawTriangle(it) }

	glEnd()
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

	val rgb = getResultRgb(face).toGL()

	glColor3f(rgb.r, rgb.g, rgb.b)

	for ((x, y, z, _) in face.viewVertices) {
		glVertex3f(x, y, z)
	}
}