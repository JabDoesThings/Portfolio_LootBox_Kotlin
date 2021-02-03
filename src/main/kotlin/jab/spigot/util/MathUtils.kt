package jab.spigot.util

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/** @author Jab */
class MathUtils {
    companion object {
        val PI2 = Math.PI * 2
        val PID2 = (Math.PI / 2.0).toFloat()

        fun lerp(start: Double, stop: Double, percent: Double): Double {
            return if (start == stop) start else start + percent * (stop - start)
        }

        fun easeInOut(t: Double): Double {
            return if (t > 0.5) 4 * (t - 1).pow(3.0) + 1 else 4 * t.pow(3.0)
        }

        fun easeIn(t: Double): Double {
            return 1.0 - cos(t * Math.PI * 0.5)
        }

        fun easeOut(t: Double): Double {
            return sin(t * Math.PI * 0.5)
        }

        fun rotateXYZ(loc: DoubleArray, rot: DoubleArray): DoubleArray? {
            return if (loc[0] == 0.0 && loc[1] == 0.0 && loc[2] == 0.0) doubleArrayOf(0.0, 0.0, 0.0) else rotateZ(
                rotateY(
                    rotateX(
                        loc[0], loc[1], loc[2], rot[0]),
                    rot[1]),
                rot[2])
        }

        fun rotateXYZ(x: Double, y: Double, z: Double, rx: Double, ry: Double, rz: Double): DoubleArray? {
            return if (x == 0.0 && y == 0.0 && z == 0.0) doubleArrayOf(0.0, 0.0, 0.0) else rotateZ(rotateY(rotateX(x,
                y,
                z,
                rx), ry), rz)
        }

        fun rotateX(loc: DoubleArray, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(loc[0], cos * loc[1] + -sin * loc[2], sin * loc[1] + cos * loc[2])
        }

        fun rotateY(loc: DoubleArray, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(cos * loc[0] + sin * loc[2], loc[1], -sin * loc[0] + cos * loc[2])
        }

        fun rotateZ(loc: DoubleArray, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(cos * loc[0] + -sin * loc[1], sin * loc[0] + cos * loc[1], loc[2])
        }

        fun rotateX(x: Double, y: Double, z: Double, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(x, cos * y + -sin * z, sin * y + cos * z)
        }

        fun rotateY(x: Double, y: Double, z: Double, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(cos * x + sin * z, y, -sin * x + cos * z)
        }

        fun rotateZ(x: Double, y: Double, z: Double, radians: Double): DoubleArray {
            val cos = cos(radians)
            val sin = sin(radians)
            return doubleArrayOf(cos * x + -sin * y, sin * x + cos * y, z)
        }

        fun rotateXYZf(loc: FloatArray, rot: FloatArray): FloatArray? {
            return if (loc[0] == 0.0f && loc[1] == 0.0f && loc[2] == 0.0f) floatArrayOf(0f, 0f, 0f) else rotateZf(
                rotateYf(
                    rotateXf(
                        loc[0], loc[1], loc[2], rot[0]),
                    rot[1]),
                rot[2])
        }

        fun rotateXYZf(x: Float, y: Float, z: Float, rx: Float, ry: Float, rz: Float): FloatArray? {
            return if (x == 0f && y == 0f && z == 0f) floatArrayOf(0f, 0f, 0f) else rotateZf(rotateYf(rotateXf(x,
                y,
                z,
                rx),
                ry), rz)
        }

        fun rotateXf(loc: FloatArray, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(loc[0], cos * loc[1] + -sin * loc[2], sin * loc[1] + cos * loc[2])
        }

        fun rotateYf(loc: FloatArray, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(cos * loc[0] + sin * loc[2], loc[1], -sin * loc[0] + cos * loc[2])
        }

        fun rotateZf(loc: FloatArray, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(cos * loc[0] + -sin * loc[1], sin * loc[0] + cos * loc[1], loc[2])
        }

        fun rotateXf(x: Float, y: Float, z: Float, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(x, cos * y + -sin * z, sin * y + cos * z)
        }

        fun rotateYf(x: Float, y: Float, z: Float, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(cos * x + sin * z, y, -sin * x + cos * z)
        }

        fun rotateZf(x: Float, y: Float, z: Float, radians: Float): FloatArray {
            val cos = cos(radians.toDouble()).toFloat()
            val sin = sin(radians.toDouble()).toFloat()
            return floatArrayOf(cos * x + -sin * y, sin * x + cos * y, z)
        }
    }
}