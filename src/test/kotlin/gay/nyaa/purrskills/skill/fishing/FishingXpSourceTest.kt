package gay.nyaa.purrskills.skill.fishing

import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for FishingXpSource - validates Fishing XP values.
 * Pure test - no Bukkit mocking required.
 */
class FishingXpSourceTest {

    // Fish XP tests
    @Test
    fun `COD grants 10 XP`() {
        assertEquals(10, FishingXpSource.getXp(Material.COD))
        assertEquals("FISH", FishingXpSource.getCategory(Material.COD))
    }

    @Test
    fun `SALMON grants 15 XP`() {
        assertEquals(15, FishingXpSource.getXp(Material.SALMON))
        assertEquals("FISH", FishingXpSource.getCategory(Material.SALMON))
    }

    @Test
    fun `TROPICAL_FISH grants 20 XP`() {
        assertEquals(20, FishingXpSource.getXp(Material.TROPICAL_FISH))
        assertEquals("FISH", FishingXpSource.getCategory(Material.TROPICAL_FISH))
    }

    @Test
    fun `PUFFERFISH grants 20 XP`() {
        assertEquals(20, FishingXpSource.getXp(Material.PUFFERFISH))
        assertEquals("FISH", FishingXpSource.getCategory(Material.PUFFERFISH))
    }

    // Treasure XP tests
    @Test
    fun `treasure items grant 50 XP`() {
        assertEquals(50, FishingXpSource.getXp(Material.BOW))
        assertEquals(50, FishingXpSource.getXp(Material.ENCHANTED_BOOK))
        assertEquals(50, FishingXpSource.getXp(Material.FISHING_ROD))
        assertEquals(50, FishingXpSource.getXp(Material.NAME_TAG))
        assertEquals(50, FishingXpSource.getXp(Material.NAUTILUS_SHELL))
        assertEquals(50, FishingXpSource.getXp(Material.SADDLE))
    }

    @Test
    fun `treasure items are categorized as TREASURE`() {
        assertEquals("TREASURE", FishingXpSource.getCategory(Material.BOW))
        assertEquals("TREASURE", FishingXpSource.getCategory(Material.ENCHANTED_BOOK))
        assertEquals("TREASURE", FishingXpSource.getCategory(Material.SADDLE))
    }

    // Junk XP tests
    @Test
    fun `junk items grant 5 XP`() {
        assertEquals(5, FishingXpSource.getXp(Material.LILY_PAD))
        assertEquals(5, FishingXpSource.getXp(Material.BOWL))
        assertEquals(5, FishingXpSource.getXp(Material.LEATHER))
        assertEquals(5, FishingXpSource.getXp(Material.LEATHER_BOOTS))
        assertEquals(5, FishingXpSource.getXp(Material.STICK))
        assertEquals(5, FishingXpSource.getXp(Material.STRING))
        assertEquals(5, FishingXpSource.getXp(Material.POTION))
    }

    @Test
    fun `junk items are categorized as JUNK`() {
        assertEquals("JUNK", FishingXpSource.getCategory(Material.LILY_PAD))
        assertEquals("JUNK", FishingXpSource.getCategory(Material.LEATHER_BOOTS))
        assertEquals("JUNK", FishingXpSource.getCategory(Material.STICK))
    }

    // Non-fishing items
    @Test
    fun `non-fishing items grant 0 XP`() {
        assertEquals(0, FishingXpSource.getXp(Material.DIAMOND))
        assertEquals(0, FishingXpSource.getXp(Material.STONE))
        assertEquals(0, FishingXpSource.getXp(Material.DIRT))
        assertEquals(0, FishingXpSource.getXp(Material.OAK_LOG))
    }

    @Test
    fun `non-fishing items have no category`() {
        assertNull(FishingXpSource.getCategory(Material.DIAMOND))
        assertNull(FishingXpSource.getCategory(Material.STONE))
        assertNull(FishingXpSource.getCategory(Material.DIRT))
    }

