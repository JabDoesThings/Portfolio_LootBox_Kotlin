package jab.spigot.util

import org.bukkit.entity.LivingEntity

class BasicTrackedLivingEntity(
    entity: LivingEntity,
    private val start: Runnable?,
    private val update: Runnable?,
    private val stop: Runnable?,
) :
    TrackedLivingEntity(entity) {

    override fun onStart(): Boolean {
        start?.run()
        return true
    }

    override fun onUpdate(): Boolean {
        update?.run()
        return !entity.isDead
    }

    override fun onStop() {
        stop?.run()
    }
}