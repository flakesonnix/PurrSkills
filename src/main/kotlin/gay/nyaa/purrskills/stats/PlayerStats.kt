package gay.nyaa.purrskills.stats

/**
 * Container for all stats of a player.
 * Stores final calculated values after applying all modifiers.
 *
 * This is a snapshot of stats at a point in time.
 * To recalculate, use StatsManager.
 *
 * Immutable - modifications return new instances.
 */
data class PlayerStats(
    val stats: Map<StatType, Double>,
) {
    /**
     * Get a specific stat value.
     * Returns the stat's base value if not present.
     */
    fun getStat(stat: StatType): Double = stats[stat] ?: stat.baseValue

    /**
     * Check if a stat has a non-base value.
     */
    fun hasStat(stat: StatType): Boolean = stats.containsKey(stat)

    /**
     * Get all stats with non-base values.
     */
    fun getAllStats(): Map<StatType, Double> = stats

    /**
     * Create a copy with a stat updated.
     */
    fun withStat(stat: StatType, value: Double): PlayerStats = copy(stats = stats + (stat to value))

    companion object {
        /**
         * Create empty PlayerStats with all base values.
         */
        fun empty(): PlayerStats = PlayerStats(emptyMap())

        /**
         * Create PlayerStats from a map of stats.
         */
        fun fromMap(stats: Map<StatType, Double>): PlayerStats = PlayerStats(stats)
    }
}
