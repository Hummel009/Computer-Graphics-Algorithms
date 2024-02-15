package com.github.hummel.cga.lab2.math

import com.github.hummel.cga.lab2.Camera
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class MatrixBuilder {
	fun buildRotationY(theta: Double): Matrix4 {
		val result = Matrix4()
		result[0, 0] = cos(theta)
		result[0, 2] = sin(theta)
		result[2, 0] = -sin(theta)
		result[2, 2] = cos(theta)
		return result
	}

	fun buildRotationX(theta: Double): Matrix4 {
		val result = Matrix4()
		result[1, 1] = cos(theta)
		result[2, 1] = sin(theta)
		result[1, 2] = -sin(theta)
		result[2, 2] = cos(theta)
		return result
	}

	companion object {
		fun buildViewport(width: Int, height: Int): Matrix4 {
			val viewport = Matrix4()
			viewport[0, 0] = width / 2.0
			viewport[1, 1] = -height / 2.0
			viewport[0, 3] = width / 2.0
			viewport[1, 3] = height / 2.0
			return viewport
		}

		fun buildProjection(aspect: Double, FOV: Double): Matrix4 {
			val zNear = 0.01
			val zFar = 50.0

			val projectionMatrix = Matrix4()
			projectionMatrix[0, 0] = 1.0 / (aspect * tan(FOV / 2.0 * 0.0174533))
			projectionMatrix[1, 1] = 1.0 / tan(FOV / 2.0 * 0.0174533)
			projectionMatrix[2, 2] = zFar / (zNear - zFar)
			projectionMatrix[2, 3] = zFar * zNear / (zNear - zFar)
			projectionMatrix[3, 2] = -1.0
			projectionMatrix[3, 3] = 0.0
			return projectionMatrix
		}

		fun buildView(camera: Camera): Matrix4 {
			return buildView(camera.eye, camera.target, camera.up)
		}

		private fun buildView(eye: Vertex?, target: Vertex?, up: Vertex?): Matrix4 {
			val viewMatrix = Matrix4()
			val ZAxis = eye!!.subtract(target).normalize()
			val XAxis = up!!.cross(ZAxis).normalize()
			val YAxis = ZAxis!!.cross(XAxis)

			viewMatrix[0, 0] = XAxis!![0].toDouble()
			viewMatrix[0, 1] = XAxis[1].toDouble()
			viewMatrix[0, 2] = XAxis[2].toDouble()
			viewMatrix[0, 3] = -XAxis.dot(eye).toDouble()

			viewMatrix[1, 0] = YAxis!![0].toDouble()
			viewMatrix[1, 1] = YAxis[1].toDouble()
			viewMatrix[1, 2] = YAxis[2].toDouble()
			viewMatrix[1, 3] = -YAxis.dot(eye).toDouble()

			viewMatrix[2, 0] = ZAxis[0].toDouble()
			viewMatrix[2, 1] = ZAxis[1].toDouble()
			viewMatrix[2, 2] = ZAxis[2].toDouble()
			viewMatrix[2, 3] = -ZAxis.dot(eye).toDouble()

			return viewMatrix
		}
	}
}
