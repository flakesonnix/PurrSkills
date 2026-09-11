package gay.nyaa.purrskills.skill.foraging

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Listens for BlockBreakEvent and awards Foraging XP for chopping logs/stems.
 *
 * Thin listener - delegates XP logic to ForagingXpSource
 * and skill management to plugin.awardSkillXp().
 */
class ForagingListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        // Don't award XP in creative or spectator
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val block = event.block
        val material = block.type

        // Check if this block grants Foraging XP
        if (!ForagingXpSource.grantsForagingXp(material)) {
            return
        }

        // Get XP amount
        val xp = ForagingXpSource.getXp(material)
        if (xp <= 0) return

        // Award XP to player
        plugin.awardSkillXp(player, Skill.FORAGING, xp)
    }
}
