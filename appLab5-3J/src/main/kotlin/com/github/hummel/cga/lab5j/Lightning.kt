package com.github.hummel.cga.lab5j

import kotlin.math.pow

const val ambientIntencity: Float = 0.0f
const val diffuseIntencity: Float = 0.8f
const val specularIntencity: Float = 0.8f

fun getResultRgb(face: Face, alpha: Float, beta: Float, gamma: Float): RGB {
	val point = getCenteredVertex(face.realVertices, alpha, beta, gamma)
	val normal = getCenteredVertex(face.normals, alpha, beta, gamma).normalize()

	val brightness = getBrightness(point, normal)

	val colorVal = (if (brightness * 255 > 255) 255 else brightness * 255).toInt()

	return RGB(colorVal, colorVal, colorVal)
}

private fun getBrightness(point: Vertex, normal: Vertex): Float {
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