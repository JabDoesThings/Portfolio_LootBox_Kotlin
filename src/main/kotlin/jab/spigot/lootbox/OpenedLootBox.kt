package jab.spigot.lootbox

import jab.spigot.util.ColorUtils.Companion.color
import jab.spigot.util.MathUtils
import jab.spigot.util.nms.NMSUtils
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.ceil

class OpenedLootBox(private val lootBox: LootBox, val player: Player, block: Block) {

    private val world: World = player.world
    private var itemYStart = 0.0
    private var itemYStop = 0.0

    val block: Block = block
    var started = false
        private set

    private var rollSlots: Array<Array<ItemStack?>>? = null
    private var names: Array<String?>? = null
    private var armorStandItem: ArmorStand? = null
    private var armorStandLabel: ArmorStand? = null
    private var itemRotation: EulerAngle? = null
    private var itemStartLocation: Location = Location(world, 0.0, 0.0, 0.0)
    private var rollCount = 0
    private var rollTickLoop = 0
    private var rollTick = 0
    private var rollGiven = 0
    private var roll = 0
    private var tick = 0
    private var openingLid = false
    private var closingLid = false

    init {
        val location = block.location
        itemYStart = location.y - 2
        itemYStop = location.y - 1
        rollCount = lootBox.rolls
    }

    fun onStart() {
        started = true
        NMSUtils.setChestLid(block, true)
        rollSlots = Array(lootBox.rolls) {
            arrayOfNulls(ROLL_SLOT_COUNT)
        }
        names = arrayOfNulls(rollCount)
        for (roll in 0 until lootBox.rolls) {
            val cfgRolls: Array<CFGItem?> = lootBox.roll(ROLL_SLOT_COUNT)
            names!![roll] = cfgRolls[ROLL_SLOT_COUNT - 1]!!.displayName
            for (index in 0 until ROLL_SLOT_COUNT) {
                rollSlots!![roll][index] = cfgRolls[index]!!.itemStack
            }
        }
        roll = -1
        createStands()
        setOpen()
    }

    fun onStop() {
        while (rollGiven < rollCount) {
            giveNextItem()
        }
        poof(24, 0.08)
        block.type = Material.AIR
        if (armorStandItem != null) {
            armorStandItem!!.remove()
            armorStandItem = null
        }
        if (armorStandLabel != null) {
            armorStandLabel!!.remove()
            armorStandLabel = null
        }
        openingLid = false
        closingLid = false

        // Create the result broadcast message using Item lore hover text.
        val playerName = player.name
        val lootBox: String = lootBox.getName()!!
        val line = TextComponent(LINE.trimIndent())
        val headerString = color("&f$playerName&r Received these items from &c$lootBox&r:\n")
        val header = TextComponent(headerString)
        val items = arrayOfNulls<TextComponent>(rollCount)
        for (index in 0 until rollCount) {
            val itemStack = rollSlots!![index][ROLL_SLOT_COUNT - 1]!!
            val itemName = itemStack.itemMeta!!.displayName
            items[index] = Main.createItemHoverText("$ITEM_BULLET$itemName\n".trimIndent(), itemStack)
        }
        val line2 = TextComponent(LINE.trimIndent())

        // Broadcast the result.
        val spigot = Bukkit.spigot()
        spigot.broadcast(line, header)
        spigot.broadcast(*items)
        spigot.broadcast(line2)

        // Play the sound if defined for the loot-box.
        this.lootBox.soundEffect?.broadcast()
    }

    fun onUpdate(): Boolean {
        if (openingLid) {
            if (tick == OPEN_LID_TICKS) {
                setRoll(++roll)
                openingLid = false
            }
        } else if (closingLid) {
            if (tick == CLOSE_LID_TICKS) {
                closingLid = false
                return false
            }
        } else {
            // Move onto the next roll.
            if (tick == 165) {
                if (roll == rollCount - 1) {
                    setClose()
                    return true
                } else {
                    setRoll(++roll)
                }
            } else {

                if (tick < 20) {
                    val lerp = MathUtils.easeOut(tick.toDouble() / 20.0)
                    val location = armorStandItem!!.location
                    val locY = MathUtils.lerp(itemYStart, itemYStop, lerp)
                    location.y = locY
                    armorStandItem!!.teleport(location)
                }
                if (tick in 81..120) {
                    val lerp = MathUtils.easeOut((tick.toDouble() - 80) / 40.0)
                    if (lerp <= 1) {
                        val name = names!![roll]!!
                        val lengthNow = ceil(lerp * name.length.toDouble()).toInt()
                        val toShow = StringBuilder()
                        var index = 0
                        while (index < lengthNow) {
                            val c = name[index++]
                            if (c == ChatColor.COLOR_CHAR) {
                                toShow.append(c).append(name[index++])
                            } else {
                                toShow.append(c)
                            }
                        }
                        toShow.append(ChatColor.MAGIC).append(ChatColor.stripColor(name.substring(lengthNow)))
                        armorStandLabel!!.customName = toShow.toString()
                    }
                }
                when (tick) {
                    140 -> {
                        itemStartLocation = armorStandItem!!.location
                    }
                    in 141..160 -> {
                        val lerp = MathUtils.easeIn((tick.toDouble() - 140) / 20.0)
                        val playerLocation = player.location
                        val newX = MathUtils.lerp(itemStartLocation.x, playerLocation.x, lerp)
                        val newY = MathUtils.lerp(itemStartLocation.y, playerLocation.y - 1, lerp)
                        val newZ = MathUtils.lerp(itemStartLocation.z, playerLocation.z, lerp)
                        val newLocation = Location(world, newX, newY, newZ)
                        armorStandItem!!.teleport(newLocation)
                        armorStandLabel!!.teleport(newLocation.add(0.0, 0.5, 0.0))
                    }
                    161 -> {
                        removeStands()
                        giveNextItem()
                    }
                }
                if (rollTick < ROLL_SLOT_COUNT - 2 && tick < 100) {
                    if (rollTickLoop == 5) {
                        val itemStack = rollSlots!![roll][rollTick++]
                        val pitch = MathUtils.lerp(1.0, 0.8, rollTick.toDouble() / (ROLL_SLOT_COUNT - 2).toDouble())
                        world.playSound(
                            armorStandItem!!.location, Sound.UI_BUTTON_CLICK, 0.5f, pitch.toFloat())
                        armorStandItem!!.setHelmet(itemStack)

                        rollTickLoop = 0
                    } else {
                        rollTickLoop++
                    }
                }
                if (tick == 100) {
                    val itemStack = rollSlots!![roll][ROLL_SLOT_COUNT - 1]
                    armorStandItem!!.setHelmet(itemStack)
                    world.playSound(armorStandItem!!.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f)
                }
            }
        }
        tick++
        return true
    }

