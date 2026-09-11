package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.stats.StatType
import kotlin.random.Random
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Applies combat stat effects to gameplay.
 *
 * Responsibilities:
 * - Health: Modify max health on join/level-up, scale current health proportionally
 * - Damage: Apply flat damage bonus
 * - Strength: Apply percentage damage bonus (similar to Hypixel SkyBlock)
 * - Defense: Reduce damage taken
 * - Crit Chance: Random chance to trigger critical hit
 * - Crit Damage: Multiply damage on crit (base 150% + bonus)
 *
 * Separate from CombatListener which handles XP gain.
 * This listener handles the EFFECTS of combat stats.
 */
class CombatEffectListener(private val plugin: PurrSkillsPlugin) : Listener {

    /**
     * Apply health stat on player join.
     * Updates max health based on HEALTH stat.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        updatePlayerHealth(player)
    }

    /**
     * Apply combat stats to damage events.
     * Handles both attacker stats (damage, strength, crit) and victim stats (defense).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        val victim = event.entity

        // Apply attacker combat stats
        if (attacker is Player) {
            val stats = plugin.skillManager.calculateStats(attacker.uniqueId)

            // Base damage from event
            var damage = event.damage

            // Apply flat damage bonus
            val damageBonus = stats.getStat(StatType.DAMAGE)
            damage += damageBonus

            // Apply strength percentage bonus (Hypixel model: each point of strength = +0.01% damage)
            val strength = stats.getStat(StatType.STRENGTH)
            if (strength > 0) {
                damage *= (1.0 + strength / 100.0)
            }

            // Check for critical hit
            val critChance = stats.getStat(StatType.CRIT_CHANCE)
            if (critChance > 0 && Random.nextDouble(0.0, 100.0) < critChance) {
                // Critical hit! Apply crit damage multiplier
                val critDamage = stats.getStat(StatType.CRIT_DAMAGE)
                // Base crit is 150% (1.5x), crit damage stat adds to this
                // Formula: damage * (1.5 + critDamage/100)
                damage *= (1.5 + critDamage / 100.0)

                // Visual feedback for crit (could be enhanced with particles/sounds)
                // For now, just apply the damage multiplier
            }

            event.damage = damage
        }

        // Apply defender defense stat
        if (victim is Player) {
            val stats = plugin.skillManager.calculateStats(victim.uniqueId)
            val defense = stats.getStat(StatType.DEFENSE)

            if (defense > 0) {
                // Defense formula (Hypixel-like): reduction = defense / (defense + 100)
                // Example: 100 defense = 50% reduction, 200 defense = 66.7% reduction
                val reduction = defense / (defense + 100.0)
                event.damage *= (1.0 - reduction)
            }
        }
    }

    /**
     * Update a player's max health based on their HEALTH stat.
     * Scales current health proportionally to maintain percentage.
     */
    fun updatePlayerHealth(player: Player) {
        val stats = plugin.skillManager.calculateStats(player.uniqueId)
        val healthStat = stats.getStat(StatType.HEALTH)

        val maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH) ?: return

        // Store old values
        val oldMax = maxHealthAttr.value
        val currentHealth = player.health
        val healthPercent = currentHealth / oldMax

        // Set new max health
        maxHealthAttr.baseValue = healthStat

        // Scale current health to maintain percentage
        // This prevents sudden death when max health increases
        player.health = (healthStat * healthPercent).coerceIn(0.0, healthStat)
    }
}
