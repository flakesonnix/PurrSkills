package gay.nyaa.purrskills.stats

/**
 * Source of a stat modifier.
 * Used to identify where bonuses come from and manage conflicts.
 *
 * Examples:
 * - SKILL_MINING: Mining skill level bonuses
 * - ITEM_HELMET: Helmet stat bonuses
 * - BUFF_POTION: Temporary potion effects
 */
enum class StatSource {
    // Skill sources (one per skill)
    SKILL_MINING,
    SKILL_FARMING,
    SKILL_FORAGING,
    SKILL_COMBAT,
    SKILL_FISHING,

    // Item sources (by slot)
    ITEM_HELMET,
    ITEM_CHESTPLATE,
    ITEM_LEGGINGS,
    ITEM_BOOTS,
    ITEM_MAIN_HAND,
    ITEM_OFF_HAND,

    // Buff sources
    BUFF_POTION,
    BUFF_EFFECT,
    BUFF_ABILITY,

    // Collection sources
    COLLECTION_MINING,
    COLLECTION_FARMING,
    COLLECTION_FORAGING,
    COLLECTION_COMBAT,
    COLLECTION_FISHING,

    // Misc sources
    BASE, // Base stats (always present)
    OTHER, // Uncategorized
}
