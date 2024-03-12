package com.github.hummel.cga.lab3

inline fun getFromLighting(face: Face): Color {
	val point = face.vertices[0]
	val normal = face.poliNormal

	val light = calculateLightDiffuse(point, normal)

	val colorVal = (if (light * 255 > 255) 255 else light * 255).toByte()

	val color = Color(colorVal, colorVal, colorVal)

	return color
}

inline fun calculateLightDiffuse(point: Vertex, normal: Vertex): Float {
	val ray = lightPos - point
	var lightResult = 0.0f
	val angle = normal scalarMul ray

	if (angle > 0) {
		lightResult = 1.0f * angle / (ray.magnitude * normal.magnitude)
	}

	return lightResult
}