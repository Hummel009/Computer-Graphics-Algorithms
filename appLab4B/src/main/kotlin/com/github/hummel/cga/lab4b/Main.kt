package com.github.hummel.cga.lab4b

import com.github.hummel.cga.lab4b.ParsingHelper.extractNormal
import com.github.hummel.cga.lab4b.ParsingHelper.extractTexture
import com.github.hummel.cga.lab4b.ParsingHelper.extractTriangle
import com.github.hummel.cga.lab4b.ParsingHelper.extractVertex
import java.awt.image.BufferedImage
import java.io.File
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
		val vertexList: MutableList<Vertex?> = ArrayList()
		val normalList: MutableList<Vertex> = ArrayList()
		val textureList: MutableList<Vertex?> = ArrayList()
		val faceList: MutableList<Face?> = ArrayList()

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
					faceList.add(extractTriangle(line, vertexList, textureList, normalList))
				}

				line = reader.readLine()
			}
		}
		val canvas = Canvas(faceList)
		canvas.isVisible = true

		while (true) {
			canvas.repaint()
		}
	}
}