package gay.nyaa.purrskills.skill.mining

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Listens for BlockBreakEvent and awards Mining XP.
 *
 * Thin listener - delegates XP logic to MiningXpSource
 * and skill management to PlayerSkills.
 */
class MiningListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        // Don't award XP in creative or spectator
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        // Check if this block grants Mining XP
        val xp = MiningXpSource.getXp(event.block.type)
        if (xp <= 0) return

        // Award XP to player
        plugin.awardSkillXp(player, Skill.MINING, xp)
    }
}
