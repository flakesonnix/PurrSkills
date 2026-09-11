package gay.nyaa.purrskills.skill.combat

import org.bukkit.entity.EntityType

/**
 * Combat XP source - defines XP values for killing mobs.
 * Pure data mapping - no Bukkit event handling here.
 *
 * Based on Hypixel SkyBlock Combat XP model.
 */
object CombatXpSource {

    /**
     * XP values for killing specific entity types.
     * Only entities that grant Combat XP are listed here.
     *
     * Players deliberately return 0 XP (no PvP XP for now).
     */
    private val XP_VALUES = mapOf(
        EntityType.ZOMBIE to 10,
        EntityType.SKELETON to 12,
        EntityType.SPIDER to 10,
        EntityType.CREEPER to 15,
        EntityType.ENDERMAN to 30,
        EntityType.WITCH to 35,
        EntityType.SLIME to 5,
        EntityType.PHANTOM to 25,
        EntityType.BLAZE to 30,
        EntityType.WITHER_SKELETON to 40,
        EntityType.PIGLIN to 12,
        EntityType.PIGLIN_BRUTE to 40,
        EntityType.HOGLIN to 20,
        EntityType.MAGMA_CUBE to 10,
        EntityType.GUARDIAN to 35,
        EntityType.ELDER_GUARDIAN to 250,
        EntityType.WITHER to 2500,
        EntityType.ENDER_DRAGON to 5000,
    )

    /**
     * Get Combat XP for killing a specific entity type.
     * Returns 0 if the entity type does not grant Combat XP.
     *
     * Players always return 0 (no PvP XP).
     */
    fun getXp(entityType: EntityType): Int = XP_VALUES[entityType] ?: 0

    /**
     * Check if an entity type grants Combat XP.
     */
    fun grantsCombatXp(entityType: EntityType): Boolean = XP_VALUES.containsKey(entityType)

    /**
     * Get all entity types that grant Combat XP.
     * Useful for testing and debugging.
     */
    fun getAllCombatEntityTypes(): Set<EntityType> = XP_VALUES.keys
}
