package gay.nyaa.purrskills.skill.foraging

import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for ForagingXpSource - validates Foraging XP values.
 * Pure test - no Bukkit mocking required.
 */
class ForagingXpSourceTest {

    @Test
    fun `OAK_LOG grants 5 XP`() {
        assertEquals(5, ForagingXpSource.getXp(Material.OAK_LOG))
    }

    @Test
    fun `SPRUCE_LOG grants 5 XP`() {
        assertEquals(5, ForagingXpSource.getXp(Material.SPRUCE_LOG))
    }

    @Test
    fun `BIRCH_LOG grants 5 XP`() {
        assertEquals(5, ForagingXpSource.getXp(Material.BIRCH_LOG))
    }

    @Test
    fun `JUNGLE_LOG grants 6 XP`() {
        assertEquals(6, ForagingXpSource.getXp(Material.JUNGLE_LOG))
    }

    @Test
    fun `ACACIA_LOG grants 6 XP`() {
        assertEquals(6, ForagingXpSource.getXp(Material.ACACIA_LOG))
    }

    @Test
    fun `DARK_OAK_LOG grants 6 XP`() {
        assertEquals(6, ForagingXpSource.getXp(Material.DARK_OAK_LOG))
    }

    @Test
    fun `MANGROVE_LOG grants 7 XP`() {
        assertEquals(7, ForagingXpSource.getXp(Material.MANGROVE_LOG))
    }

    @Test
    fun `CHERRY_LOG grants 7 XP`() {
        assertEquals(7, ForagingXpSource.getXp(Material.CHERRY_LOG))
    }

    @Test
    fun `CRIMSON_STEM grants 7 XP`() {
        assertEquals(7, ForagingXpSource.getXp(Material.CRIMSON_STEM))
    }

    @Test
    fun `WARPED_STEM grants 7 XP`() {
        assertEquals(7, ForagingXpSource.getXp(Material.WARPED_STEM))
    }

    @Test
    fun `leaves grant 0 XP`() {
        assertEquals(0, ForagingXpSource.getXp(Material.OAK_LEAVES))
        assertEquals(0, ForagingXpSource.getXp(Material.SPRUCE_LEAVES))
        assertEquals(0, ForagingXpSource.getXp(Material.BIRCH_LEAVES))
        assertEquals(0, ForagingXpSource.getXp(Material.JUNGLE_LEAVES))
    }

    @Test
    fun `non-foraging blocks grant 0 XP`() {
        assertEquals(0, ForagingXpSource.getXp(Material.STONE))
        assertEquals(0, ForagingXpSource.getXp(Material.DIRT))
        assertEquals(0, ForagingXpSource.getXp(Material.DIAMOND_ORE))
        assertEquals(0, ForagingXpSource.getXp(Material.WHEAT))
    }

    @Test
    fun `grantsForagingXp returns true for logs and stems`() {
        assertTrue(ForagingXpSource.grantsForagingXp(Material.OAK_LOG))
        assertTrue(ForagingXpSource.grantsForagingXp(Material.SPRUCE_LOG))
        assertTrue(ForagingXpSource.grantsForagingXp(Material.MANGROVE_LOG))
        assertTrue(ForagingXpSource.grantsForagingXp(Material.CRIMSON_STEM))
        assertTrue(ForagingXpSource.grantsForagingXp(Material.WARPED_STEM))
    }

    @Test
    fun `grantsForagingXp returns false for leaves`() {
        assertFalse(ForagingXpSource.grantsForagingXp(Material.OAK_LEAVES))
        assertFalse(ForagingXpSource.grantsForagingXp(Material.SPRUCE_LEAVES))
    }

    @Test
    fun `grantsForagingXp returns false for non-foraging blocks`() {
        assertFalse(ForagingXpSource.grantsForagingXp(Material.STONE))
        assertFalse(ForagingXpSource.grantsForagingXp(Material.DIRT))
        assertFalse(ForagingXpSource.grantsForagingXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `getAllForagingMaterials returns all 10 materials`() {
        val materials = ForagingXpSource.getAllForagingMaterials()
        assertEquals(10, materials.size)

        // Spot check
        assertTrue(materials.contains(Material.OAK_LOG))
        assertTrue(materials.contains(Material.CHERRY_LOG))
        assertTrue(materials.contains(Material.CRIMSON_STEM))
    }

    @Test
    fun `foraging XP is independent from mining XP`() {
        // Foraging materials should not grant mining XP
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.OAK_LOG))
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.CRIMSON_STEM))

        // Mining materials should not grant foraging XP
        assertEquals(0, ForagingXpSource.getXp(Material.STONE))
        assertEquals(0, ForagingXpSource.getXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `foraging XP is independent from farming XP`() {
        // Foraging materials should not grant farming XP
        assertEquals(0, gay.nyaa.purrskills.skill.farming.FarmingXpSource.getXp(Material.OAK_LOG))
        assertEquals(0, gay.nyaa.purrskills.skill.farming.FarmingXpSource.getXp(Material.CHERRY_LOG))

        // Farming materials should not grant foraging XP
        assertEquals(0, ForagingXpSource.getXp(Material.WHEAT))
        assertEquals(0, ForagingXpSource.getXp(Material.PUMPKIN))
    }

    @Test
    fun `basic logs grant less XP than exotic logs`() {
        val basicLogs = listOf(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG)
        val jungleLogs = listOf(Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG)
        val exoticLogs = listOf(Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM)

        // Basic logs = 5 XP
        basicLogs.forEach { assertEquals(5, ForagingXpSource.getXp(it)) }

        // Jungle/Acacia/Dark Oak = 6 XP
        jungleLogs.forEach { assertEquals(6, ForagingXpSource.getXp(it)) }

        // Exotic = 7 XP
        exoticLogs.forEach { assertEquals(7, ForagingXpSource.getXp(it)) }
    }
}
