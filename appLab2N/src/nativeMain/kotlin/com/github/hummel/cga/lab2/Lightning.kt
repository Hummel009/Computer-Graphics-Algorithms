@file:Suppress("NOTHING_TO_INLINE")

package com.github.hummel.cga.lab2

const val ambientIntencity: Float = 0.0f
const val diffuseIntencity: Float = 0.8f

inline fun getResultRgb(face: Face): RGB {
	val point = face.realVertices[0]
	val normal = face.poliNormal

	val brightness = getBrightness(point, normal)

	val colorVal = (if (brightness * 255 > 255) 255 else brightness * 255).toInt()

	return RGB(colorVal, colorVal, colorVal)
}

inline fun getBrightness(point: Vertex, normal: Vertex): Float {
	//diffuse
	val ray = lightPos - point
	var brightness = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		brightness += diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	return brightness + ambientIntencity
}