package com.github.hummel.cga.lab4j

import kotlin.math.pow

const val ambientIntencity: Float = 0.4f
const val diffuseIntencity: Float = 0.0f
const val specularIntencity: Float = 0.2f

fun getResultRgb(face: Face, alpha: Float, beta: Float, gamma: Float): RGB {
	val tex = getCenteredVertex(face.textels, alpha, beta, gamma)
	val texX = (tex.x * imgSize).toInt().coerceIn(0, imgSize - 1)
	val texY = ((1.0f - tex.y) * imgSize).toInt().coerceIn(0, imgSize - 1)

	val point = getCenteredVertex(face.realVertices, alpha, beta, gamma)

	val normalRgb = normalImage.getRGB(texX, texY).decompose()
	val normal = Vertex(
		(normalRgb.r / 256.0f) * 2.0f - 1.0f,
		(normalRgb.g / 256.0f) * 2.0f - 1.0f,
		(normalRgb.b / 256.0f) * 2.0f - 1.0f
	)

	val mraoRgb = mraoImage.getRGB(texX, texY).decompose()
	val mrao = Vertex(
		mraoRgb.r / 256.0f,
		mraoRgb.g / 256.0f,
		mraoRgb.b / 256.0f
	)

	val texRgb = textureImage.getRGB(texX, texY).decompose()

	val brightness = getBrightness(point, normal, mrao)

	val resultRgb = applyBrightness(texRgb, brightness)

	return resultRgb
}

private fun getBrightness(point: Vertex, normal: Vertex, mrao: Vertex): Float {
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
		brightness += specularIntencity * mrao.x * (rDotV / (refr.magnitude * view.magnitude)).pow(10.0f)
	}

	return brightness + ambientIntencity
}

private fun applyBrightness(rgb: RGB, brightness: Float): RGB {
	val r = (rgb.r * brightness).toInt()
	val g = (rgb.g * brightness).toInt()
	val b = (rgb.b * brightness).toInt()
	return RGB(r, g, b)
}