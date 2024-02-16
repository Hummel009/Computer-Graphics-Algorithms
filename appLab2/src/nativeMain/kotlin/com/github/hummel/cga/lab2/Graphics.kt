package com.github.hummel.cga.lab2

private val white: Color = Color(255, 255, 255, 255)
private val zBuffer: FloatArray = FloatArray(width * height)

fun renderObject() {
	fillBackground(white)
	zBuffer.fill(Float.POSITIVE_INFINITY)

	val filteredList = filterTriangles(faces)
	val drawList = applyMatrix(filteredList, displayMatrix)

	println(filteredList.size)
	println(drawList.size)

	for (i in drawList.indices) {
		val t = filteredList[i]
		val drawT = drawList[i]
		val center = t.getCenter()
		val normal = t.vertices[3].normalize()
		val ray = (center - eye - up).normalize()
		val cosAngle = normal scalarMul ray
		drawRasterTriangle(drawT, zBuffer, cosAngle)
	}
}

private fun fillBackground(color: Color) {
	for (y in 0 until height) {
		for (x in 0 until width) {
			val offset = (y * width + x) * 4
			bitmapData[offset + 0] = color.blue
			bitmapData[offset + 1] = color.green
			bitmapData[offset + 2] = color.red
			bitmapData[offset + 3] = color.alpha
		}
	}
}

private fun <T> split(list: List<T>, parts: Int): Array<List<T>> {
	require(parts > 0) { "Number of parts must be greater than zero." }

	val size = list.size
	val chunkSize = (size + parts - 1) / parts

	return list.chunked(chunkSize).toTypedArray()
}