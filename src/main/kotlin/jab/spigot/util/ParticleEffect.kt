package jab.spigot.util

import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.yaml.snakeyaml.error.YAMLException

/**
 * The <i>ParticleEffect</i> class handles reading and spawning {@link Particle} effects using the
 * Bukkit API.
 *
 * @author Jab
 *
 * @param cfg The ConfigurationSection to read.
 */
class ParticleEffect(cfg: ConfigurationSection) {
    var type: Particle
        private set
    var amount: Int
        private set

    init {
        if (!cfg.isString("type")) {
            throw YAMLException("The type for the particle_effect is not defined.")
        }
        val typeString =
            cfg.getString("type") ?: throw YAMLException("The type for the particle_effect is not defined.")
        val type = getParticle(typeString) ?: throw YAMLException("Unknown particle_effect type: $typeString")
        this.type = type
        if (!cfg.isInt("amount")) {
            throw YAMLException("The amount for the particle_effect is not defined.")
        }
        this.amount = cfg.getInt("amount")
        if (amount < 0) {
            throw YAMLException(
                "The amount value for the particle_effect is out of range. Amount must be 0 or greater. ($amount given)"
            )
        }
    }

    /**
     * Spawns the particle-effect at a player's location.
     *
     * <p><b>NOTE</b>: If the player is not online, no particle-effect is spawned.
     *
     * @param player The player to spawn the particles at.
     */
    fun spawn(player: Player) {
        if (amount == 0) {
            return
        }

        if (!player.isOnline) {
            return
        }

        player.world.spawnParticle(type, player.location.add(0.0, 1.0, 0.0), amount)
    }

    companion object {
        fun getParticle(type: String): Particle? {
            val type2 = type.toUpperCase().replace(" ".toRegex(), "_")
            for (next in Particle.values()) {
                if (next.name == type2) return next
            }
            return null
        }
    }
}