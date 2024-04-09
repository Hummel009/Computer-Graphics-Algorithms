package com.github.hummel.cga.lab5j

fun RGB.toGL(): GLRGB {
	val glR = r.toFloat() / 255.0f
	val glG = g.toFloat() / 255.0f
	val glB = b.toFloat() / 255.0f
	return GLRGB(glR, glG, glB)
}