package jab.spigot.lootbox

import jab.spigot.util.WeightTableEntry
import java.util.*

/**
 * The <i>CFGLootBoxItem</i> class handles inserting '%table%' into the lore of a LootBox item.
 *
 * @author Jab
 *
 * @param lootBox The lootbox instance.
 */
class CFGLootBoxItem(private val lootBox: LootBox) : CFGItem(lootBox.id) {

    init {

    }

    override fun preProcessLore(lore: List<String?>?): List<String?>? {

        if (lore == null) {
            return null
        }


        val newLore: MutableList<String> = ArrayList()
        val table = lootBox.lootTable
        // Order all items by ascending rarity.
        val items: List<WeightTableEntry<CFGItem>> = ArrayList(table.entries).sortedByDescending { it.weight }

        for (next in lore) {

            if (next == null || next.isEmpty()) {
                newLore.add("")
                continue
            } else if (!next.contains("%table%")) {
                // If not %table%, then add as a normal line.
                newLore.add(next)
                continue
            }

            // Process each item, adding a line in the lore.
            for (entry in items) {
                val item = entry.value ?: continue
                val chance = table.getChance(entry) * 100.0
                newLore.add(lootBox.manager.processTableLine(item, chance))
            }
        }
        return newLore
    }
}