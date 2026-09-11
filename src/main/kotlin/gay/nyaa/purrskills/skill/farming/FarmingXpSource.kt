package gay.nyaa.purrskills.skill.farming

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData

/**
 * Farming XP source - defines XP values for farming/harvesting crops.
 * Pure data mapping - no Bukkit event handling here.
 *
 * Based on Hypixel SkyBlock Farming XP model.
 */
object FarmingXpSource {

    /**
     * XP values for farming specific crops.
     * Only crops that grant Farming XP are listed here.
     */
    private val XP_VALUES = mapOf(
        Material.WHEAT to 2,
        Material.CARROTS to 2,
        Material.POTATOES to 2,
        Material.BEETROOTS to 2,
        Material.PUMPKIN to 5,
        Material.MELON to 3,
        Material.SUGAR_CANE to 2,
        Material.COCOA to 3,
        Material.NETHER_WART to 3,
        Material.SWEET_BERRY_BUSH to 2,
        Material.CACTUS to 2,
    )

    /**
     * Get Farming XP for a specific material.
     * Returns 0 if the material does not grant Farming XP.
     */
    fun getXp(material: Material): Int = XP_VALUES[material] ?: 0

    /**
     * Check if a material grants Farming XP.
     */
    fun grantsFarmingXp(material: Material): Boolean = XP_VALUES.containsKey(material)

    /**
     * Check if a crop is mature and ready for harvest.
     * For age-based crops (wheat, carrots, etc.), checks if age == maxAge.
     * For non-age crops (pumpkin, melon, etc.), returns true.
     *
     * @param blockData The block data to check
     * @return true if the crop should grant XP when broken
     */
    fun isMature(blockData: BlockData): Boolean = when (blockData) {
        is Ageable -> blockData.age == blockData.maximumAge
        else -> true // Non-age blocks like pumpkin/melon are always "mature"
    }

    /**
     * Get all materials that grant Farming XP.
     * Useful for testing and debugging.
     */
    fun getAllFarmingMaterials(): Set<Material> = XP_VALUES.keys
}
