package hummel

infix fun List<FloatArray>.of(i: Int): FloatArray = this[i - 1]
infix fun Float.transformX(pair: Pair<Float, Float>): Int = ((this - pair.first) * pair.second).toInt()
infix fun Float.transformY(pair: Pair<Float, Float>): Int = ((this - pair.first) * pair.second).toInt()
infix fun Int.invertAxisY(int: Int): Int = this - int