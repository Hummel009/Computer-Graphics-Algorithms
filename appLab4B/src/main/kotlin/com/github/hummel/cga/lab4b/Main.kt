package com.github.hummel.cga.lab4b

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import java.awt.EventQueue
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import javax.swing.UIManager

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
	const val dist: Double = 3.5

	init {
		try {
			mraoImage = ImageIO.read(File(mraoPath))
			normalMapImage = ImageIO.read(File(normalMapPath))
			textureImage = ImageIO.read(File(texturePath))
		} catch (e: IOException) {
			throw RuntimeException(e)
		}
	}

	@Throws(Exception::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val faceList = Parser.parse(modelPath)

		FlatLightLaf.setup()
		EventQueue.invokeLater {
			try {
				UIManager.setLookAndFeel(FlatGitHubDarkIJTheme())
				val myFrame = MyFrame(faceList)
				myFrame.isVisible = true
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}