package jab.spigot.util

import java.util.*
import kotlin.collections.ArrayList

class WeightTable<E> {
    private var entries: MutableList<WeightTableEntry<E>> = mutableListOf()
    private var built: Boolean = false
    private var table = ArrayList<E?>()
    private var random: Random? = null

    init {
        this.clear()
    }

    companion object {
        val DEFAULT_RANDOM = Random()
    }

    @Suppress("UNCHECKED_CAST")
    fun roll(): E? {
        if (!this.built) {
            this.build()
        }
        if (this.table.isEmpty()) {
            return null
        } else if (this.table.size == 1) {
            return this.table[0]
        }

        val random = (if (this.random != null) this.random else DEFAULT_RANDOM) ?: return null
        return table[random.nextInt(table.size)]
    }

    /** Builds the table. If the table is up to-date, nothing is altered. */
    fun build() {
        if (this.built) {
            return
        }
        if (this.entries.isEmpty()) {
            this.table = ArrayList()
            return
        }
        var sum = 0
        for (next in entries) {
            sum += next.weight
        }
        this.table = ArrayList()
        for (next in entries) {
            val value = next.value
            for (index in 0..next.weight) {
                this.table.add(value)
            }
        }
        built = true
    }

    /**
     * @return Returns all stored values in the weight table.
     *     <p><b>NOTE</b>: Duplicate values are pruned.
     */
    @Suppress("UNCHECKED_CAST")
    fun getValues(): Array<E?> {
        var array: Array<Any> = emptyArray()
        for (next in this.entries) {
            var found = false
            for (e in array) {
                if (next.isValue(e)) {
                    found = true
                    break
                }
            }
            if (!found) {
                array = array.plus(next)
            }
        }
        return array as Array<E?>
    }

    /**
     * Adds a entry to the WeightTable.
     *
     * <p><b>NOTE</b>: The table will need to be rebuilt to roll.
     *
     * @param entry The entry to add to the WeightTable.
     */
    fun add(entry: WeightTableEntry<E>) {
        this.entries.add(entry)
        this.built = false
    }

    /**
     * Adds a value to the WeightTable with a assigned weight value.
     *
     * <p><b>NOTE</b>: The table will need to be rebuilt to roll.
     *
     * @param value The value to add to the WeightTable.
     * @param weight The weight to assign to the value when the WeightTable is built.
     * @return Returns the result entry created for the value and weight assignment.
     */
    fun add(value: E?, weight: Int): WeightTableEntry<E> {
        val entry = WeightTableEntry(value, weight)
        this.entries.add(entry)
        this.built = false
        return entry
    }

    /**
     * Removes all entries from the WeightTable that has a set value equal to the one given.
     *
     * <p><b>NOTE</b>: The table will need to be rebuilt to roll.
     *
     * @param value The value to remove.
     * @param all Set to true if you want any duplicate entries removed from the WeightTable.
     */
    fun remove(value: E?, all: Boolean) {
        if (all) {
            this.built = !this.entries.removeIf { next: WeightTableEntry<E>? -> next!!.isValue(value) }
        } else {
            val iterator = this.entries.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.isValue(value)) {
                    iterator.remove()
                    this.built = false
                    break
                }
            }
        }
    }

    /**
     * Removes a entry from the WeightTable.
     *
     * <p><b>NOTE</b>: The table will need to be rebuilt to roll.
     *
     * @param entry The entry to remove.
     * @param all Set to true if you want any duplicate entries removed from the WeightTable.
     */
    fun remove(entry: WeightTableEntry<E>, all: Boolean) {
        if (all) {
            while (this.entries.contains(entry)) {
                this.entries.remove(entry)
                this.built = false
            }
        } else {
            this.built = !this.entries.remove(entry)
        }
    }

    /** Clears the WeightTable of all registered entries and clears the built table. */
    fun clear() {
        this.entries.clear()
        this.table = ArrayList()
        this.built = false
    }

    /**
     * @param entry The entry to test.
     * @return Returns true if the weight table contains the entry.
     */
    fun containsEntry(entry: WeightTableEntry<E>): Boolean {
        return this.entries.contains(entry)
    }

    /**
     * @param value The value to test.
     * @return Returns true if one or more entries for the WeightTable stores a value equal to the one
     *     given.
     */
    fun contains(value: E?): Boolean {
        for (next in this.entries) {
            if (next.isValue(value)) {
                return true
            }
        }
        return false
    }

    /**
     * @param value The value to match.
     * @return Returns all entries for the WeightTable that stores a value equal to the one given as a
     *     List.
     */
    fun get(value: E?): List<WeightTableEntry<E>> {
        return this.entries.filter { entry: WeightTableEntry<E> -> entry.isValue(value) }
    }

    /**
     * @param value The value to check.
     * @return Returns the percentage chance of the value rolling in the weight table.
     */
    fun getChance(value: E?): Double {
        if (!this.built) {
            build()
        }

        val entries = get(value)
        var sum = 0
        for (next in entries) {
            sum += next.weight
        }

        return sum.toDouble() / this.table.size.toDouble()
    }

    /**
     * @param entry The entry to check.
     * @return Returns the percentage chance of the entry rolling in the weight table.
     */
    fun getChance(entry: WeightTableEntry<E>): Double {
        if (!this.built) {
            build()
        }

        return entry.weight.toDouble() / this.table.size.toDouble()
    }

    /** @return Returns the amount of entries to build in the WeightTable. */
    fun entrySize(): Int {
        return this.entries.size
    }

    /**
     * @return Returns the size of the built table. (The sum of all weights). If the table is not
     * built, -1 is returned.
     */
    fun tableSize(): Int {
        return if (built) table.size else -1
    }
}