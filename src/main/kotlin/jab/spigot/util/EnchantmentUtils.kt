package jab.spigot.util

import org.bukkit.enchantments.Enchantment
import java.util.*

/**
 * The <i>EnchantmentUtils</i> class handles mapping identities for Bukkit enchantments.
 *
 * @author Jab
 */
class EnchantmentUtils {
    companion object {
        private val enchantments: HashMap<String, Enchantment> = HashMap()

        init {
            for (next in Enchantment.values()) {
                enchantments[next.key.key.toUpperCase()] = next
            }
            enchantments["PROTECTION_ENVIRONMENTAL"] = Enchantment.PROTECTION_ENVIRONMENTAL
            enchantments["PROTECTION_FIRE"] = Enchantment.PROTECTION_FIRE
            enchantments["PROTECTION_FALL"] = Enchantment.PROTECTION_FALL
            enchantments["PROTECTION_EXPLOSIONS"] = Enchantment.PROTECTION_EXPLOSIONS
            enchantments["PROTECTION_PROJECTILE"] = Enchantment.PROTECTION_PROJECTILE
            enchantments["OXYGEN"] = Enchantment.OXYGEN
            enchantments["WATER_WORKER"] = Enchantment.WATER_WORKER
            enchantments["THORNS"] = Enchantment.THORNS
            enchantments["DEPTH_STRIDER"] = Enchantment.DEPTH_STRIDER
            enchantments["FROST_WALKER"] = Enchantment.FROST_WALKER
            enchantments["BINDING_CURSE"] = Enchantment.BINDING_CURSE
            enchantments["DAMAGE_ALL"] = Enchantment.DAMAGE_ALL
            enchantments["DAMAGE_UNDEAD"] = Enchantment.DAMAGE_UNDEAD
            enchantments["DAMAGE_ARTHROPODS"] = Enchantment.DAMAGE_ARTHROPODS
            enchantments["KNOCKBACK"] = Enchantment.KNOCKBACK
            enchantments["FIRE_ASPECT"] = Enchantment.FIRE_ASPECT
            enchantments["LOOT_BONUS_MOBS"] = Enchantment.LOOT_BONUS_MOBS
            enchantments["SWEEPING_EDGE"] = Enchantment.SWEEPING_EDGE
            enchantments["DIG_SPEED"] = Enchantment.DIG_SPEED
            enchantments["SILK_TOUCH"] = Enchantment.SILK_TOUCH
            enchantments["DURABILITY"] = Enchantment.DURABILITY
            enchantments["LOOT_BONUS_BLOCKS"] = Enchantment.LOOT_BONUS_BLOCKS
            enchantments["ARROW_DAMAGE"] = Enchantment.ARROW_DAMAGE
            enchantments["ARROW_KNOCKBACK"] = Enchantment.ARROW_KNOCKBACK
            enchantments["ARROW_FIRE"] = Enchantment.ARROW_FIRE
            enchantments["ARROW_INFINITE"] = Enchantment.ARROW_INFINITE
            enchantments["LUCK"] = Enchantment.LUCK
            enchantments["LURE"] = Enchantment.LURE
            enchantments["LOYALTY"] = Enchantment.LOYALTY
            enchantments["IMPALING"] = Enchantment.IMPALING
            enchantments["RIPTIDE"] = Enchantment.RIPTIDE
            enchantments["CHANNELING"] = Enchantment.CHANNELING
            enchantments["MULTISHOT"] = Enchantment.MULTISHOT
            enchantments["QUICK_CHARGE"] = Enchantment.QUICK_CHARGE
            enchantments["PIERCING"] = Enchantment.PIERCING
            enchantments["MENDING"] = Enchantment.MENDING
            enchantments["VANISHING_CURSE"] = Enchantment.VANISHING_CURSE
        }

        /**
         * @param id The ID of the enchantment.
         * @return Returns the enchantment identified with the given ID. If no enchantment is defined by
         *     the given ID, NULL is returned.
         */
        fun getEnchantment(id: String?): Enchantment? {
            if (id == null) {
                return null
            } else if (id.isEmpty()) {
                return null
            }
            val id2 = id.toUpperCase().replace(" ".toRegex(), "_").replace("-".toRegex(), "_")
            return enchantments[id2]
        }
    }
}