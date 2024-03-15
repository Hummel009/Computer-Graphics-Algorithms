package com.github.hummel.cga.lab2j

const val generalIntencity: Float = 0.8f

fun getColor(face: Face): Int {
	val point = face.vertices[0]
	val normal = face.poliNormal

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
		lightResult = generalIntencity * angle / (ray.magnitude * normal.magnitude)
	}

	return lightResult
}