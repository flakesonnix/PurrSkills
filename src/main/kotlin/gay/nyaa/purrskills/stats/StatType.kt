package gay.nyaa.purrskills.stats

/**
 * Types of stats that can be modified by skills, items, buffs, etc.
 *
 * Following Hypixel SkyBlock model:
 * - Mining stats affect block breaking
 * - Combat stats affect damage/defense
 * - Farming stats affect crop yields
 * - General stats affect all activities
 *
 * Stats are the intermediate layer between sources (skills/items/buffs)
 * and actual gameplay effects (listeners apply these stats).
 */
enum class StatType(
    val displayName: String,
    val baseValue: Double = 0.0,
) {
    // Mining stats
    MINING_SPEED("Mining Speed", baseValue = 100.0), // 100 = normal speed
    MINING_FORTUNE("Mining Fortune", baseValue = 0.0), // Chance for extra drops

    // Combat stats
    HEALTH("Health", baseValue = 100.0),
    DAMAGE("Damage", baseValue = 0.0),
    DEFENSE("Defense", baseValue = 0.0),
    STRENGTH("Strength", baseValue = 0.0),
    CRIT_CHANCE("Crit Chance", baseValue = 0.0),
    CRIT_DAMAGE("Crit Damage", baseValue = 50.0), // 50% base crit damage

    // Farming stats
    FARMING_FORTUNE("Farming Fortune", baseValue = 0.0),
    FARMING_SPEED("Farming Speed", baseValue = 100.0),

    // Foraging stats
    FORAGING_FORTUNE("Foraging Fortune", baseValue = 0.0),
    FORAGING_SPEED("Foraging Speed", baseValue = 100.0),

    // Fishing stats
    FISHING_SPEED("Fishing Speed", baseValue = 100.0),
    SEA_CREATURE_CHANCE("Sea Creature Chance", baseValue = 0.0),

    // General stats
    SPEED("Speed", baseValue = 100.0), // Movement speed
    MAGIC_FIND("Magic Find", baseValue = 0.0), // Rare drop chance
    ;

    /**
     * Get the i18n key for this stat.
     * e.g., "stats.mining-speed"
     */
    fun i18nKey(): String = "stats.${name.lowercase().replace('_', '-')}"
}
