package com.github.hummel.cga.lab5j

fun Int.decompose(): RGB {
	val r = this shr 16 and 0xff
	val g = this shr 8 and 0xff
	val b = this and 0xff
	return RGB(r, g, b)
}

fun RGB.compose(): Int = r shl 16 or (g shl 8) or b

fun RGB.toGL(): GLRGB {
	val glR = r.toFloat() / 255.0f
	val glG = g.toFloat() / 255.0f
	val glB = b.toFloat() / 255.0f
	return GLRGB(glR, glG, glB)
}