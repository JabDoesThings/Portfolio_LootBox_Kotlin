package jab.spigot.lootbox

import jab.spigot.util.ColorUtils.Companion.color
import jab.spigot.util.EnchantmentUtils
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.yaml.snakeyaml.error.YAMLException

/**
 * The <i>CFGItem</i> class handles reading and interpreting item data and giving {@link ItemStack
 * ItemStacks} to players for the LootBox plug-in.
 *
 * <p><b>NOTE</b>: Items are required to be loaded outside of the constructor for sub-class
 * implementations.
 *
 * @author Jab
 *
 * @param id The internal ID of the item.
 *     <p><b>NOTE</b>: If the id is null, the id will be the name of the ConfigurationSection
 *     given for {@link CFGItem#read(ConfigurationSection)}.
 */
open class CFGItem(var id: String?) {

    var displayName: String? = null
        private set
    var itemStack: ItemStack? = null
        private set
    private var loaded: Boolean = false

    /**
     * Reads configured data for the item from the config.yml.
     *
     * @param cfg The ConfigurationSection to read.
     */
    fun read(cfg: ConfigurationSection) {

        if (id == null) id = CFGManager.formatId(cfg.name)

        if (!cfg.isString("material_type")) {
            throw YAMLException("""The item "$id" does not have a defined material type.""")
        }
        val typeString = cfg.getString("material_type")
            ?: throw YAMLException("""The item "$id" does not have a defined material type.""")
        var type = Material.matchMaterial(typeString)
        if (type == null) type = Material.matchMaterial(typeString, true)
        if (type == null) {
            throw YAMLException(
                """The item "$id" does not have a valid material type. ("$typeString" given)""")
        }

        this.displayName = if (cfg.isString("display_name")) {
            color(cfg.getString("display_name")!!)
        } else {
            null
        }

        var lore: List<String?>? = if (cfg.isList("lore")) {
            color(cfg.getStringList("lore"))
        } else {
            null
        }

        // Create and assign aesthetics to the ItemStack.
        val itemStack = ItemStack(type)
        val itemMeta = itemStack.itemMeta!!
        itemMeta.setLocalizedName(id)
        itemMeta.setDisplayName(displayName)
        lore = preProcessLore(lore)
        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        this.itemStack = itemStack

        // Add any defined Enchantments.
        if (cfg.contains("enchantments")) {
            if (!cfg.isConfigurationSection("enchantments")) {
                throw YAMLException(
                    ("""The 'enchantments' section of the item "$id" is not a valid configured section."""))
            }
            val enchantments = cfg.getConfigurationSection("enchantments")!!
            for (key: String in enchantments.getKeys(false)) {
                val enchantment = EnchantmentUtils.getEnchantment(key)
                    ?: throw YAMLException(
                        """The enchantment "$key" for the item "$id" is an unknown enchantment.""")
                if (!enchantments.isInt(key)) {
                    throw YAMLException(
                        ("""The enchantment "$key" for the item "$id" is not a valid Integer value. (0-10)"""))
                }
                val amplifier = enchantments.getInt(key)
                itemStack.addUnsafeEnchantment(enchantment, amplifier)
            }
        }
        loaded = true
    }

    /**
     * @param amount The amount to calculate.
     * @return Returns the amount of item slots required to represent the amount given.
     */
    fun getSlotCount(amount: Int): Int {
        var amount2 = amount
        checkIfLoaded()
        var count = 0
        val stackSize = itemStack!!.type.maxStackSize
        while (amount2 > 0) {
            amount2 -= stackSize
            count++
        }
        return count
    }

    /**
     * Gives the item to a player.
     *
     *
     * **NOTE**: If the inventory is full, items may not be given to the Player.
     *
     * @param player The player to give the item to.
     * @param amount The amount of the item to give.
     */
    fun give(player: Player, amount: Int) {
        var amount2 = amount
        checkIfLoaded()
        if (amount2 == 0) return
        if (!player.isOnline) return
        val playerInventory = player.inventory
        val maxStackSize = itemStack!!.type.maxStackSize
        while (amount2 > maxStackSize) {
            amount2 -= maxStackSize
            val itemStack = itemStack!!.clone()
            itemStack.amount = maxStackSize
            playerInventory.addItem(itemStack)
        }
        if (amount2 > 0) {
            val itemStack = itemStack!!.clone()
            itemStack.amount = amount2
            player.inventory.addItem(itemStack)
        }
    }

    private fun checkIfLoaded() {
        if (!loaded) throw RuntimeException("The item '$id'is not loaded.")
    }

    /**
     * This method allows for sub-classes to modify lore defined when read from a
     * ConfigurationSection.
     *
     * @param lore The lore to modify.
     * @return Returns the modified lore.
     */
    protected open fun preProcessLore(lore: List<String?>?): List<String?>? {
        return lore
    }
}