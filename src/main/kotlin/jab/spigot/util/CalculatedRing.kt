package jab.spigot.util

import kotlin.math.cos
import kotlin.math.sin

class CalculatedRing(private val segments: Int) : Calculated<Array<FloatArray>>() {

    override fun onCalculate(): Array<FloatArray> {
        val floats = Array(segments) { FloatArray(2) }
        for (index in 0 until segments) {
            val dIndex = index.toDouble() / segments.toDouble() * MathUtils.PI2
            floats[index][0] = cos(dIndex).toFloat()
            floats[index][1] = sin(dIndex).toFloat()
        }
        return floats
    }

    fun transform(
        locX: Float, locY: Float, locZ: Float, rotX: Float, rotY: Float, rotZ: Float, scale: Float,
    ): Array<FloatArray> {
        val original = result!!
        val transformed = Array(original.size) {
            FloatArray(3)
        }
        for (index in original.indices) {
            val x = original[index][0] * scale
            val y = original[index][1] * scale
            transformed[index] = MathUtils.rotateXYZf(x, y, 0.0f, rotX, rotY, rotZ)!!
            transformed[index][0] += locX
            transformed[index][1] += locY
            transformed[index][2] += locZ
        }
        return transformed
    }
}