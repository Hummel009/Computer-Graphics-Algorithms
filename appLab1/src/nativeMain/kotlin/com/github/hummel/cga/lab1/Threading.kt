package com.github.hummel.cga.lab1

import platform.windows.DWORD
import platform.windows.LPVOID

@Suppress("UNUSED_PARAMETER")
fun drawLines1(lpParameter: LPVOID?): DWORD {
	for ((v11, v21, _) in faces) {
		val v1 = vertices[v11 - 1]
		val v2 = vertices[v21 - 1]

		drawLineDDA(
			hdcBack1!!,
			(v1.x * n + 500).toInt(),
			(680 - (v1.y * n) - 550 + (n)).toInt(),
			(v2.x * n + 500).toInt(),
			(680 - (v2.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines2(lpParameter: LPVOID?): DWORD {
	for ((_, v21, v31) in faces) {
		val v2 = vertices[v21 - 1]
		val v3 = vertices[v31 - 1]

		drawLineDDA(
			hdcBack2!!,
			(v2.x * n + 500).toInt(),
			(680 - (v2.y * n) - 550 + (n)).toInt(),
			(v3.x * n + 500).toInt(),
			(680 - (v3.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}

@Suppress("UNUSED_PARAMETER")
fun drawLines3(lpParameter: LPVOID?): DWORD {
	for ((v11, _, v31) in faces) {
		val v1 = vertices[v11 - 1]
		val v3 = vertices[v31 - 1]

		drawLineDDA(
			hdcBack3!!,
			(v3.x * n + 500).toInt(),
			(680 - (v3.y * n) - 550 + (n)).toInt(),
			(v1.x * n + 500).toInt(),
			(680 - (v1.y * n) - 550 + (n)).toInt()
		)
	}
	return 0u
}