package com.github.hummel.cga.lab3j

import kotlin.math.pow

const val ambientIntencity: Float = 0.0f
const val diffuseIntencity: Float = 0.8f
const val specularIntencity: Float = 0.8f

fun getShading(face: Face, alpha: Float, beta: Float, gamma: Float): Int {
	val point = getCenteredVecForSet(face.vertices, alpha, beta, gamma)
	val normal = getCenteredVecForSet(face.normals, alpha, beta, gamma).normalize()

	val brightness = getBrightness(point, normal)

	val colorVal = (if (brightness * 255 > 255) 255 else brightness * 255).toInt()

	val color = (colorVal shl 16) or (colorVal shl 8) or colorVal

	return color
}

fun getBrightness(point: Vertex, normal: Vertex): Float {
	//diffuse
	val ray = lightPos - point
	var lightResult = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		lightResult += diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	//specular
	val refr = normal * 2.0f * angle - ray
	val view = eyePos - point
	val rDotV = refr scalarMul view

	if (rDotV > 0) {
		lightResult += specularIntencity * (rDotV / (refr.magnitude * view.magnitude)).pow(2.0f)
	}

	return lightResult + ambientIntencity
}

fun getCenteredVecForSet(set: Array<Vertex>, alpha: Float, beta: Float, gamma: Float): Vertex =
	set[0] * alpha + set[1] * beta + set[2] * gamma