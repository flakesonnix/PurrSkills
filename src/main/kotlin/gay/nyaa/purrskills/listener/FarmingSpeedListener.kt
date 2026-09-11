package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.farming.FarmingXpSource
import gay.nyaa.purrskills.stats.StatType
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Applies Farming Speed stat to crop breaking speed.
 *
 * Farming Speed Mechanics:
 * - Base: 100 = normal speed
 * - 100-199: No effect (normal speed)
 * - 200-299: Haste I
 * - 300-399: Haste II
 * - 400+: Instant break (no effect needed, crops break instantly)
 *
 * Conversion formula:
 * - Speed 100 → No effect (normal)
 * - Speed 200 → Haste I (+20% speed)
 * - Speed 300 → Haste II (+40% speed)
 * - Speed 400+ → Instant break
 *
 * Only applies to blocks that grant Farming XP.
 * Runs at LOW priority to apply effects early in event chain.
 */
class FarmingSpeedListener(private val plugin: PurrSkillsPlugin) : Listener {

    companion object {
        private const val BASE_SPEED = 100.0
        private const val HASTE_I_THRESHOLD = 200.0
        private const val HASTE_II_THRESHOLD = 300.0
        private const val INSTANT_BREAK_THRESHOLD = 400.0
        private const val EFFECT_DURATION = 40 // 2 seconds (40 ticks)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        // Don't apply speed in creative/spectator
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        // Only apply to farming blocks
        if (!FarmingXpSource.grantsFarmingXp(block.type)) {
            return
        }

        // Get player's farming speed stat
        val stats = plugin.skillManager.calculateStats(player.uniqueId)
        val farmingSpeed = stats.getStat(StatType.FARMING_SPEED)

        // Apply speed effect based on stat value
        applyFarmingSpeedEffect(player, farmingSpeed)
    }

    /**
     * Apply farming speed as potion effect.
     *
     * @param player The player farming
     * @param farmingSpeed The calculated farming speed stat
     */
    private fun applyFarmingSpeedEffect(player: org.bukkit.entity.Player, farmingSpeed: Double) {
        when {
            // Very high speed: Instant break (crops are easy to break)
            farmingSpeed >= INSTANT_BREAK_THRESHOLD -> {
                // Instant break - block breaks instantly at this speed
                // No additional action needed
            }

            // High speed: Haste II
            farmingSpeed >= HASTE_II_THRESHOLD -> {
                applyHasteEffect(player, 2)
            }

            // Medium speed: Haste I
            farmingSpeed >= HASTE_I_THRESHOLD -> {
                applyHasteEffect(player, 1)
            }

            // Below 200: no effect (normal speed at base 100)
        }
    }

    /**
     * Apply haste effect to player.
     *
     * @param player The player
     * @param level Haste level (1 = Haste I, 2 = Haste II)
     */
    private fun applyHasteEffect(player: org.bukkit.entity.Player, level: Int) {
        if (level <= 0) return

        val amplifier = level - 1 // Level 1 = amplifier 0

        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.HASTE,
                EFFECT_DURATION,
                amplifier,
                false, // ambient
                false, // particles
                false, // icon
            ),
        )
    }
}
