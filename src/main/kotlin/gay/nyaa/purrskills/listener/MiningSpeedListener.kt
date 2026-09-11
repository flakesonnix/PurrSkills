package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.mining.MiningXpSource
import gay.nyaa.purrskills.stats.StatType
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Applies Mining Speed stat to block breaking speed.
 *
 * Mining Speed Mechanics:
 * - Base: 100 = normal speed
 * - 100-199: No effect (normal speed)
 * - 200-299: Haste I
 * - 300-399: Haste II
 * - 400+: Instant break
 *
 * Conversion formula:
 * - Speed 100 → No effect (normal)
 * - Speed 200 → Haste I (+20% speed)
 * - Speed 300 → Haste II (+40% speed)
 * - Speed 400+ → Instant break
 *
 * Only applies to blocks that grant Mining XP.
 * Runs at LOW priority to apply effects early in event chain.
 */
class MiningSpeedListener : Listener {

    private var plugin: PurrSkillsPlugin? = null
    private var skillManager: gay.nyaa.purrskills.SkillManager? = null

    // Primary constructor for production use
    constructor(plugin: PurrSkillsPlugin) {
        this.plugin = plugin
    }

    // Test constructor that accepts SkillManager directly
    constructor(skillManager: gay.nyaa.purrskills.SkillManager) {
        this.skillManager = skillManager
    }

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

        // Only apply to mining blocks
        if (!MiningXpSource.grantsMiningXp(block.type)) {
            return
        }

        // Get player's mining speed stat
        val manager = skillManager ?: plugin!!.skillManager
        val stats = manager.calculateStats(player.uniqueId)
        val miningSpeed = stats.getStat(StatType.MINING_SPEED)

        // Apply speed effect based on stat value
        applyMiningSpeedEffect(event, player, miningSpeed)
    }

    /**
     * Apply mining speed as gameplay effect.
     *
     * @param event The block break event
     * @param player The player mining
     * @param miningSpeed The calculated mining speed stat
     */
    private fun applyMiningSpeedEffect(
        event: BlockBreakEvent,
        player: org.bukkit.entity.Player,
        miningSpeed: Double,
    ) {
        when {
            // Very high speed: Instant break
            miningSpeed >= INSTANT_BREAK_THRESHOLD -> {
                event.isDropItems = true
                event.isDropItems = true // Keep drops
                // Note: setInstaBreak() doesn't exist in all Bukkit versions
                // Instead, apply very high haste level for near-instant mining
                applyHasteEffect(player, 10)
            }

            // High speed: Haste II
            miningSpeed >= HASTE_II_THRESHOLD -> {
                applyHasteEffect(player, 2)
            }

            // Medium speed: Haste I
            miningSpeed >= HASTE_I_THRESHOLD -> {
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