    private fun resetItemPosition() {
        val directional = block.blockData as Directional
        val blockFace = directional.facing
        var rotY = 0.0
        var locX = 0.5
        val locY = 0.0
        var locZ = 0.5
        when (blockFace) {
            BlockFace.NORTH -> {
                locZ += 0.25
            }
            BlockFace.WEST -> {
                rotY = -Math.PI / 2
                locX += 0.25
            }
            BlockFace.SOUTH -> {
                rotY = Math.PI
                locZ -= 0.25
            }
            BlockFace.EAST -> {
                rotY = -Math.PI * 1.5
                locX -= 0.25
            }
        }
        itemStartLocation = block.location.add(locX, locY, locZ)
        itemStartLocation.y = itemYStart
        itemRotation = EulerAngle(0.0, rotY, 0.0)
    }

    private fun createStands() {
        removeStands()
        resetItemPosition()
        this.armorStandItem = world.spawnEntity(itemStartLocation, EntityType.ARMOR_STAND) as ArmorStand
        val armorStandItem = this.armorStandItem!!
        armorStandItem.headPose = itemRotation!!
        armorStandItem.isInvulnerable = true
        armorStandItem.isSilent = true
        armorStandItem.isVisible = false
        armorStandItem.setGravity(false)
        armorStandItem.setHelmet(ItemStack(Material.DIAMOND))
        val labelLocation = block.location.add(0.5, -0.5, 0.5)
        this.armorStandLabel = world.spawnEntity(labelLocation, EntityType.ARMOR_STAND) as ArmorStand
        val armorStandLabel = this.armorStandLabel!!
        armorStandLabel.isCustomNameVisible = true
        armorStandLabel.isInvulnerable = true
        armorStandLabel.isSilent = true
        armorStandLabel.isVisible = false
        armorStandLabel.setGravity(false)
    }

    private fun removeStands() {
        if (armorStandItem != null) {
            armorStandItem!!.remove()
            armorStandItem = null
        }
        if (armorStandLabel != null) {
            armorStandLabel!!.remove()
            armorStandLabel = null
        }
    }

    private fun giveNextItem() {
        val inventory = player.inventory
        val itemStack = rollSlots!![rollGiven++][ROLL_SLOT_COUNT - 1]!!.clone()
        if (Main.hasRoom(inventory, 1)) {
            inventory.addItem(itemStack)
        } else {
            val item = world.dropItem(player.location, itemStack)
            item.velocity = Vector(0, 0, 0)
        }
    }

    private fun setRoll(roll: Int) {
        tick = 0
        rollTick = 0
        this.roll = roll
        createStands()
        armorStandLabel!!.customName = ChatColor.MAGIC.toString() + ChatColor.stripColor(names!![roll])
    }

    private fun setOpen() {
        openingLid = true
        closingLid = false
        tick = 0
        NMSUtils.setChestLid(block, true)
    }

    private fun setClose() {
        openingLid = false
        closingLid = true
        tick = 0
        NMSUtils.setChestLid(block, false)
    }

    @Suppress("SameParameterValue")
    private fun poof(count: Int, speed: Double) {
        val location = block.location.add(0.5, 0.5, 0.5)
        val particle: Particle? = lootBox.particle
        if (particle != null) {
            world.spawnParticle(particle, location, count, 0.5, 0.5, 0.5, speed)
        }
    }

    companion object {
        private val LINE: String = color("&m&7--------------------------------------&r")
        private val ITEM_BULLET: String = color("  \u25cf ")

        private const val OPEN_LID_TICKS = 5
        private const val CLOSE_LID_TICKS = 20
        private const val ROLL_SLOT_COUNT = 20
    }
}