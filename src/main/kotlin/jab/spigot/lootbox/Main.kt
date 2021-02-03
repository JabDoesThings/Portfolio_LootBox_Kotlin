package jab.spigot.lootbox

import org.bukkit.plugin.java.JavaPlugin

/**
 *
 */
class Main : JavaPlugin() {

    override fun onEnable() {
        // Load config.
        saveDefaultConfig()



    }

    override fun onDisable() {
        System.out.println("Goodbye, world!");
    }
}