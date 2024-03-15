package com.github.hummel.cga.lab3

import kotlin.math.pow

const val ambientIntencity: Float = 0.0f
const val diffuseIntencity: Float = 0.8f
const val specularIntencity: Float = 0.8f

inline fun getShading(face: Face, alpha: Float, beta: Float, gamma: Float): Byte {
	val point = getCenteredVecForSet(face.vertices, alpha, beta, gamma)
	val normal = getCenteredVecForSet(face.normals, alpha, beta, gamma).normalize()

	val brightness = getBrightness(point, normal)

	val colorVal = (if (brightness * 255 > 255) 255 else brightness * 255).toByte()

	return colorVal
}

inline fun getBrightness(point: Vertex, normal: Vertex): Float {
	//diffuse
	val ray = lightPos - point
	var brightness = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		brightness += diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	//specular
	val refr = normal * 2.0f * angle - ray
	val view = eyePos - point
	val rDotV = refr scalarMul view

	if (rDotV > 0) {
		brightness += specularIntencity * (rDotV / (refr.magnitude * view.magnitude)).pow(2.0f)
	}

	return brightness + ambientIntencity
}

fun getCenteredVecForSet(set: Array<Vertex>, alpha: Float, beta: Float, gamma: Float): Vertex =
	set[0] * alpha + set[1] * beta + set[2] * gamma