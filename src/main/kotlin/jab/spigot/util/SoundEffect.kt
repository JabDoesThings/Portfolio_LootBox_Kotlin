package jab.spigot.util

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.yaml.snakeyaml.error.YAMLException

/**
 * The <i>SoundEffect</i> class handles reading and playing {@link Sound} effects using the Bukkit
 * API.
 *
 * @author Jab
 *
 * @param cfg The ConfigurationSection to read.
 */
class SoundEffect(cfg: ConfigurationSection) {
    private var type: Sound
    private var volume: Float
    private var pitch: Float

    init {
        if (!cfg.isString("type")) {
            throw YAMLException("The type for the sound_effect is not defined.")
        }
        val typeString = cfg.getString("type") ?: throw YAMLException("The type for the sound_effect is not defined.")
        val type = getSound(typeString) ?: throw YAMLException("Unknown sound_effect type: $typeString")
        this.type = type
        if (cfg.contains("volume")) {
            if (cfg.isInt("volume")) {
                volume = cfg.getInt("volume").toFloat()
            } else if (cfg.isDouble("volume")) {
                volume = cfg.getDouble("volume").toFloat()
            } else {
                throw YAMLException(
                    "The volume for the sound_effect is not a valid number. (${cfg["volume"]} given)"
                )
            }
            if (volume < 0) {
                throw YAMLException(
                    "The volume for the sound_effect is out of range. Volume must be 0 or greater. ($volume given)"
                )
            }
        } else {
            volume = 1f
        }
        if (cfg.contains("pitch")) {
            if (cfg.isInt("pitch")) {
                pitch = cfg.getInt("pitch").toFloat()
            } else if (cfg.isDouble("pitch")) {
                pitch = cfg.getDouble("pitch").toFloat()
            } else {
                throw YAMLException(
                    "The pitch for the sound_effect is not a valid number. (${cfg["pitch"]} given)"
                )
            }
            if (pitch < 0) {
                throw YAMLException(
                    "The pitch for the sound_effect is out of range. pitch must be 0 or greater. ($pitch given)"
                )
            }
        } else {
            pitch = 1f
        }
    }

    /**
     * Plays the sound-effect at a player's location.
     *
     * <p><b>NOTE</b>: If the player is not online, no sound is played.
     *
     * @param player The player to play the sound at.
     */
    fun play(player: Player) {
        if (!player.isOnline) {
            return
        }
        if (volume == 0f || pitch == 0f) {
            return
        }
        player.world.playSound(player.location, type, volume, pitch)
    }

    /**
     * Broadcasts a sound to all online players.
     */
    fun broadcast() {
        for (player in Bukkit.getOnlinePlayers()) {
            play(player!!)
        }
    }

    companion object {
        fun getSound(type: String): Sound? {
            val type2 = type.toUpperCase().replace(" ".toRegex(), "_")
            for (next in Sound.values()) {
                if (next.name == type2) {
                    return next
                }
            }
            return null
        }
    }
}