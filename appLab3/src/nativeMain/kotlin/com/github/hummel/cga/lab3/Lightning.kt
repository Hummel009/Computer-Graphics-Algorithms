package com.github.hummel.cga.lab3

import kotlin.math.abs
import kotlin.math.pow

const val diffuseIntency: Float = 0.2f
const val specularIntency: Float = 0.8f

inline fun getFromLighting(face: Face, alpha: Float, beta: Float, gamma: Float): Color {
	val point = face.getCenteredVecForVertices(alpha, beta, gamma)
	val normal = face.getCenteredVecForNormals(alpha, beta, gamma).normalize()

	val ray = (lightPos - point).normalize()
	val cosAngle = normal scalarMul ray

	val diffuse = abs(cosAngle) * diffuseIntency

	val refr = ((normal * 2.0f) * cosAngle) - ray
	val view = (eye - point).normalize()
	val rdotv = refr scalarMul view

	var specular = rdotv.pow(2.0f) * specularIntency
	if (specular < 0.0f) {
		specular = 0.0f
	}

	var colorVal = (0xff * (diffuse + specular)).toInt().toByte()
	if (colorVal > 0xff) {
		colorVal = 0xff.toByte()
	}

	val color = Color(colorVal, colorVal, colorVal)

	return color
}

inline fun Face.getCenteredVecForNormals(alpha: Float, beta: Float, gamma: Float): Vertex =
	(normals[0] * alpha + normals[1] * beta + normals[2] * gamma)

inline fun Face.getCenteredVecForVertices(alpha: Float, beta: Float, gamma: Float): Vertex =
	vertices[0] * alpha + vertices[1] * beta + vertices[2] * gamma