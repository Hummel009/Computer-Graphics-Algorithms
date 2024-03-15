package com.github.hummel.cga.lab4j

import kotlin.math.max
import kotlin.math.pow

const val generalIntencity: Float = 0.8f

const val diffuseIntencity: Float = 0.8f
const val specularIntencity: Float = 0.8f

fun getColor(
	face: Face, alpha: Float, beta: Float, gamma: Float
): Int {
	val triple = getColorForCoords(face, alpha, beta, gamma)

	val texX = triple.first
	val texY = triple.second
	val color = triple.third

	val brightness = getBrightness(texX, texY, face, alpha, beta, gamma)

	val resultColor = applyBrightness(color, brightness)
	return resultColor
}

fun getColorForCoords(
	face: Face, alpha: Float, beta: Float, gamma: Float
): Triple<Int, Int, Int> {
	var texVec = getCenteredVecForSet(face.textures, alpha, beta, gamma)
	texVec = Vertex(texVec.x, 1.0f - texVec.y, 0.0f)
	var texX = (texVec.x * textureImage.width).toInt() % textureImage.width
	var texY = (texVec.y * textureImage.height).toInt() % textureImage.height

	if (texX > 4095) {
		texX = 4095
	}
	if (texX < 0) {
		texX = 0
	}
	if (texY > 4095) {
		texY = 4095
	}
	if (texY < 0) {
		texY = 0
	}

	val color = textureImage.getRGB(texX, texY)
	return Triple(texX, texY, color)
}

fun getBrightness(
	texX: Int, texY: Int, face: Face, alpha: Float, beta: Float, gamma: Float
): Float {
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

	val pos = getCenteredVecForSet(face.vertices, alpha, beta, gamma)
	val lightPos = Vertex(5.0f, 5.0f, 5.0f)
	val ray = pos.minus(lightPos).normalize()
	val diffuse = max(normal.scalarMul(ray) * diffuseIntencity, 0.0f)

	// считаем specular
	var specular = 0.0f
	val l = lightPos.minus(pos)
	val s = 10.0f
	val angle = normal.scalarMul(l)

	val r = normal.times(angle).times(2.0f).minus(l)
	val v = eyePos - pos
	val rDotV = max(r.scalarMul(v), 0.0f)
	if (rDotV > 0) {
		specular = (rDotV / (r.magnitude * v.magnitude)).pow(s)
	}

	val brightColor = diffuse * diffuseIntencity + specular * mraoVec.x * specularIntencity
	return brightColor
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

fun getColorOld(face: Face, alpha: Float, beta: Float, gamma: Float): Int {
	val point = getCenteredVecForSet(face.vertices, alpha, beta, gamma)
	val normal = getCenteredVecForSet(face.normals, alpha, beta, gamma).normalize()

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

fun getCenteredVecForSet(set: Array<Vertex>, alpha: Float, beta: Float, gamma: Float): Vertex =
	set[0] * alpha + set[1] * beta + set[2] * gamma