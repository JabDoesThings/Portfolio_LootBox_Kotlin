package jab.spigot.util

import org.bukkit.entity.LivingEntity

/**
 * @param entity The entity to track.
 */
abstract class TrackedLivingEntity(var entity: LivingEntity) {

    var started: Boolean = false
        private set

    fun start(): Boolean {
        try {
            this.started = onStart()
        } catch (e: Exception) {
            System.err.println("The tracked entity $entity failed to start.")
            e.printStackTrace(System.err)
        }
        return this.started
    }

    fun update(): Boolean {
        try {
            return onUpdate();
        } catch (e: Exception) {
            System.err.println("The tracked entity $entity failed to update.")
            e.printStackTrace(System.err)
        }
        return false
    }

    fun stop() {
        try {
            onStop()
        } catch (e: Exception) {
            System.err.println("The tracked entity $entity encountered an exception while stopping.")
            e.printStackTrace(System.err)
        }
    }

    abstract fun onStart(): Boolean
    abstract fun onUpdate(): Boolean
    abstract fun onStop()
}