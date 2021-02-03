package jab.spigot.util

/**
 * @author Jab
 *
 * @param V The type of value to calculate.
 */
abstract class Calculated<V> {
    var result: V? = null
        private set
    var calculated: Boolean = false
        private set

    /**
     * Compiles the data.
     *
     * @throws RuntimeException Thrown if the data is already calculated.
     */
    fun calculate() {
        if (this.calculated) {
            throw RuntimeException("Already calculated.")
        }
        try {
            this.result = onCalculate()
            this.calculated = true
        } catch (e: Exception) {
            System.err.println("Failed to calculate ${javaClass.simpleName}.")
            e.printStackTrace()
        }
    }

    protected abstract fun onCalculate(): V?
}