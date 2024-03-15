package com.github.hummel.cga.lab4j

import kotlin.math.pow

const val generalIntencity: Float = 0.8f

const val diffuseIntencity: Float = 1.0f
const val specularIntencity: Float = 1.0f

fun getShading(face: Face, alpha: Float, beta: Float, gamma: Float): Int {
	var texVec = getCenteredVecForSet(face.textures, alpha, beta, gamma)
	texVec = Vertex(texVec.x, 1.0f - texVec.y, 0.0f)
	val texX = (texVec.x * textureImage.width).toInt().coerceIn(0, 4095)
	val texY = (texVec.y * textureImage.height).toInt().coerceIn(0, 4095)

	val point = getCenteredVecForSet(face.vertices, alpha, beta, gamma)

	val normalData = normalImage.getRGB(texX, texY)
	val normal = -Vertex(
		(((normalData shr 16) and (0x000000ff)) / 256.0f) * 2.0f - 1.0f,
		(((normalData shr 8) and (0x000000ff)) / 256.0f) * 2.0f - 1.0f,
		(((normalData) and (0x000000ff)) / 256.0f) * 2.0f - 1.0f
	)

	val mraoData = mraoImage.getRGB(texX, texY)
	val mraoVec = Vertex(
		((mraoData shr 16) and (0x000000ff)) / 256.0f,
		((mraoData shr 8) and (0x000000ff)) / 256.0f,
		((mraoData) and (0x000000ff)) / 256.0f
	)

	val color = textureImage.getRGB(texX, texY)

	val brightness = getBrightness(point, normal)

	val resultColor = applyBrightness(color, brightness)
	return resultColor
}

fun getBrightness(point: Vertex, normal: Vertex): Float {
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

fun applyBrightness(color: Int, brightness: Float): Int {
	var r = (color and 0x00ff0000) shr 16
	var g = (color and 0x0000ff00) shr 8
	var b = (color and 0x000000ff)
	r = (r * brightness).toInt()
	g = (g * brightness).toInt()
	b = (b * brightness).toInt()
	return (r shl 16) or (g shl 8) or b
}

fun getCenteredVecForSet(set: Array<Vertex>, alpha: Float, beta: Float, gamma: Float): Vertex =
	set[0] * alpha + set[1] * beta + set[2] * gamma