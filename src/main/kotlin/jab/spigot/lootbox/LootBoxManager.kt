package jab.spigot.lootbox

import jab.spigot.util.nms.NMSUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LootBoxManager(private val plugin: Main) : Listener {

    var taskId: Int = -1
        private set

    private val mapOpenedLootBoxes: HashMap<UUID, OpenedLootBox> = HashMap()
    private val blocksToCancel: HashMap<Location, OpenedLootBox> = HashMap()
    private val listToRemove: ArrayList<UUID> = ArrayList()

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        val player = event.player
        val playerId = player.uniqueId
        if (mapOpenedLootBoxes.containsKey(playerId)) {
            val openedLootBox = mapOpenedLootBoxes[playerId]
            stop(openedLootBox)
        }
    }

    @EventHandler
    fun on(event: BlockPlaceEvent) {
        val cfgManager: CFGManager = plugin.cfgManager!!
        val player = event.player
        val inventory = player.inventory
        val block = event.block

        // Check to see if the player has right-clicked with a LootBox in
        //   their main hand.
        val mainHand = inventory.itemInMainHand
        val id = getId(mainHand) ?: return
        val lootBox = cfgManager.getLootBox(id) ?: return
        openLootBox(lootBox, player, block)
    }

    private fun getId(itemStack: ItemStack): String? {
        if (!itemStack.hasItemMeta()) return null
        val itemMeta = itemStack.itemMeta
        return if (itemMeta == null || !itemMeta.hasLocalizedName()) null else itemMeta.localizedName
    }

    @EventHandler
    fun on(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return

        // Make sure that the block is a chest before checking if it is a loot-box chest.
        val type = clickedBlock.type
        if (type != Material.CHEST && type != Material.ENDER_CHEST) {
            return
        }

        // Check and see if the location is a loot-box chest.
        val location = clickedBlock.location
        if (blocksToCancel.containsKey(location)) {
            event.isCancelled = true
            stop(blocksToCancel[location])
        }
    }

    private fun stop(openedLootBox: OpenedLootBox?) {
        openedLootBox!!.onStop()
        blocksToCancel.remove(openedLootBox.block.location)
        mapOpenedLootBoxes.remove(openedLootBox.player.uniqueId)
    }

    @EventHandler
    fun on(event: BlockBreakEvent) {
        val block = event.block

        // Make sure that the block is a chest before checking if it is a loot-box chest.
        val type = block.type
        if (type != Material.CHEST && type != Material.ENDER_CHEST) {
            return
        }

        // Check to see if the block is a chest that is a lootbox chest.
        val location = block.location

        // If so, re-open the chest.
        if (blocksToCancel.containsKey(location)) {
            event.isCancelled = true
            object : BukkitRunnable() {
                override fun run() {
                    NMSUtils.setChestLid(block, true)
                }
            }.runTaskLater(plugin, 1L)
        }
    }

    fun start() {

        // Make sure that the manager is not already started.
        if (taskId != -1) {
            throw RuntimeException("The LootBoxManager is already running and cannot be started.")
        }

        // Starts the update loop.
        val scheduler = Bukkit.getScheduler()
        taskId = scheduler.scheduleSyncRepeatingTask(plugin, { update() }, 1L, 1L)

        // Register events.
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(this, plugin)
    }

    fun stop() {

        // Make sure that the manager is running.
        if (taskId == -1) {
            throw RuntimeException("The LootBoxManager is not running and cannot be stopped.")
        }

        // Clear any currently opening loot boxes.
        if (mapOpenedLootBoxes.isNotEmpty()) {
            for (key in mapOpenedLootBoxes.keys) {
                val openedLootBox = mapOpenedLootBoxes[key]
                openedLootBox!!.onStop()
            }
            mapOpenedLootBoxes.clear()
        }

        // Clear any locations for opening loot boxes.
        if (blocksToCancel.isNotEmpty()) {
            blocksToCancel.clear()
        }

        // Stops the update loop.
        val scheduler = Bukkit.getScheduler()
        scheduler.cancelTask(taskId)
        taskId = -1

        // Unregister events.
        HandlerList.unregisterAll(this)
    }

    private fun update() {
        if (mapOpenedLootBoxes.isNotEmpty()) {
            for (key in mapOpenedLootBoxes.keys) {
                val openedLootBox = mapOpenedLootBoxes[key]!!
                if (!openedLootBox.started) {
                    openedLootBox.onStart()
                } else if (!openedLootBox.onUpdate()) {
                    listToRemove.add(key)
                }
            }
            if (listToRemove.isNotEmpty()) {
                val iterator = listToRemove.iterator()
                while (iterator.hasNext()) {


                    // Grab the next opened loot-box to remove.
                    val key = iterator.next()
                    val openedLootBox = mapOpenedLootBoxes.remove(key)

                    // Process the cleanup portion of the loot-box opening.
                    openedLootBox!!.onStop()

                    // Remove the block from being restricted to use.
                    val block = openedLootBox.block
                    val location = block.location
                    blocksToCancel.remove(location)

                    // Remove the opened loot-box inst7ance.
                    iterator.remove()
                }
            }
        }
    }

    private fun openLootBox(lootBox: LootBox, player: Player, block: Block) {
        val playerId = player.uniqueId
        var openedLootBox: OpenedLootBox?
        if (mapOpenedLootBoxes.containsKey(playerId)) {
            openedLootBox = mapOpenedLootBoxes.remove(playerId)
            openedLootBox!!.onStop()
            blocksToCancel.remove(openedLootBox.block.getLocation())
        }
        openedLootBox = OpenedLootBox(lootBox, player, block)
        mapOpenedLootBoxes[playerId] = openedLootBox
        blocksToCancel[block.location] = openedLootBox
    }

    fun isRunning(): Boolean {
        return taskId != -1
    }
}