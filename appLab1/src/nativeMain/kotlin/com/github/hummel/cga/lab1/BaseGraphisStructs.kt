package com.github.hummel.cga.lab1

abstract class Vector {
	protected var length: Int
	protected var coordinates: FloatArray

	constructor() {
		coordinates = floatArrayOf(0f, 0f, 0f)
		length = coordinates.size
	}

	constructor(a: Float, b: Float, c: Float) {
		coordinates = floatArrayOf(a, b, c)
		length = coordinates.size
	}

	constructor(vector: Vector) {
		coordinates = vector.coordinates
		length = vector.length
	}

	operator fun get(index: Int): Float = coordinates[index]

	operator fun set(index: Int, value: Float) {
		coordinates[index] = value
	}
}

open class CoordinateVector : Vector {
	var x: Float
		get() = coordinates[0]
		set(value) {
			coordinates[0] = value
		}

	var y: Float
		get() = coordinates[1]
		set(value) {
			coordinates[1] = value
		}

	var z: Float
		get() = coordinates[2]
		set(value) {
			coordinates[2] = value
		}

	constructor() : super()

	constructor(x: Float, y: Float, z: Float) : super(x, y, z)

	constructor(vector: CoordinateVector) : super(vector)
}

class GeometricVertex : CoordinateVector {
	var w: Float
		get() = coordinates[3]
		set(value) {
			coordinates[3] = value
		}

	var TranslateX: Float
		get() = coordinates[4]
		set(value) {
			coordinates[4] = value
		}

	var TranslateY: Float
		get() = coordinates[5]
		set(value) {
			coordinates[5] = value
		}

	var TranslateZ: Float
		get() = coordinates[6]
		set(value) {
			coordinates[6] = value
		}

	constructor() : super() {
		coordinates = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
		length = coordinates.size
	}

	constructor(x: Float, y: Float, z: Float, w: Float) : super(x, y, z) {
		coordinates = floatArrayOf(x, y, z, w, 0f, 0f, 0f)
		length = coordinates.size
	}

	constructor(vertex: GeometricVertex) : super(vertex)
}

class TextureVertice : Vector {
	var U: Float
		get() = coordinates[0]
		set(value) {
			coordinates[0] = value
		}

	var V: Float
		get() = coordinates[1]
		set(value) {
			coordinates[1] = value
		}

	var W: Float
		get() = coordinates[2]
		set(value) {
			coordinates[2] = value
		}

	constructor() : super()

	constructor(u: Float, v: Float, w: Float) : super(u, v, w)

	constructor(vertice: TextureVertice) : super(vertice)
}

class NormalVertice : Vector {
	var I: Float
		get() = coordinates[0]
		set(value) {
			coordinates[0] = value
		}

	var J: Float
		get() = coordinates[1]
		set(value) {
			coordinates[1] = value
		}

	var K: Float
		get() = coordinates[2]
		set(value) {
			coordinates[2] = value
		}

	constructor() : super()

	constructor(i: Float, j: Float, k: Float) : super(i, j, k)

	constructor(vertice: NormalVertice) : super(vertice)
}