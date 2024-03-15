package com.github.hummel.cga.lab4

import kotlin.math.pow

const val generalIntencity: Float = 0.8f

const val diffuseIntencity: Float = 1.0f
const val specularIntencity: Float = 1.0f

fun getColor(face: Face, alpha: Float, beta: Float, gamma: Float): Int {
	val point = face.getCenteredVecForVertices(alpha, beta, gamma)
	val normal = face.getCenteredVecForNormals(alpha, beta, gamma).normalize()

	val light = calculateLight(point, normal)

	val colorVal = (if (light * 255 > 255) 255 else light * 255).toInt()

	val color = (colorVal shl 16) or (colorVal shl 8) or colorVal

	return color
}

fun calculateLight(point: Vertex, normal: Vertex): Float {
	//diffuse
	val ray = lightPos - point
	var lightResult = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		lightResult += generalIntencity * diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	//specular
	val refr = normal * 2.0f * angle - ray
	val view = eyePos - point
	val rDotV = refr scalarMul view

	if (rDotV > 0) {
		lightResult += generalIntencity * specularIntencity * (rDotV / (refr.magnitude * view.magnitude)).pow(2.0f)
	}

	return lightResult
}

fun Face.getCenteredVecForNormals(alpha: Float, beta: Float, gamma: Float): Vertex =
	normals[0] * alpha + normals[1] * beta + normals[2] * gamma

fun Face.getCenteredVecForVertices(alpha: Float, beta: Float, gamma: Float): Vertex =
	vertices[0] * alpha + vertices[1] * beta + vertices[2] * gamma