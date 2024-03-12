package com.github.hummel.cga.lab3

import kotlin.math.pow

const val generalIntencity: Float = 1.0f

const val diffuseIntencity: Float = 0.2f
const val specularIntencity: Float = 0.8f

inline fun getColor(face: Face, alpha: Float, beta: Float, gamma: Float): Color {
	val point = face.getCenteredVecForVertices(alpha, beta, gamma)
	val normal = face.getCenteredVecForNormals(alpha, beta, gamma).normalize()

	val light = calculateLight(point, normal)

	val colorValR = (if (light.x * 255 > 255) 255 else light.x * 255).toByte()
	val colorValG = (if (light.y * 255 > 255) 255 else light.y * 255).toByte()
	val colorValB = (if (light.z * 255 > 255) 255 else light.z * 255).toByte()

	val color = Color(colorValR, colorValG, colorValB)

	return color
}

inline fun calculateLight(point: Vertex, normal: Vertex): Vertex {
	val ray = lightPos - point
	var lightResult = Vertex(0.0f, 0.0f, 0.0f)

	val angle = normal scalarMul ray

	if (angle > 0) {
		lightResult += generalIntencity * diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	val refr = normal * 2.0f * angle - ray
	val view = eye - point
	val rDotV = refr scalarMul view
	if (rDotV > 0) {
		lightResult += generalIntencity * specularIntencity * (rDotV / (refr.magnitude * view.magnitude)).pow(2.0f)
	}

	return lightResult
}

inline fun Face.getCenteredVecForNormals(alpha: Float, beta: Float, gamma: Float): Vertex =
	(normals[0] * alpha + normals[1] * beta + normals[2] * gamma)

inline fun Face.getCenteredVecForVertices(alpha: Float, beta: Float, gamma: Float): Vertex =
	vertices[0] * alpha + vertices[1] * beta + vertices[2] * gamma