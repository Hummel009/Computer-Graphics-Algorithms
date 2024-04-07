package com.github.hummel.cga.lab4b

import kotlin.math.tan

object MyMath {
	@JvmStatic
	fun buildViewport(width: Int, height: Int): MyMatrix {
		val viewport = MyMatrix()
		viewport.set(0, 0, width / 2.0)
		viewport.set(1, 1, -height / 2.0)
		viewport.set(0, 3, width / 2.0)
		viewport.set(1, 3, height / 2.0)
		return viewport
	}

	@JvmStatic
	fun buildProjection(aspect: Double, FOV: Double): MyMatrix {
		val zNear = 0.01
		val zFar = 1.0

		val projectionMatrix = MyMatrix()
		projectionMatrix.set(0, 0, 1.0 / (aspect * tan(FOV / 2.0 * 0.0174533)))
		projectionMatrix.set(1, 1, 1.0 / tan(FOV / 2.0 * 0.0174533))
		projectionMatrix.set(2, 2, zFar / (zNear - zFar))
		projectionMatrix.set(2, 3, zFar * zNear / (zNear - zFar))
		projectionMatrix.set(3, 2, -1.0)
		projectionMatrix.set(3, 3, 0.0)
		return projectionMatrix
	}

	@JvmStatic
	fun buildView(camera: Camera): MyMatrix {
		return buildView(camera.eye, camera.target, camera.up)
	}

	private fun buildView(eye: Vertex?, target: Vertex?, up: Vertex?): MyMatrix {
		val viewMatrix = MyMatrix()
		val ZAxis = eye!!.subtract(target!!).normalize()
		val XAxis = up!!.cross(ZAxis).normalize()
		val YAxis = ZAxis.cross(XAxis)

		viewMatrix.set(0, 0, XAxis[0])
		viewMatrix.set(0, 1, XAxis[1])
		viewMatrix.set(0, 2, XAxis[2])
		viewMatrix.set(0, 3, -XAxis.dot(eye))

		viewMatrix.set(1, 0, YAxis[0])
		viewMatrix.set(1, 1, YAxis[1])
		viewMatrix.set(1, 2, YAxis[2])
		viewMatrix.set(1, 3, -YAxis.dot(eye))

		viewMatrix.set(2, 0, ZAxis[0])
		viewMatrix.set(2, 1, ZAxis[1])
		viewMatrix.set(2, 2, ZAxis[2])
		viewMatrix.set(2, 3, -ZAxis.dot(eye))

		return viewMatrix
	}
}