    // grantsFishingXp tests
    @Test
    fun `grantsFishingXp returns true for fish`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.COD))
        assertTrue(FishingXpSource.grantsFishingXp(Material.SALMON))
        assertTrue(FishingXpSource.grantsFishingXp(Material.TROPICAL_FISH))
        assertTrue(FishingXpSource.grantsFishingXp(Material.PUFFERFISH))
    }

    @Test
    fun `grantsFishingXp returns true for treasure`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.BOW))
        assertTrue(FishingXpSource.grantsFishingXp(Material.SADDLE))
        assertTrue(FishingXpSource.grantsFishingXp(Material.ENCHANTED_BOOK))
    }

    @Test
    fun `grantsFishingXp returns true for junk`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.STICK))
        assertTrue(FishingXpSource.grantsFishingXp(Material.LEATHER_BOOTS))
        assertTrue(FishingXpSource.grantsFishingXp(Material.BOWL))
    }

    @Test
    fun `grantsFishingXp returns false for non-fishing items`() {
        assertFalse(FishingXpSource.grantsFishingXp(Material.DIAMOND))
        assertFalse(FishingXpSource.grantsFishingXp(Material.STONE))
        assertFalse(FishingXpSource.grantsFishingXp(Material.OAK_LOG))
    }

    // getAllFishingMaterials test
    @Test
    fun `getAllFishingMaterials returns all fishing items`() {
        val materials = FishingXpSource.getAllFishingMaterials()

        // Should contain fish (4) + treasure (6) + junk (11) = 21 items
        assertEquals(21, materials.size)

        // Spot check each category
        assertTrue(materials.contains(Material.COD))
        assertTrue(materials.contains(Material.ENCHANTED_BOOK))
        assertTrue(materials.contains(Material.STICK))
    }

    // XP tier tests
    @Test
    fun `treasure grants more XP than fish`() {
        val fishXp = FishingXpSource.getXp(Material.SALMON)
        val treasureXp = FishingXpSource.getXp(Material.SADDLE)

        assertTrue(treasureXp > fishXp)
    }

    @Test
    fun `fish grants more XP than junk`() {
        val junkXp = FishingXpSource.getXp(Material.STICK)
        val fishXp = FishingXpSource.getXp(Material.COD)

        assertTrue(fishXp > junkXp)
    }

    @Test
    fun `XP progression is JUNK less than FISH less than TREASURE`() {
        val junkXp = 5
        val fishXp = 10 // minimum fish XP (COD)
        val treasureXp = 50

        assertEquals(junkXp, FishingXpSource.getXp(Material.STICK))
        assertEquals(fishXp, FishingXpSource.getXp(Material.COD))
        assertEquals(treasureXp, FishingXpSource.getXp(Material.SADDLE))

        assertTrue(junkXp < fishXp)
        assertTrue(fishXp < treasureXp)
    }

    // Independence tests
    @Test
    fun `fishing XP is independent from mining XP`() {
        // Fishing items should not grant mining XP
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.COD))
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.SADDLE))

        // Mining materials should not grant fishing XP
        assertEquals(0, FishingXpSource.getXp(Material.STONE))
        assertEquals(0, FishingXpSource.getXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `fishing XP is independent from farming XP`() {
        // Fishing items should not grant farming XP
        assertEquals(0, gay.nyaa.purrskills.skill.farming.FarmingXpSource.getXp(Material.COD))
        assertEquals(0, gay.nyaa.purrskills.skill.farming.FarmingXpSource.getXp(Material.SADDLE))

        // Farming materials should not grant fishing XP
        assertEquals(0, FishingXpSource.getXp(Material.WHEAT))
        assertEquals(0, FishingXpSource.getXp(Material.PUMPKIN))
    }

    @Test
    fun `fishing XP is independent from foraging XP`() {
        // Fishing items should not grant foraging XP
        assertEquals(0, gay.nyaa.purrskills.skill.foraging.ForagingXpSource.getXp(Material.COD))
        assertEquals(0, gay.nyaa.purrskills.skill.foraging.ForagingXpSource.getXp(Material.SADDLE))

        // Foraging materials should not grant fishing XP
        assertEquals(0, FishingXpSource.getXp(Material.OAK_LOG))
        assertEquals(0, FishingXpSource.getXp(Material.CHERRY_LOG))
    }

    @Test
    fun `fishing XP is independent from combat XP`() {
        // Fishing items are Materials, Combat uses EntityTypes
        // No cross-contamination possible - different type systems

        // But we can verify fishing items don't accidentally grant combat XP
        // (This would be a type error, so this test is more documentation)
    }

    // Specific vanilla fishing items
    @Test
    fun `all vanilla fish types are defined`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.COD))
        assertTrue(FishingXpSource.grantsFishingXp(Material.SALMON))
        assertTrue(FishingXpSource.grantsFishingXp(Material.TROPICAL_FISH))
        assertTrue(FishingXpSource.grantsFishingXp(Material.PUFFERFISH))
    }

    @Test
    fun `common treasure items are defined`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.BOW))
        assertTrue(FishingXpSource.grantsFishingXp(Material.ENCHANTED_BOOK))
        assertTrue(FishingXpSource.grantsFishingXp(Material.SADDLE))
        assertTrue(FishingXpSource.grantsFishingXp(Material.NAME_TAG))
    }

    @Test
    fun `common junk items are defined`() {
        assertTrue(FishingXpSource.grantsFishingXp(Material.STICK))
        assertTrue(FishingXpSource.grantsFishingXp(Material.LEATHER_BOOTS))
        assertTrue(FishingXpSource.grantsFishingXp(Material.STRING))
        assertTrue(FishingXpSource.grantsFishingXp(Material.BOWL))
    }
}
