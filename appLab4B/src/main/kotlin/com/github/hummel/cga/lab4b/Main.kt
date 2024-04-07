package com.github.hummel.cga.lab4b

import com.github.hummel.cga.lab4b.ParsingHelper.extractNormal
import com.github.hummel.cga.lab4b.ParsingHelper.extractTexture
import com.github.hummel.cga.lab4b.ParsingHelper.extractTriangle
import com.github.hummel.cga.lab4b.ParsingHelper.extractVertex
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

object Main {
	const val width: Int = (1040.0f * 1.25f).toInt()
	const val height: Int = (580.0f * 1.25f).toInt()
	private const val modelPath = "box.obj"
	private const val texturePath = "box_texture.bmp"
	private const val normalMapPath = "box_normal.bmp"
	private const val mraoPath = "box_mrao.bmp"
	var textureImage: BufferedImage? = null
	var normalMapImage: BufferedImage? = null
	var mraoImage: BufferedImage? = null
	const val dist: Double = 3.0 // dist

	init {
		try {
			mraoImage = ImageIO.read(File(mraoPath))
			normalMapImage = ImageIO.read(File(normalMapPath))
			textureImage = ImageIO.read(File(texturePath))
		} catch (e: IOException) {
			throw RuntimeException(e)
		}
	}

	// teapot 8.0
	// destroyer 3000.0
	// doom 300.0
	@Throws(Exception::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val vertexList: MutableList<Vector4?> = ArrayList()
		val normalList: MutableList<Vector4> = ArrayList()
		val textureList: MutableList<Vector4?> = ArrayList()
		val triangleList: MutableList<Triangle?> = ArrayList()

		File(modelPath).bufferedReader().use { reader ->
			var line = reader.readLine()
			while (line != null) {
				if (line.startsWith("v ")) {
					vertexList.add(extractVertex(line))
				} else if (line.startsWith("vn ")) {
					normalList.add(extractNormal(line))
				} else if (line.startsWith("vt ")) {
					textureList.add(extractTexture(line))
				} else if (line.startsWith("f ")) {
					triangleList.add(extractTriangle(line, vertexList, textureList, normalList))
				}

				line = reader.readLine()
			}
		}
		val canvas = Canvas(triangleList)
		canvas.isVisible = true

		while (true) {
			canvas.repaint()
		}
	}
}