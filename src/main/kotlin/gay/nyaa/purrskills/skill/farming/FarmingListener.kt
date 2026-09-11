package gay.nyaa.purrskills.skill.farming

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Listens for BlockBreakEvent and awards Farming XP for harvesting crops.
 *
 * Thin listener - delegates XP logic to FarmingXpSource
 * and skill management to plugin.awardSkillXp().
 */
class FarmingListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        // Don't award XP in creative or spectator
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val block = event.block
        val material = block.type

        // Check if this block grants Farming XP
        if (!FarmingXpSource.grantsFarmingXp(material)) {
            return
        }

        // Check if crop is mature (for age-based crops)
        if (!FarmingXpSource.isMature(block.blockData)) {
            return
        }

        // Get XP amount
        val xp = FarmingXpSource.getXp(material)
        if (xp <= 0) return

        // Award XP to player
        plugin.skillManager.awardSkillXp(player, Skill.FARMING, xp)
    }
}
