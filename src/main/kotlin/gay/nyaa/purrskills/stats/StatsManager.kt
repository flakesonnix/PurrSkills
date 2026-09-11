package gay.nyaa.purrskills.stats

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages player stats and modifier calculations.
 *
 * Responsibilities:
 * - Store stat modifiers per player
 * - Calculate final stats from all modifiers
 * - Provide API for adding/removing modifiers
 *
 * Calculation order:
 * 1. Start with base value
 * 2. Apply all ADDITIVE modifiers (sum)
 * 3. Apply all MULTIPLICATIVE modifiers (multiply)
 *
 * Example:
 * ```
 * Base: 100
 * Additive: +20, +30 = 150
 * Multiplicative: +10%, +20% = 150 × 1.3 = 195
 * ```
 *
 * Thread-safe: Uses ConcurrentHashMap for modifier storage.
 */
class StatsManager {
    // Player UUID -> List of stat modifiers
    private val playerModifiers = ConcurrentHashMap<UUID, MutableList<StatModifier>>()

    /**
     * Add a stat modifier for a player.
     * Does not replace existing modifiers - they stack.
     *
     * @param uuid Player UUID
     * @param modifier The stat modifier to add
     */
    fun addModifier(uuid: UUID, modifier: StatModifier) {
        playerModifiers.computeIfAbsent(uuid) { mutableListOf() }.add(modifier)
    }

    /**
     * Add multiple stat modifiers for a player.
     */
    fun addModifiers(uuid: UUID, modifiers: List<StatModifier>) {
        playerModifiers.computeIfAbsent(uuid) { mutableListOf() }.addAll(modifiers)
    }

    /**
     * Remove all modifiers from a specific source for a player.
     * Useful when equipment changes or buffs expire.
     *
     * @param uuid Player UUID
     * @param source The source to remove (e.g., ITEM_HELMET)
     */
    fun removeModifiersFromSource(uuid: UUID, source: StatSource) {
        playerModifiers[uuid]?.removeIf { it.source == source }
    }

    /**
     * Remove all modifiers for a player.
     * Called when player logs out.
     */
    fun clearModifiers(uuid: UUID) {
        playerModifiers.remove(uuid)
    }

    /**
     * Get all modifiers for a player.
     * Returns immutable copy to prevent external modification.
     */
    fun getModifiers(uuid: UUID): List<StatModifier> = playerModifiers[uuid]?.toList() ?: emptyList()

    /**
     * Calculate final stats for a player.
     * Applies all modifiers in correct order.
     *
     * @param uuid Player UUID
     * @return PlayerStats with final calculated values
     */
    fun calculateStats(uuid: UUID): PlayerStats {
        val modifiers = getModifiers(uuid)
        if (modifiers.isEmpty()) {
            return PlayerStats.empty()
        }

        // Group modifiers by stat type
        val modifiersByStat = modifiers.groupBy { it.stat }

        // Calculate each stat
        val finalStats = mutableMapOf<StatType, Double>()

        for ((stat, statModifiers) in modifiersByStat) {
            val finalValue = calculateStat(stat, statModifiers)
            if (finalValue != stat.baseValue) {
                finalStats[stat] = finalValue
            }
        }

        return PlayerStats.fromMap(finalStats)
    }

    /**
     * Calculate a single stat from its modifiers.
     *
     * Order:
     * 1. Base value
     * 2. Sum all ADDITIVE modifiers
     * 3. Apply all MULTIPLICATIVE modifiers
     */
    private fun calculateStat(stat: StatType, modifiers: List<StatModifier>): Double {
        var value = stat.baseValue

        // Apply additive modifiers
        val additiveSum = modifiers
            .filter { it.type == StatModifier.ModifierType.ADDITIVE }
            .sumOf { it.value }
        value += additiveSum

        // Apply multiplicative modifiers
        val multiplicativeSum = modifiers
            .filter { it.type == StatModifier.ModifierType.MULTIPLICATIVE }
            .sumOf { it.value }
        value *= (1.0 + multiplicativeSum)

        return value
    }

    /**
     * Get the number of players with modifiers cached.
     */
    fun getCachedPlayerCount(): Int = playerModifiers.size
}
