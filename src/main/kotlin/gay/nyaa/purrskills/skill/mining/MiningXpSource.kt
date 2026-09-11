package gay.nyaa.purrskills.skill.mining

import org.bukkit.Material

/**
 * Mining XP source - defines XP values for mining blocks.
 * Pure data mapping - no Bukkit event handling here.
 *
 * Based on Hypixel SkyBlock Mining XP model.
 */
object MiningXpSource {

    /**
     * XP values for mining specific blocks.
     * Only blocks that grant Mining XP are listed here.
     */
    private val XP_VALUES = mapOf(
        Material.STONE to 1,
        Material.DEEPSLATE to 2,
        Material.COAL_ORE to 5,
        Material.DEEPSLATE_COAL_ORE to 6,
        Material.IRON_ORE to 8,
        Material.DEEPSLATE_IRON_ORE to 10,
        Material.COPPER_ORE to 6,
        Material.DEEPSLATE_COPPER_ORE to 7,
        Material.GOLD_ORE to 12,
        Material.DEEPSLATE_GOLD_ORE to 15,
        Material.REDSTONE_ORE to 10,
        Material.DEEPSLATE_REDSTONE_ORE to 12,
        Material.LAPIS_ORE to 12,
        Material.DEEPSLATE_LAPIS_ORE to 14,
        Material.DIAMOND_ORE to 50,
        Material.DEEPSLATE_DIAMOND_ORE to 60,
        Material.EMERALD_ORE to 75,
        Material.DEEPSLATE_EMERALD_ORE to 90,
        Material.NETHER_QUARTZ_ORE to 8,
        Material.ANCIENT_DEBRIS to 100,
    )

    /**
     * Get Mining XP for a specific material.
     * Returns 0 if the material does not grant Mining XP.
     */
    fun getXp(material: Material): Int = XP_VALUES[material] ?: 0

    /**
     * Check if a material grants Mining XP.
     */
    fun grantsMiningXp(material: Material): Boolean = XP_VALUES.containsKey(material)

    /**
     * Get all materials that grant Mining XP.
     * Useful for testing and debugging.
     */
    fun getAllMiningMaterials(): Set<Material> = XP_VALUES.keys
}
