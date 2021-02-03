package jab.spigot.util.nms

import net.minecraft.server.v1_16_R3.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The <i>NMSUtils_1_16_R3</i> class handles NMS implemented methods for {@link NMSUtils} for the
 * Bukkit version '1.16'. (Revision 3)
 *
 * @author Jab
 */
class NMSUtils_1_16_R3 {
    companion object {

        /**
         * Sets a {@link Material#CHEST CHEST} or {@link Material#ENDER_CHEST ENDER_CHEST}'s lid state as
         * either 'open' or 'closed'.
         *
         * @param block The block to modify.
         * @param flag Set to true to set the chest lid as 'open'. Set to false to set the chest lid as
         *     'closed'.
         * @throws RuntimeException Thrown if the block given is not a {@link Material#CHEST CHEST} or a
         *     {@link Material#ENDER_CHEST ENDER_CHEST}.
         */
        fun setChestLid(block: Block, flag: Boolean) {
            val type: Material = block.type
            if (type != Material.CHEST && type != Material.ENDER_CHEST) {
                throw RuntimeException("The block provided is not a chest.")
            }
            val world: World = block.world
            val nmsWorld: WorldServer? = (world as CraftWorld).handle
            val location: Location = block.location
            val position = BlockPosition(location.x, location.y, location.z)
            if (type == Material.CHEST) {
                nmsWorld?.playBlockAction(position, Blocks.CHEST, 1, if (flag) 1 else 0)
            } else {
                nmsWorld?.playBlockAction(position, Blocks.ENDER_CHEST, 1, if (flag) 1 else 0)
            }
        }

        /**
         * Spawns a particle, sending it to the players given.
         *
         * @param players The players to send the particle to.
         * @param particle The particle type.
         * @param location The location for the particle.
         * @param offsetX The offset x-coordinate.
         * @param offsetY The offset y-coordinate.
         * @param offsetZ The offset z-coordinate.
         * @param speed The speed of the particle.
         * @param amount The amount of the particle to spawn.
         */
        fun spawnParticle(
            players: List<Player?>,
            particle: Particle,
            location: Location,
            offsetX: Float,
            offsetY: Float,
            offsetZ: Float,
            speed: Float,
            amount: Int
        ) {
            val particleNMS: ParticleParam = CraftParticle.toNMS(particle)
            val packet = PacketPlayOutWorldParticles(
                particleNMS,
                true,
                location.x,
                location.y,
                location.z,
                offsetX,
                offsetY,
                offsetZ,
                speed,
                amount
            )
            sendPacket(players, packet)
        }

        /**
         * Sends a packet to a collection of players.
         *
         * @param players The players to send the packet.
         * @param packet The packet to send.
         */
        private fun sendPacket(players: List<Player?>, packet: Packet<PacketListenerPlayOut>) {
            for (player in players) {
                (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
        }

        fun convertItemStackToJson(itemStack: ItemStack): String {
            val nmsItemStack: net.minecraft.server.v1_16_R3.ItemStack = CraftItemStack.asNMSCopy(itemStack)
            val compound = NBTTagCompound()
            nmsItemStack.save(compound)
            return compound.toString()
        }
    }
}