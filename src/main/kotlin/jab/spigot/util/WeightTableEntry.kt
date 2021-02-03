package jab.spigot.util

/**
 * The <i>WeightTableEntry</i> class handles storing and handling entries with assigned weights for
 * {@link WeightTable weight tables}.
 *
 * @author Jab
 * @param <E> The Type to store in a <i>WeightTable</i>.
 */
@Suppress("MemberVisibilityCanBePrivate")
class WeightTableEntry<E> {
    var value: E?
        private set
    var weight: Int
        private set

    /**
     * @param value The value to store in a WeightTable.
     * @param weight The weight of the value to set in the WeightTable.
     * @throws IllegalArgumentException Thrown if the weight given is less than 1.
     */
    constructor(value: E?, weight: Int) {
        if (weight < 1) {
            throw IllegalArgumentException("The weight given is less than 1. ($weight given)")
        }
        this.value = value
        this.weight = weight
    }

    fun isValue(value: Any?): Boolean {
        return if (value == null) {
            this.value == null
        } else {
            this.value == value;
        }
    }
}
