package com.github.hummel.cga.lab4b

import kotlin.math.tan

object MyMath {
	@JvmStatic
	fun buildViewport(width: Int, height: Int): MyMatrix {
		val viewport = MyMatrix()
		viewport[0, 0] = width / 2.0f
		viewport[1, 1] = -height / 2.0f
		viewport[0, 3] = width / 2.0f
		viewport[1, 3] = height / 2.0f
		return viewport
	}

	@JvmStatic
	fun buildProjection(aspect: Float, FOV: Float): MyMatrix {
		val zNear = 0.01f
		val zFar = 1.0f

		val projectionMatrix = MyMatrix()
		projectionMatrix[0, 0] = 1.0f / (aspect * tan(FOV / 2.0f * 0.0174533f))
		projectionMatrix[1, 1] = 1.0f / tan(FOV / 2.0f * 0.0174533f)
		projectionMatrix[2, 2] = zFar / (zNear - zFar)
		projectionMatrix[2, 3] = zFar * zNear / (zNear - zFar)
		projectionMatrix[3, 2] = -1.0f
		projectionMatrix[3, 3] = 0.0f
		return projectionMatrix
	}

	@JvmStatic
	fun buildView(camera: Camera): MyMatrix = buildView(camera.eye, camera.target, camera.up)

	private fun buildView(eye: Vertex?, target: Vertex?, up: Vertex?): MyMatrix {
		val viewMatrix = MyMatrix()
		val ZAxis = eye!!.minus(target!!).normalize()
		val XAxis = up!!.vectorMul(ZAxis).normalize()
		val YAxis = ZAxis.vectorMul(XAxis)

		viewMatrix[0, 0] = XAxis[0]
		viewMatrix[0, 1] = XAxis[1]
		viewMatrix[0, 2] = XAxis[2]
		viewMatrix[0, 3] = -XAxis.scalarMul(eye)

		viewMatrix[1, 0] = YAxis[0]
		viewMatrix[1, 1] = YAxis[1]
		viewMatrix[1, 2] = YAxis[2]
		viewMatrix[1, 3] = -YAxis.scalarMul(eye)

		viewMatrix[2, 0] = ZAxis[0]
		viewMatrix[2, 1] = ZAxis[1]
		viewMatrix[2, 2] = ZAxis[2]
		viewMatrix[2, 3] = -ZAxis.scalarMul(eye)

		return viewMatrix
	}
}
