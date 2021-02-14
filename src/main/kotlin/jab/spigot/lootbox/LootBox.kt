@file:Suppress("unused")

package jab.spigot.lootbox

import jab.spigot.lootbox.CFGManager.Companion.formatId
import jab.spigot.util.ParticleEffect
import jab.spigot.util.SoundEffect
import jab.spigot.util.WeightTable
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.yaml.snakeyaml.error.YAMLException

/**
 * The <i>LootBox</i> class handles reading and interpreting lootbox data, as well as opening and
 * giving items to players for the LootBox plug-in.
 *
 * @author Jab
 */
class LootBox(val manager: CFGManager, cfg: ConfigurationSection) {

    val lootTable: WeightTable<CFGItem> = WeightTable()
    val id: String = formatId(cfg.name)
    val soundEffect: SoundEffect?
    var rolls = 0
        private set
    var particle: Particle?
        private set
    var item: CFGItem
        private set
    private val itemIdTable: HashMap<String, Int> = HashMap()
    private var cfgLootBoxItem: ConfigurationSection?

    init {
        if (!cfg.contains("lootbox_item")) {
            throw YAMLException("The lootbox '$id' does not have a defined 'lootbox_item'.")
        }
        item = CFGLootBoxItem(this)
        this.cfgLootBoxItem = cfg.getConfigurationSection("lootbox_item")!!

        if (cfg.contains("rolls")) {
            if (!cfg.isInt("rolls")) {
                throw YAMLException(
                    """The field 'rolls' for the lootbox '$id' is not a valid value. Roll counts must be positive 
                        |integers between 1 and $MAX_ROLL_COUNT. (${cfg["rolls"]} given)""".trimMargin())
            }
            rolls = cfg.getInt("rolls")
            if (rolls < 1 || rolls > MAX_ROLL_COUNT) {
                throw YAMLException(
                    ("""The field 'rolls' for the lootbox '$id' is not a valid value. Roll counts must be positive 
                        |integers between 1 and $MAX_ROLL_COUNT. ($rolls given)""".trimMargin()))
            }
        } else {
            rolls = 1
        }

        if (cfg.contains("items")) {
            if (!cfg.isConfigurationSection("items")) {
                throw YAMLException(
                    "The section 'items' is not a valid configured section for the lootbox: $id.")
            }
            val items = cfg.getConfigurationSection("items")
            for (key: String in items!!.getKeys(false)) {
                if (!items.isInt(key)) {
                    throw YAMLException(
                        ("""The item '$key' does not have a valid weight. Weights are supposed to be positive 
                            |integers.""".trimMargin()))
                }
                val weight = items.getInt(key)
                itemIdTable[key] = weight
            }
        }

        if (cfg.contains("on_open")) {
            if (!cfg.isConfigurationSection("on_open")) {
                throw YAMLException("'on_open' for the lootbox '$id' is not a valid configured section.")
            }
            val onOpen = cfg.getConfigurationSection("on_open")
            soundEffect = if (onOpen!!.contains("sound_effect")) {
                if (!onOpen.isConfigurationSection("sound_effect")) {
                    throw YAMLException(("""'on_open.sound_effect' for the lootbox '$id' is not a valid configured
                                | section.""".trimMargin()))
                }
                SoundEffect((onOpen.getConfigurationSection("sound_effect"))!!)
            } else {
                null
            }
            particle = if (onOpen.contains("particle")) {
                if (!onOpen.isString("particle")) {
                    throw YAMLException(
                        "'on_open.particle' for the lootbox '$id' is not a valid string.")
                }
                ParticleEffect.getParticle((onOpen.getString("particle"))!!)
            } else {
                null
            }
        } else {
            soundEffect = null
            particle = null
        }
    }

    /** Internally pairs loaded items to the lootbox.  */
    fun pair() {
        lootTable.clear()
        if (itemIdTable.isNotEmpty()) {
            for (next in itemIdTable.keys) {
                val item: CFGItem? = manager.getItem(next)
                val weight = itemIdTable[next]!!
                lootTable.add(item, weight)
            }
        }
        lootTable.build()
        item.read(cfgLootBoxItem!!)
        cfgLootBoxItem = null
    }

    /**
     * Gives a player the LootBox item.
     *
     * @param player The player to give to.
     * @param amount The amount of the item to give.
     */
    fun give(player: Player, amount: Int) {
        item.give(player, amount)
    }

    fun roll(count: Int): Array<CFGItem?> {
        val rolls = arrayOfNulls<CFGItem>(count)
        for (index in 0 until count) {
            rolls[index] = lootTable.roll()
        }
        return rolls
    }

    fun getName(): String? {
        return this.item.displayName
    }

    companion object {
        const val MAX_ROLL_COUNT = 16
    }
}