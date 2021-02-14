package jab.spigot.lootbox

import jab.spigot.lootbox.Main.Companion.hasRoom
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.ArrayList

/**
 * The <i>LootBoxCommand</i> class handle the execution and tab-completion for the 'lootbox' command
 * for the LootBox plug-in.
 *
 * @author Jab
 *
 * @param plugin The plugin instance.
 */
class LootBoxCommand(private val plugin: Main): CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        c: Command,
        label: String,
        args: Array<String>,
    ): Boolean {
        val command = c.name.toLowerCase()
        if (command != "lootbox") return true
        val mgr: CFGManager = plugin.cfgManager!!

        // Make sure the required arguments are present.
        if (args.size < 3 || args.size > 4) {
            sender.sendMessage(mgr.getCommandGiveHelp()!!)
            return true
        }

        // Attempt to retrieve the player with the argument given.
        val player = Bukkit.getPlayer(args[0])
        if (player == null) {
            sender.sendMessage(mgr.getUnknownPlayer(args[0]))
            return true
        }
        val playerInventory = player.inventory

        // Check if the commander wants a lootbox or item to give.
        val isLootbox = args[1].equals("lootbox", ignoreCase = true)
        if (!isLootbox && !args[1].equals("item", ignoreCase = true)) {
            sender.sendMessage(mgr.getCommandGiveHelp()!!)
            return true
        }
        val id = args[2]
        var amount = 1

        // If the amount arguments is specified, attempt to parse it.
        if (args.size == 4) {
            amount = try {
                args[3].toInt()
            } catch (e: NumberFormatException) {
                sender.sendMessage(mgr.getCommandGiveInvalidAmount(args[3]))
                return true
            }
            if (amount < 1) {
                sender.sendMessage(mgr.getCommandGiveInvalidAmount(args[3]))
                return true
            }
        }
        if (isLootbox) {
            val lootBox = mgr.getLootBox(id)
            if (lootBox == null) {
                sender.sendMessage(mgr.getUnknownLootbox(id))
                return true
            }
            val itemName = lootBox.getName()

            // Check to make sure the player can receive the item.
            val minSlots: Int = lootBox.item.getSlotCount(amount)
            if (!hasRoom(playerInventory, minSlots)) {
                sender.sendMessage(mgr.getCommandGiveInventoryFullCommander(player, itemName!!, amount))
                player.sendMessage(mgr.getCommandGiveInventoryFullRecipient(itemName, amount, minSlots))
                return true
            }
            lootBox.give(player, amount)
            player.sendMessage(mgr.getCommandGiveRecipiant(itemName!!, amount))
            sender.sendMessage(mgr.getCommandGiveCommander(player, itemName, amount))
        } else {
            val item = mgr.getItem(id)
            if (item == null) {
                sender.sendMessage(mgr.getUnknownItem(id))
                return true
            }
            val itemName: String = item.displayName!!

            // Check to make sure the player can receive the item.
            val minSlots = item.getSlotCount(amount)
            if (!hasRoom(playerInventory, minSlots)) {
                sender.sendMessage(mgr.getCommandGiveInventoryFullCommander(player, itemName, amount))
                player.sendMessage(mgr.getCommandGiveInventoryFullRecipient(itemName, amount, minSlots))
                return true
            }
            item.give(player, amount)
            sender.sendMessage(mgr.getCommandGiveCommander(player, itemName, amount))
            player.sendMessage(mgr.getCommandGiveRecipiant(itemName, amount))
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>,
    ): List<String?>? {
        if (command.name.toLowerCase() != "lootbox") return null
        val mgr: CFGManager = plugin.cfgManager!!
        val list: MutableList<String?> = ArrayList()

        // Player argument.
        if (args.size == 1 && !args[0].isEmpty()) {
            for (player in Bukkit.getOnlinePlayers()) {
                val playerName = player.name
                if (playerName.toLowerCase().contains(args[0].toLowerCase())) {
                    list.add(playerName)
                }
            }
            return list
        } else if (args.size == 2) {
            if (!args[1].isEmpty()) {
                if ("lootbox".contains(args[1].toLowerCase())) {
                    list.add("lootbox")
                } else if ("item".contains(args[1].toLowerCase())) {
                    list.add("item")
                }
            } else {
                list.add("lootbox")
                list.add("item")
            }
            return list
        } else if (args.size == 3) {
            if (args[1].equals("lootbox", ignoreCase = true)) {
                if (args[2].isEmpty()) {
                    for (next in mgr.getLootBoxes()) {
                        list.add(next!!.id)
                    }
                } else {
                    for (next in mgr.getLootBoxes()) {
                        val id = next!!.id
                        if (id.toLowerCase().contains(args[2].toLowerCase())) {
                            list.add(id)
                        }
                    }
                }
            } else if (args[1].equals("item", ignoreCase = true)) {
                if (args[2].isEmpty()) {
                    for (next in mgr.getItems()) {
                        list.add(next!!.id)
                    }
                } else {
                    for (next in mgr.getItems()) {
                        val id = next!!.id
                        if (id!!.toLowerCase().contains(args[2].toLowerCase())) {
                            list.add(id)
                        }
                    }
                }
            }
            return list
        } else if (args.size == 4 && args[3].isEmpty()) {
            list.add("amount")
            return list
        }

        // Keep the arguments blank for erroneous commands.
        return ArrayList()
    }
}