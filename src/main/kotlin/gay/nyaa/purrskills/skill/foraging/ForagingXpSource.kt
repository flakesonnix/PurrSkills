package gay.nyaa.purrskills.skill.foraging

import org.bukkit.Material

/**
 * Foraging XP source - defines XP values for chopping logs and stems.
 * Pure data mapping - no Bukkit event handling here.
 *
 * Based on Hypixel SkyBlock Foraging XP model.
 */
object ForagingXpSource {

    /**
     * XP values for foraging specific materials (logs and stems).
     * Only materials that grant Foraging XP are listed here.
     */
    private val XP_VALUES = mapOf(
        Material.OAK_LOG to 5,
        Material.SPRUCE_LOG to 5,
        Material.BIRCH_LOG to 5,
        Material.JUNGLE_LOG to 6,
        Material.ACACIA_LOG to 6,
        Material.DARK_OAK_LOG to 6,
        Material.MANGROVE_LOG to 7,
        Material.CHERRY_LOG to 7,
        Material.CRIMSON_STEM to 7,
        Material.WARPED_STEM to 7,
    )

    /**
     * Get Foraging XP for a specific material.
     * Returns 0 if the material does not grant Foraging XP.
     */
    fun getXp(material: Material): Int = XP_VALUES[material] ?: 0

    /**
     * Check if a material grants Foraging XP.
     */
    fun grantsForagingXp(material: Material): Boolean = XP_VALUES.containsKey(material)

    /**
     * Get all materials that grant Foraging XP.
     * Useful for testing and debugging.
     */
    fun getAllForagingMaterials(): Set<Material> = XP_VALUES.keys
}
