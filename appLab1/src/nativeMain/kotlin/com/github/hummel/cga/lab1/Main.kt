package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.windows.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

var rotationAngleX: Float = 0.0f
const val rotationSpeedX: Float = 0.2f

var rotationAngleY: Float = 0.0f
const val rotationSpeedY: Float = 0.2f

var rotationAngleZ: Float = 0.0f
const val rotationSpeedZ: Float = 0.2f

const val width: Int = 960
const val height: Int = 540

val vertices: ArrayList<Vertex> = ArrayList()
val faces: ArrayList<Face> = ArrayList()

var hdcBack: HDC? = null
var hbmBack: HBITMAP? = null

const val VK_Z: Int = 0x5A
const val VK_X: Int = 0x58
const val VK_C: Int = 0x43

fun main() {
	memScoped {
		val className = "RedSquare"
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

		val windowWidth = width
		val windowHeight = height

		val windowX = max(0, (screenWidth - windowWidth) / 2)
		val windowY = max(0, (screenHeight - windowHeight) / 2)

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

		val file = fopen("D:\\teapot.obj", "r")
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
	memScoped {
		when (msg.toInt()) {
			WM_CREATE -> {
				initializeBackBuffer(window, 1040, 580)
			}

			WM_KEYDOWN -> {
				when (wParam.toInt()) {
					VK_LEFT, VK_X -> {
						rotateModelX()
						InvalidateRect(window, null, FALSE)
					}

					VK_UP, VK_C -> {
						rotateModelY()
						InvalidateRect(window, null, FALSE)
					}

					VK_RIGHT, VK_Z -> {
						rotateModelZ()
						InvalidateRect(window, null, FALSE)
					}

					else -> {}
				}
			}

			WM_PAINT -> {
				memScoped {
					val ps = alloc<PAINTSTRUCT>()
					PatBlt(hdcBack, 0, 0, 1040, 580, WHITENESS)
					for ((v11, v21, v31) in faces) {
						val v1 = vertices[v11 - 1]
						val v2 = vertices[v21 - 1]
						val v3 = vertices[v31 - 1]

						drawLineDDA(
							hdcBack!!,
							(v1.x * n + 500).toInt(),
							(680 - (v1.y * n) - 550 + (n)).toInt(),
							(v2.x * n + 500).toInt(),
							(680 - (v2.y * n) - 550 + (n)).toInt()
						)
						drawLineDDA(
							hdcBack!!,
							(v2.x * n + 500).toInt(),
							(680 - (v2.y * n) - 550 + (n)).toInt(),
							(v3.x * n + 500).toInt(),
							(680 - (v3.y * n) - 550 + (n)).toInt()
						)
						drawLineDDA(
							hdcBack!!,
							(v3.x * n + 500).toInt(),
							(680 - (v3.y * n) - 550 + (n)).toInt(),
							(v1.x * n + 500).toInt(),
							(680 - (v1.y * n) - 550 + (n)).toInt()
						)
					}

					val hdc = BeginPaint(window, ps.ptr)
					BitBlt(hdc, 0, 0, 1040, 580, hdcBack, 0, 0, SRCCOPY)
					EndPaint(window, ps.ptr)
				}
			}

			WM_SIZE -> {
				finalizeBackBuffer()
				initializeBackBuffer(window, 1040, 580)
			}

			WM_CLOSE -> DestroyWindow(window)

			WM_DESTROY -> {
				finalizeBackBuffer()
				PostQuitMessage(0)
			}

			else -> {}
		}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}

var n: Int = 100
fun drawLineDDA(hdc: HDC, x1: Int, y1: Int, x2: Int, y2: Int) {
	val dx = x2 - x1
	val dy = y2 - y1
	val steps = if (abs(dx) > abs(dy)) abs(dx) else abs(dy)
	val xIncrement = dx.toFloat() / steps.toFloat()
	val yIncrement = dy.toFloat() / steps.toFloat()
	var x = x1.toFloat()
	var y = y1.toFloat()
	for (i in 0..steps step 2) {
		SetPixel(hdc, x.toInt(), y.toInt(), 0u)
		x += xIncrement
		y += yIncrement
	}
}

fun rotateModelX() {
	for (vertex in vertices) {
		val y = vertex.y
		val z = vertex.z
		vertex.y = y * cos(rotationSpeedX) - z * sin(rotationSpeedX)
		vertex.z = y * sin(rotationSpeedX) + z * cos(rotationSpeedX)
	}
	rotationAngleX += rotationSpeedX
}

fun rotateModelY() {
	for (vertex in vertices) {
		val x = vertex.x
		val z = vertex.z
		vertex.x = x * cos(rotationSpeedY) + z * sin(rotationSpeedY)
		vertex.z = -x * sin(rotationSpeedY) + z * cos(rotationSpeedY)
	}
	rotationAngleY += rotationSpeedY
}

fun rotateModelZ() {
	for (vertex in vertices) {
		val x = vertex.x
		val y = vertex.y
		vertex.x = x * cos(rotationSpeedZ) - y * sin(rotationSpeedZ)
		vertex.y = x * sin(rotationSpeedZ) + y * cos(rotationSpeedZ)
	}
	rotationAngleZ += rotationSpeedZ
}

fun initializeBackBuffer(hWnd: HWND?, w: Int, h: Int) {
	val hdcWindow = GetDC(hWnd)

	hdcBack = CreateCompatibleDC(hdcWindow)
	hbmBack = CreateCompatibleBitmap(hdcWindow, w, h)
	SaveDC(hdcBack)
	SelectObject(hdcBack, hbmBack)

	ReleaseDC(hWnd, hdcWindow)
}

fun finalizeBackBuffer() {
	hdcBack?.let {
		RestoreDC(hdcBack, -1)
		DeleteObject(hbmBack)
		DeleteDC(hdcBack)
		hdcBack = null
	}
}