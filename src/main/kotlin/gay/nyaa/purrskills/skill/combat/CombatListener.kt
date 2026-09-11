package gay.nyaa.purrskills.skill.combat

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.skill.Skill
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/**
 * Listens for EntityDeathEvent and awards Combat XP for killing mobs.
 *
 * Thin listener - delegates XP logic to CombatXpSource
 * and skill management to SkillManager.
 */
class CombatListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val entityType = entity.type

        // Check if this entity grants Combat XP
        if (!CombatXpSource.grantsCombatXp(entityType)) {
            return
        }

        // Get the player who killed the entity (kill credit)
        // LivingEntity.getKiller() returns the Player who dealt the killing blow
        // Returns null if:
        // - Entity died from non-player causes (fall, fire, etc.)
        // - Entity was killed by another entity (mob vs mob)
        // - Kill credit expired (default: 100 ticks / 5 seconds)
        val killer = entity.killer
        if (killer == null) {
            return // No player kill credit
        }

        // Get XP amount
        val xp = CombatXpSource.getXp(entityType)
        if (xp <= 0) return

        // Award XP to player
        plugin.skillManager.awardSkillXp(killer, Skill.COMBAT, xp)
    }
}
