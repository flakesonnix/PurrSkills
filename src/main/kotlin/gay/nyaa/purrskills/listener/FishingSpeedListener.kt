package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.stats.StatType
import kotlin.math.max
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

/**
 * Applies Fishing Speed stat to fishing wait time.
 *
 * Fishing Speed Mechanics:
 * - Base: 100 = normal wait time (100-600 ticks vanilla)
 * - 200 = 50% faster (50-300 ticks)
 * - 300 = 66% faster (33-200 ticks)
 * - 400+ = 75% faster (25-150 ticks, capped)
 *
 * Conversion formula:
 * - waitTimeMultiplier = 100 / fishingSpeed
 * - Speed 100 → 1.0x multiplier (normal)
 * - Speed 200 → 0.5x multiplier (twice as fast)
 * - Speed 300 → 0.33x multiplier (3x as fast)
 * - Speed 400+ → 0.25x multiplier (4x as fast, capped)
 *
 * Uses PlayerFishEvent.FISHING state to modify the wait time before a fish bites.
 * Requires Paper API for setMinWaitTime/setMaxWaitTime methods.
 * Runs at LOW priority to apply effects early in event chain.
 */
class FishingSpeedListener(private val plugin: PurrSkillsPlugin) : Listener {

    companion object {
        private const val BASE_SPEED = 100.0
        private const val MIN_SPEED_DIVISOR = 50.0 // Avoid divide by zero
        private const val MAX_SPEED_MULTIPLIER = 0.25 // Cap at 4x speed (400+)
        private const val MIN_WAIT_TIME_TICKS = 20 // 1 second minimum
        private const val VANILLA_MIN_WAIT = 100 // Vanilla: 5 seconds (100 ticks)
        private const val VANILLA_MAX_WAIT = 600 // Vanilla: 30 seconds (600 ticks)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerFish(event: PlayerFishEvent) {
        val player = event.player

        // Only modify wait time when starting to fish
        if (event.state != PlayerFishEvent.State.FISHING) {
            return
        }

        // Get player's fishing speed stat
        val stats = plugin.skillManager.calculateStats(player.uniqueId)
        val fishingSpeed = stats.getStat(StatType.FISHING_SPEED)

        // Apply wait time modification based on speed
        applyFishingSpeedEffect(event, fishingSpeed)
    }

    /**
     * Apply fishing speed by modifying hook wait time.
     *
     * @param event The fishing event
     * @param fishingSpeed The calculated fishing speed stat
     */
    private fun applyFishingSpeedEffect(event: PlayerFishEvent, fishingSpeed: Double) {
        // Calculate wait time multiplier based on speed
        val speedMultiplier = calculateSpeedMultiplier(fishingSpeed)

        // If speed is base (100), no modification needed
        if (speedMultiplier >= 1.0) {
            return
        }

        // Apply modified wait time to fishing hook
        val hook = event.hook

        try {
            // Calculate new wait times
            val minWaitTime = (VANILLA_MIN_WAIT * speedMultiplier).toInt().coerceAtLeast(MIN_WAIT_TIME_TICKS)
            val maxWaitTime = (VANILLA_MAX_WAIT * speedMultiplier).toInt().coerceAtLeast(minWaitTime)

            // Paper API: set wait times
            hook.minWaitTime = minWaitTime
            hook.maxWaitTime = maxWaitTime
        } catch (e: Exception) {
            // API not available (Spigot or old Paper version)
            plugin.logger.fine("Could not modify fishing wait time: ${e.message}")
        }
    }

    /**
     * Calculate wait time multiplier from fishing speed.
     *
     * @param fishingSpeed The fishing speed stat value
     * @return Multiplier (0.25 to 1.0+)
     */
    private fun calculateSpeedMultiplier(fishingSpeed: Double): Double {
        val safeSpeed = max(fishingSpeed, MIN_SPEED_DIVISOR)
        val multiplier = BASE_SPEED / safeSpeed
        return multiplier.coerceAtLeast(MAX_SPEED_MULTIPLIER)
    }
}
