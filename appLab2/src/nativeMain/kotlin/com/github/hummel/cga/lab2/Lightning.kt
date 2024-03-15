package com.github.hummel.cga.lab2

const val diffuseIntencity: Float = 0.8f

inline fun getShading(face: Face): Byte {
	val point = face.vertices[0]
	val normal = face.poliNormal

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
		brightness = diffuseIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	return brightness
}