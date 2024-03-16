package com.github.hummel.cga.lab4j

fun Int.decompose(): RGB {
	val r = this shr 16 and 0xff
	val g = this shr 8 and 0xff
	val b = this and 0xff
	return RGB(r, g, b)
}

fun RGB.compose(): Int = r shl 16 or (g shl 8) or b