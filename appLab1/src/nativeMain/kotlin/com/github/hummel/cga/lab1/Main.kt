package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.windows.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round
import kotlin.time.measureTime

const val width: Int = 1040
const val height: Int = 580

const val VK_Z: Int = 0x5A
const val VK_X: Int = 0x58
const val VK_C: Int = 0x43

val vertices: ArrayList<Vertex> = ArrayList()
val faces: ArrayList<Face> = ArrayList()

var bitmapData: ByteArray = ByteArray(width * height * 4)

fun main() {
	memScoped {
		val className = "Teapot"
		val windowTitle = "Windows API: Kotlin Native"

		val windowClass = alloc<WNDCLASS>()
		windowClass.style = 0u
		windowClass.lpfnWndProc = staticCFunction(::wndProc)
		windowClass.cbClsExtra = 0
		windowClass.cbWndExtra = 0
		windowClass.hInstance = null
		windowClass.hIcon = null
		windowClass.hCursor = null
		windowClass.hbrBackground = (COLOR_WINDOW + 1).toLong().toCPointer()
		windowClass.lpszMenuName = null
		windowClass.lpszClassName = className.wcstr.ptr

		RegisterClassW(windowClass.ptr)

		val screenWidth = GetSystemMetrics(SM_CXSCREEN)
		val screenHeight = GetSystemMetrics(SM_CYSCREEN)

		val windowX = max(0, (screenWidth - width) / 2)
		val windowY = max(0, (screenHeight - height) / 2)

		CreateWindowExW(
			0u,
			className,
			windowTitle,
			(WS_VISIBLE or WS_CAPTION or WS_SYSMENU).toUInt(),
			windowX,
			windowY,
			width,
			height,
			null,
			null,
			null,
			null
		)

		val file = fopen("teapot.obj", "r")
		val bufferLength = 1024
		val buffer = ByteArray(bufferLength)

		while (fgets(buffer.refTo(0), bufferLength, file) != null) {
			val line = buffer.toKString()
			val array = line.trim().split("\\s+".toRegex()).toTypedArray()

			when (array[0]) {
				"v" -> {
					val vertex = Vertex(
						array[1].toFloat(), array[2].toFloat() - 1.5f, array[3].toFloat()
					)
					vertices.add(vertex)
				}

				"f" -> {
					val face = Face(
						array[1].split("/")[0].toInt(), array[2].split("/")[0].toInt(), array[3].split("/")[0].toInt()
					)
					faces.add(face)
				}
			}
		}

		fclose(file)

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}
	}
}

private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	when (msg.toInt()) {
		WM_CREATE -> {
		}

		WM_KEYDOWN -> {
			when (wParam.toInt()) {
				VK_Z -> {
					rotateVerticesAroundZ()
					InvalidateRect(window, null, FALSE)
				}

				VK_X -> {
					rotateVerticesAroundX()
					InvalidateRect(window, null, FALSE)
				}

				VK_C -> {
					rotateVerticesAroundY()
					InvalidateRect(window, null, FALSE)
				}

				VK_LEFT -> {
					translateVertices(-0.05f, 0.0f)
					InvalidateRect(window, null, FALSE)
				}

				VK_RIGHT -> {
					translateVertices(0.05f, 0.0f)
					InvalidateRect(window, null, FALSE)
				}

				VK_UP -> {
					translateVertices(0.0f, 0.05f)
					InvalidateRect(window, null, FALSE)
				}

				VK_DOWN -> {
					translateVertices(0.0f, -0.05f)
					InvalidateRect(window, null, FALSE)
				}

				VK_OEM_PLUS, VK_ADD -> {
					scaleVertices(1.1f)
					InvalidateRect(window, null, FALSE)
				}

				VK_OEM_MINUS, VK_SUBTRACT -> {
					scaleVertices(1 / 1.1f)
					InvalidateRect(window, null, FALSE)
				}

				else -> {}
			}
		}

		WM_PAINT -> {
			memScoped {
				val time = measureTime {
					val ps = alloc<PAINTSTRUCT>()
					val hdc = BeginPaint(window, ps.ptr)
					val hdcMem = CreateCompatibleDC(hdc)

					//MAKE IT WHITE
					for (y in 0 until height) {
						for (x in 0 until width) {
							val offset = (y * width + x) * 4
							bitmapData[offset + 0] = 255.toByte() // BLUE
							bitmapData[offset + 1] = 255.toByte() // GREEN
							bitmapData[offset + 2] = 255.toByte() // RED
							bitmapData[offset + 3] = 255.toByte() // ALPHA
						}
					}

					for ((v11, v21, _) in faces) {
						val v1 = vertices[v11 - 1]
						val v2 = vertices[v21 - 1]

						drawLineDDA(
							(v1.x * n + 500).toInt(),
							(680 - (v1.y * n) - 550 + (n)).toInt(),
							(v2.x * n + 500).toInt(),
							(680 - (v2.y * n) - 550 + (n)).toInt()
						)
					}

					for ((_, v21, v31) in faces) {
						val v2 = vertices[v21 - 1]
						val v3 = vertices[v31 - 1]

						drawLineDDA(
							(v2.x * n + 500).toInt(),
							(680 - (v2.y * n) - 550 + (n)).toInt(),
							(v3.x * n + 500).toInt(),
							(680 - (v3.y * n) - 550 + (n)).toInt()
						)
					}

					for ((v11, _, v31) in faces) {
						val v1 = vertices[v11 - 1]
						val v3 = vertices[v31 - 1]

						drawLineDDA(
							(v3.x * n + 500).toInt(),
							(680 - (v3.y * n) - 550 + (n)).toInt(),
							(v1.x * n + 500).toInt(),
							(680 - (v1.y * n) - 550 + (n)).toInt()
						)
					}

					val hBitmap = CreateBitmap(width, height, 1u, 32u, bitmapData.refTo(0))
					val hOldBitmap = SelectObject(hdcMem, hBitmap)

					BitBlt(hdc, ps.rcPaint.left, ps.rcPaint.top, width, height, hdcMem, 0, 0, SRCCOPY)

					SelectObject(hdcMem, hOldBitmap)
					DeleteObject(hBitmap)

					DeleteDC(hdcMem)
					EndPaint(window, ps.ptr)
				}.inWholeNanoseconds
				println("Draw: $time")
			}
		}

		WM_CLOSE -> DestroyWindow(window)

		WM_DESTROY -> PostQuitMessage(0)

		else -> {}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

var n: Int = 100
fun drawLineDDA(x1: Int, y1: Int, x2: Int, y2: Int) {
	val dx = x2 - x1
	val dy = y2 - y1
	val steps = max(abs(dx), abs(dy))

	val xIncrement = dx / steps.toFloat()
	val yIncrement = dy / steps.toFloat()

	var x = x1.toFloat()
	var y = y1.toFloat()

	for (i in 0..steps) {
		//IF THE OBJECT IS OUT OF BOUNDS, IT SHOULDN'T BE DISPLAYED
		if (x > width - 1 || x < 0 || y > height - 1 || y < 0) {
			x += xIncrement
			y += yIncrement
		} else {
			val index = (round(y).toInt() * width + round(x).toInt()) * 4

			bitmapData[index + 0] = 0.toByte() // BLUE
			bitmapData[index + 1] = 0.toByte() // GREEN
			bitmapData[index + 2] = 0.toByte() // RED
			bitmapData[index + 3] = 255.toByte() // ALPHA

			x += xIncrement
			y += yIncrement
		}
	}
}