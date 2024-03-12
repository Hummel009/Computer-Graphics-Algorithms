package com.github.hummel.cga.lab2

const val generalIntencity: Float = 1.0f

inline fun getColor(face: Face): Color {
	val point = face.vertices[0]
	val normal = face.poliNormal

	val light = calculateLight(point, normal)

	val colorVal = (if (light * 255 > 255) 255 else light * 255).toByte()

	val color = Color(colorVal, colorVal, colorVal)

	return color
}

inline fun calculateLight(point: Vertex, normal: Vertex): Float {
	val ray = lightPos - point
	var lightResult = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		lightResult = generalIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	return lightResult
}