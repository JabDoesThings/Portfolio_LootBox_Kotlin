package jab.spigot.util

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

class LivingEntityTracker(val plugin: JavaPlugin) {

    val mapTrackedEntities: HashMap<UUID, TrackedLivingEntity> = HashMap()
    val listToRemove: ArrayList<UUID> = ArrayList()
    var taskId: Int = -1

    fun track(livingEntity: LivingEntity, runnable: Runnable?) {
        track(livingEntity, null, runnable, null)
    }

    fun track(livingEntity: LivingEntity, start: Runnable?, update: Runnable?, stop: Runnable?) {
        val entityId = livingEntity.uniqueId
        if (mapTrackedEntities.containsKey(entityId)) {
            throw RuntimeException("The LivingEntity is already tracked: $livingEntity")
        }
        val tracker: TrackedLivingEntity = BasicTrackedLivingEntity(livingEntity, start, update, stop)
        mapTrackedEntities.put(entityId, tracker)
    }

    fun start() {
        if (taskId != -1) {
            throw RuntimeException("LivingEntityTracker is already running and cannot be started.")
        }
        val scheduler = Bukkit.getScheduler()
        taskId = scheduler.scheduleSyncRepeatingTask(plugin, { update() }, 1L, 1L)
    }

    fun stop() {
        if (taskId == -1) {
            throw RuntimeException("LivingEntityTracker is not running and cannot be stopped.")
        }
        val scheduler = Bukkit.getScheduler()
        scheduler.cancelTask(taskId)
        taskId = -1
    }

    private fun update() {
        if (!mapTrackedEntities.isEmpty()) {
            for (key in mapTrackedEntities.keys) {
                val next = mapTrackedEntities[key]!!
                if (!next.started && !next.start() || !next.update()) {
                    listToRemove.add(key)
                }
            }
            if (!listToRemove.isEmpty()) {
                val iterator: MutableIterator<UUID> = listToRemove.iterator()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val next: TrackedLivingEntity? = mapTrackedEntities.remove(key)
                    if (next!!.started) {
                        next.stop()
                    }
                    iterator.remove()
                }
            }
        }
    }

    fun isTracking(entity: LivingEntity): Boolean {
        return isTracking(entity.uniqueId)
    }

    fun isTracking(entityId: UUID?): Boolean {
        return mapTrackedEntities.containsKey(entityId)
    }

    fun isRunning(): Boolean {
        return taskId != -1
    }
}