package com.github.hummel.cga.lab4b

import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.max
import kotlin.math.pow

object MyGraphics {
	@JvmStatic
	fun applyMatrix(faces: Collection<Face?>, matrix: MyMatrix): List<Face> {
		return faces.asSequence().map { face: Face? ->
			val newFace = Face()
			newFace.normals = face!!.normals
			newFace.textures = face.textures
			val list: MutableList<Vertex> = face.vertices.mapTo(ArrayList()) { matrix.mul(it) }
			newFace.vertices = list.toTypedArray<Vertex>()
			newFace
		}.toList()
	}

	@JvmStatic
	private fun getNormal(t: Face): Vertex {
		var seen = false
		var acc: Vertex? = null
		for (normal in t.normals) {
			if (seen) {
				acc = acc!!.add(normal)
			} else {
				seen = true
				acc = normal
			}
		}
		return (if (seen) Optional.of(acc!!) else Optional.empty()).map { vertex: Vertex? -> vertex!!.div(3.0f) }.get()
	}

	@JvmStatic
	fun filterTriangles(faces: Collection<Face?>?, camera: Camera): List<Face?> {
		return faces!!.asSequence().filter { t: Face? ->
			val viewDir = camera.eye?.let { t!!.vertices[0].subtract(it).normalize() }
			val normal = getNormal(t!!)

			val cos = viewDir?.let { normal.dot(it) }
			cos!! > 0
		}.toList()
	}

	@JvmStatic
	private fun getCenteredVecForPoint(vertices: Array<Vertex>, alpha: Float, beta: Float, gamma: Float): Vertex =
		vertices[0].mul(alpha).add(vertices[1].mul(beta)).add(vertices[2].mul(gamma))

	@JvmStatic
	private fun calculateBarycentricCoordinates(face: Face, x: Float, y: Float): FloatArray {
		val barycentricCoordinates = FloatArray(3)

		val x1 = face.vertices[0][0]
		val y1 = face.vertices[0][1]
		val x2 = face.vertices[1][0]
		val y2 = face.vertices[1][1]
		val x3 = face.vertices[2][0]
		val y3 = face.vertices[2][1]

		val denominator = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3)

		barycentricCoordinates[0] = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator
		barycentricCoordinates[1] = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator
		barycentricCoordinates[2] = 1 - barycentricCoordinates[0] - barycentricCoordinates[1]

