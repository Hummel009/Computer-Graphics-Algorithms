package com.github.hummel.cga.lab4

import kotlin.math.pow

const val ambientIntencity: Float = 0.4f
const val diffuseIntencity: Float = 0.8f
const val specularIntencity: Float = 0.8f

inline fun getResultRgb(face: Face, alpha: Float, beta: Float, gamma: Float): RGB {
	val tex = getCenteredVecForSet(face.textures, alpha, beta, gamma)
	val texX = (tex.x * 4096).toInt().coerceIn(0, 4095)
	val texY = ((1.0f - tex.y) * 4096).toInt().coerceIn(0, 4095)

	val point = getCenteredVecForSet(face.vertices, alpha, beta, gamma)

	val normalData = normalData.getRGB(texX, texY)
	val normal = -Vertex(
		(normalData.r / 256.0f) * 2.0f - 1.0f,
		(normalData.g / 256.0f) * 2.0f - 1.0f,
		(normalData.b / 256.0f) * 2.0f - 1.0f
	)

	val mraoData = mraoData.getRGB(texX, texY)
	val mrao = Vertex(
		mraoData.r / 256.0f,
		mraoData.g / 256.0f,
		mraoData.b / 256.0f
	)

	val rgb = textureData.getRGB(texX, texY)

	val brightness = getBrightness(point, normal, mrao)

	val resultRgb = applyBrightness(rgb, brightness)

	return resultRgb
}

inline fun getBrightness(point: Vertex, normal: Vertex, mrao: Vertex): Float {
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
		brightness += specularIntencity * mrao.x * (rDotV / (refr.magnitude * view.magnitude)).pow(2.0f)
	}

	return brightness + ambientIntencity
}

inline fun applyBrightness(rgb: RGB, brightness: Float): RGB {
	var r = rgb.r
	var g = rgb.g
	var b = rgb.b
	r = (r * brightness).toInt()
	g = (g * brightness).toInt()
	b = (b * brightness).toInt()
	return RGB(r, g, b)
}