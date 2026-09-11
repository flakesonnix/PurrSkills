package gay.nyaa.purrskills.skill.fishing

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.Skill
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

/**
 * Listens for PlayerFishEvent and awards Fishing XP for successful catches.
 *
 * Thin listener - delegates XP logic to FishingXpSource
 * and skill management to SkillManager.
 */
class FishingListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerFish(event: PlayerFishEvent) {
        val player = event.player

        // Only award XP for successful catches
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        // Get the caught entity
        // In CAUGHT_FISH state, this is an Item entity containing the caught ItemStack
        val caught = event.caught
        if (caught !is Item) {
            return // Safety check - should always be Item in CAUGHT_FISH state
        }

        // Get the material of the caught item
        val itemStack = caught.itemStack
        val material = itemStack.type

        // Check if this material grants Fishing XP
        if (!FishingXpSource.grantsFishingXp(material)) {
            return
        }

        // Get XP amount
        val xp = FishingXpSource.getXp(material)
        if (xp <= 0) return

        // Award XP to player
        plugin.skillManager.awardSkillXp(player, Skill.FISHING, xp)
    }
}
