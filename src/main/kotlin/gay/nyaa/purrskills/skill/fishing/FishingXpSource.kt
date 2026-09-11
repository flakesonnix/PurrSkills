package gay.nyaa.purrskills.skill.fishing

import org.bukkit.Material

/**
 * Fishing XP source - defines XP values for catching fish and other items.
 * Pure data mapping - no Bukkit event handling here.
 *
 * Based on Hypixel SkyBlock Fishing XP model.
 *
 * Categories:
 * - Fish: COD, SALMON, TROPICAL_FISH, PUFFERFISH
 * - Treasure: Valuable catches (enchanted books, saddles, etc.)
 * - Junk: Low-value catches (leather boots, sticks, etc.)
 */
object FishingXpSource {

    /**
     * XP values for specific fish types.
     */
    private val FISH_XP = mapOf(
        Material.COD to 10,
        Material.SALMON to 15,
        Material.TROPICAL_FISH to 20,
        Material.PUFFERFISH to 20,
    )

    /**
     * Materials that count as treasure catches.
     * Based on vanilla Minecraft fishing loot tables.
     */
    private val TREASURE_ITEMS = setOf(
        Material.BOW,
        Material.ENCHANTED_BOOK,
        Material.FISHING_ROD,
        Material.NAME_TAG,
        Material.NAUTILUS_SHELL,
        Material.SADDLE,
    )

    /**
     * Materials that count as junk catches.
     * Based on vanilla Minecraft fishing loot tables.
     */
    private val JUNK_ITEMS = setOf(
        Material.LILY_PAD,
        Material.BOWL,
        Material.LEATHER,
        Material.LEATHER_BOOTS,
        Material.ROTTEN_FLESH,
        Material.STICK,
        Material.STRING,
        Material.POTION, // Water bottle is a potion
        Material.BONE,
        Material.INK_SAC,
        Material.TRIPWIRE_HOOK,
    )

    /**
     * Get Fishing XP for a caught item.
     *
     * @param material The material of the caught item
     * @return XP amount (0 if unknown)
     */
    fun getXp(material: Material): Int = when {
        FISH_XP.containsKey(material) -> FISH_XP[material]!!
        TREASURE_ITEMS.contains(material) -> 50
        JUNK_ITEMS.contains(material) -> 5
        else -> 0
    }

    /**
     * Check if a material grants Fishing XP.
     */
    fun grantsFishingXp(material: Material): Boolean = FISH_XP.containsKey(material) ||
        TREASURE_ITEMS.contains(material) ||
        JUNK_ITEMS.contains(material)

    /**
     * Get the category of a caught item.
     * Useful for testing and debugging.
     */
    fun getCategory(material: Material): String? = when {
        FISH_XP.containsKey(material) -> "FISH"
        TREASURE_ITEMS.contains(material) -> "TREASURE"
        JUNK_ITEMS.contains(material) -> "JUNK"
        else -> null
    }

    /**
     * Get all materials that grant Fishing XP.
     */
    fun getAllFishingMaterials(): Set<Material> = FISH_XP.keys + TREASURE_ITEMS + JUNK_ITEMS
}
