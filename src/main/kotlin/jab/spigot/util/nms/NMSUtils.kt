package jab.spigot.util.nms

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

/**
 * The <i>NMSUtils</i> class handles interfacing NMS utilities for multiple Bukkit versions.
 *
 * @author Jab
 */
class NMSUtils {
    companion object {

        /**
         * Sets a {@link Material#CHEST CHEST} or {@link Material#ENDER_CHEST ENDER_CHEST}'s lid state as
         * either 'open' or 'closed'.
         *
         * @param block The block to modify.
         * @param flag Set to true to set the chest lid as 'open'. Set to false to set the chest lid as
         *     'closed'.
         * @throws RuntimeException Thrown if the block given is not a {@link Material#CHEST CHEST} or a
         *     {@link Material#ENDER_CHEST ENDER_CHEST}. Thrown if the current Bukkit version is not
         *     supported.
         */
        fun setChestLid(block: Block, flag: Boolean) {
            val version = Bukkit.getVersion()
            if (version.contains("1.16")) {
                NMSUtils_1_16_R3.setChestLid(block, flag)
                return
            }
            throw RuntimeException("Version not supported: $version")
        }

        fun convertItemStackToJson(itemStack: ItemStack): String {
            val version = Bukkit.getVersion()
            if (version.contains("1.16")) {
                return NMSUtils_1_16_R3.convertItemStackToJson(itemStack)
            }
            throw IllegalStateException("Unsupported version: $version")
        }
    }
}