		return barycentricCoordinates
	}

	@JvmStatic
	private fun applyBrightness(color: Int, brightness: Float): Int {
		var r = (color and 0x00ff0000) shr 16
		var g = (color and 0x0000ff00) shr 8
		var b = color and 0x000000ff
		r = (r * brightness).toInt()
		g = (g * brightness).toInt()
		b = (b * brightness).toInt()
		return r shl 16 or (g shl 8) or b
	}

	@JvmStatic
	fun drawRasterTriangle(
		bufferedImage: BufferedImage, worldFace: Face, drawFace: Face, zBuffer: FloatArray, camera: Camera
	) {
		var minY = Int.MAX_VALUE
		var maxY = Int.MIN_VALUE
		for (vertex in drawFace.vertices) {
			val y = vertex[1].toInt()
			if (y < minY) {
				minY = y
			}
			if (y > maxY) {
				maxY = y
			}
		}

		// Создать цикл по каждой строке изображения
		for (y in minY..maxY) {
			// Найти пересечения текущей строки с каждой из сторон треугольника
			val xIntersections = IntArray(2)
			var intersectionCount = 0
			for (i in 0..2) {
				val v0 = drawFace.vertices[i]
				val v1 = drawFace.vertices[(i + 1) % 3]
				val y0 = v0[1].toInt()
				val y1 = v1[1].toInt()
				if (y in y0..<y1 || y in y1..<y0) {
					val t = (y - y0) / (y1 - y0).toFloat()
					val x = (v0[0] + t * (v1[0] - v0[0])).toInt()
					xIntersections[intersectionCount] = x
					intersectionCount++
				}
			}

			// Отсортировать пересечения по возрастанию
			if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
				val temp = xIntersections[0]
				xIntersections[0] = xIntersections[1]
				xIntersections[1] = temp
			}

			// Заполнить пиксели между пересечениями цветом треугольника
			if (intersectionCount == 2) {
				for (x in xIntersections[0]..xIntersections[1]) {
					val id = x * windowHeight + y
					if (id < 0 || id >= windowWidth * windowHeight) {
						continue
					}

					// Вычисление z-фрагмента
					val barycCords = calculateBarycentricCoordinates(drawFace, x.toFloat(), y.toFloat())

					val v0 = drawFace.vertices[0]
					val v1 = drawFace.vertices[1]
					val v2 = drawFace.vertices[2]
					var alpha = barycCords[0]
					var beta = barycCords[1]
					var gamma = barycCords[2]
					alpha /= drawFace.depthArr[0]
					beta /= drawFace.depthArr[1]
					gamma /= drawFace.depthArr[2]
					val sum = alpha + beta + gamma
					alpha /= sum
					beta /= sum
					gamma /= sum

					val zFragment = alpha * v0[2] + beta * v1[2] + gamma * v2[2]

					val ambientCoeff = 0.0f
					val diffuseCoeff = 0.4f
					val specularCoeff = 0.2f

					// Проверка z-буфера
					if (zBuffer[x * windowHeight + y] > zFragment) {
						// cчитаем diffuse
						var texVec = getCenteredVecForPoint(worldFace.textures, alpha, beta, gamma)
						texVec = Vertex(texVec[0], 1.0f - texVec[1], 0.0f)
						var texX = (texVec[0] * textureImage.width).toInt() % textureImage.width
						var texY = (texVec[1] * textureImage.height).toInt() % textureImage.height

						if (texX > textureImage.width - 1) {
							texX = textureImage.width - 1
						}
						if (texX < 0) {
							texX = 0
						}
						if (texY > textureImage.width - 1) {
							texY = textureImage.width - 1
						}
						if (texY < 0) {
							texY = 0
						}

						val normalData = normalImage.getRGB(texX, texY)
						val normal = Vertex(
							(normalData shr 16 and 0x000000ff) / 256.0f * 2.0f - 1.0f,
							(normalData shr 8 and 0x000000ff) / 256.0f * 2.0f - 1.0f,
							(normalData and 0x000000ff) / 256.0f * 2.0f - 1.0f
						).mul(-1.0f)

						val mraoData = mraoImage.getRGB(texX, texY)
						val mraoVec = Vertex(
							(mraoData shr 16 and 0x000000ff) / 256.0f,
							(mraoData shr 8 and 0x000000ff) / 256.0f,
							(mraoData and 0x000000ff) / 256.0f
						)

						val pos = getCenteredVecForPoint(worldFace.vertices, alpha, beta, gamma)
						camera.eye?.let { camera.target?.subtract(it)?.normalize() }
						val lightPos = Vertex(5.0f, 5.0f, 5.0f)
						val ray = pos.subtract(lightPos).normalize()
						val diffuse = max(normal.dot(ray) * diffuseCoeff, 0.0f)

						// считаем specular
						var specular = 0.0f
						val l = lightPos.subtract(pos)
						val s = 10.0f
						val angle = normal.dot(l)

						val r = normal.mul(angle).mul(2.0f).subtract(l)
						val v = camera.eye?.subtract(pos)
						val rDotV = max(r.dot(v ?: return), 0.0f)
						if (rDotV > 0) {
							specular = (rDotV / (r.len() * v.len())).pow(s)
						}

						zBuffer[x * windowHeight + y] = zFragment

						val colorValCoeff =
							ambientCoeff + diffuse * diffuseCoeff + specular * mraoVec[0] * specularCoeff

						var texColor = textureImage.getRGB(texX, texY)
						texColor = applyBrightness(texColor, colorValCoeff)

						bufferedImage.setRGB(x, y, texColor)
					}
				}
			}
		}
	}
}
