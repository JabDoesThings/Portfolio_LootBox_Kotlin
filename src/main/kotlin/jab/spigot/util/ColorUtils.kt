package jab.spigot.util

import org.bukkit.ChatColor
import java.util.*

class ColorUtils {
    companion object {

        /**
         * Encodes a list of strings to MineCraft's color encoding using [ ][ChatColor.translateAlternateColorCodes].
         *
         * @param strings The list of strings to encode.
         * @return Returns the color-encoded strings as a new List.
         */
        fun color(strings: List<String?>): List<String?> {
            if (strings.isEmpty()) return ArrayList()
            val coloredMessages: ArrayList<String?> = ArrayList()
            for (message in strings) {
                if (message == null) {
                    coloredMessages.add(null)
                } else {
                    coloredMessages.add(color(message))
                }
            }
            return coloredMessages
        }

        /**
         * Encodes a string to MineCraft's color encoding using [ ][ChatColor.translateAlternateColorCodes].
         *
         * @param string The string to encode.
         * @return Returns the color-encoded strings as a new List.
         */
        fun color(string: String): String {
            return if (string.isEmpty()) "" else ChatColor.translateAlternateColorCodes(
                '&',
                string)
        }
    }
}