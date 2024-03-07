package com.github.hummel.cga.lab1

import kotlinx.cinterop.*
import platform.windows.*
import kotlin.math.max
import kotlin.time.measureTime

private const val VK_Z: Int = 0x5A
private const val VK_X: Int = 0x58
private const val VK_C: Int = 0x43

const val width: Int = 1040
const val height: Int = 580

var faces: MutableList<Face> = ArrayList()
var bitmapData: ByteArray = ByteArray(width * height * 4)

lateinit var name: String

fun main() {
	print("Enter model name (tie|mace): ")
	name = readln()

	parse("$name.obj")

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

		val msg = alloc<MSG>()
		while (GetMessageW(msg.ptr, null, 0u, 0u) != 0) {
			TranslateMessage(msg.ptr)
			DispatchMessageW(msg.ptr)
		}
	}
}

private val times: MutableList<Long> = ArrayList()
private var min: Long = Long.MAX_VALUE
private var max: Long = Long.MIN_VALUE
private fun wndProc(window: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
	when (msg.toInt()) {
		WM_KEYDOWN -> {
			when (wParam.toInt()) {
				VK_Z -> {
					rotateVerticesAxisZ()
					InvalidateRect(window, null, FALSE)
				}

				VK_X -> {
					rotateVerticesAxisX()
					InvalidateRect(window, null, FALSE)
				}

				VK_C -> {
					rotateVerticesAxisY()
					InvalidateRect(window, null, FALSE)
				}

				else -> {}
			}
		}

		WM_PAINT -> {
			val time = measureTime {
				memScoped {
					val ps = alloc<PAINTSTRUCT>()
					val hdc = BeginPaint(window, ps.ptr)
					val hdcMem = CreateCompatibleDC(hdc)

					renderObject()

					val hBitmap = CreateBitmap(width, height, 1u, 32u, bitmapData.refTo(0))
					val hOldBitmap = SelectObject(hdcMem, hBitmap)

					BitBlt(hdc, 0, 0, width, height, hdcMem, 0, 0, SRCCOPY)

					SelectObject(hdcMem, hOldBitmap)
					DeleteObject(hBitmap)

					DeleteDC(hdcMem)
					EndPaint(window, ps.ptr)
				}
			}.inWholeNanoseconds

			times.add(time)

			if (times.size >= 50) {
				val fps = (1000000000.0 / times.takeLast(50).average()).toLong()

				min = min.coerceAtMost(fps)
				max = max.coerceAtLeast(fps)

				println("$fps FPS, [$min; $max]")
			}
		}

		WM_CLOSE -> DestroyWindow(window)

		WM_DESTROY -> PostQuitMessage(0)

		else -> {}
	}
	return DefWindowProcW(window, msg, wParam, lParam)
}