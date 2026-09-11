package gay.nyaa.purrskills.skill.mining

import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for MiningXpSource - validates Mining XP values.
 * Pure test - no Bukkit mocking required.
 */
class MiningXpSourceTest {

    @Test
    fun `STONE grants 1 XP`() {
        assertEquals(1, MiningXpSource.getXp(Material.STONE))
    }

    @Test
    fun `DEEPSLATE grants 2 XP`() {
        assertEquals(2, MiningXpSource.getXp(Material.DEEPSLATE))
    }

    @Test
    fun `COAL_ORE grants 5 XP`() {
        assertEquals(5, MiningXpSource.getXp(Material.COAL_ORE))
    }

    @Test
    fun `DEEPSLATE_COAL_ORE grants 6 XP`() {
        assertEquals(6, MiningXpSource.getXp(Material.DEEPSLATE_COAL_ORE))
    }

    @Test
    fun `IRON_ORE grants 8 XP`() {
        assertEquals(8, MiningXpSource.getXp(Material.IRON_ORE))
    }

    @Test
    fun `DEEPSLATE_IRON_ORE grants 10 XP`() {
        assertEquals(10, MiningXpSource.getXp(Material.DEEPSLATE_IRON_ORE))
    }

    @Test
    fun `COPPER_ORE grants 6 XP`() {
        assertEquals(6, MiningXpSource.getXp(Material.COPPER_ORE))
    }

    @Test
    fun `DEEPSLATE_COPPER_ORE grants 7 XP`() {
        assertEquals(7, MiningXpSource.getXp(Material.DEEPSLATE_COPPER_ORE))
    }

    @Test
    fun `GOLD_ORE grants 12 XP`() {
        assertEquals(12, MiningXpSource.getXp(Material.GOLD_ORE))
    }

    @Test
    fun `DEEPSLATE_GOLD_ORE grants 15 XP`() {
        assertEquals(15, MiningXpSource.getXp(Material.DEEPSLATE_GOLD_ORE))
    }

    @Test
    fun `REDSTONE_ORE grants 10 XP`() {
        assertEquals(10, MiningXpSource.getXp(Material.REDSTONE_ORE))
    }

    @Test
    fun `DEEPSLATE_REDSTONE_ORE grants 12 XP`() {
        assertEquals(12, MiningXpSource.getXp(Material.DEEPSLATE_REDSTONE_ORE))
    }

    @Test
    fun `LAPIS_ORE grants 12 XP`() {
        assertEquals(12, MiningXpSource.getXp(Material.LAPIS_ORE))
    }

    @Test
    fun `DEEPSLATE_LAPIS_ORE grants 14 XP`() {
        assertEquals(14, MiningXpSource.getXp(Material.DEEPSLATE_LAPIS_ORE))
    }

    @Test
    fun `DIAMOND_ORE grants 50 XP`() {
        assertEquals(50, MiningXpSource.getXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `DEEPSLATE_DIAMOND_ORE grants 60 XP`() {
        assertEquals(60, MiningXpSource.getXp(Material.DEEPSLATE_DIAMOND_ORE))
    }

    @Test
    fun `EMERALD_ORE grants 75 XP`() {
        assertEquals(75, MiningXpSource.getXp(Material.EMERALD_ORE))
    }

    @Test
    fun `DEEPSLATE_EMERALD_ORE grants 90 XP`() {
        assertEquals(90, MiningXpSource.getXp(Material.DEEPSLATE_EMERALD_ORE))
    }

    @Test
    fun `NETHER_QUARTZ_ORE grants 8 XP`() {
        assertEquals(8, MiningXpSource.getXp(Material.NETHER_QUARTZ_ORE))
    }

    @Test
    fun `ANCIENT_DEBRIS grants 100 XP`() {
        assertEquals(100, MiningXpSource.getXp(Material.ANCIENT_DEBRIS))
    }

    @Test
    fun `non-mining blocks grant 0 XP`() {
        assertEquals(0, MiningXpSource.getXp(Material.DIRT))
        assertEquals(0, MiningXpSource.getXp(Material.GRASS_BLOCK))
        assertEquals(0, MiningXpSource.getXp(Material.OAK_LOG))
        assertEquals(0, MiningXpSource.getXp(Material.WHEAT))
        assertEquals(0, MiningXpSource.getXp(Material.AIR))
    }

    @Test
    fun `grantsMiningXp returns true for mining blocks`() {
        assertTrue(MiningXpSource.grantsMiningXp(Material.STONE))
        assertTrue(MiningXpSource.grantsMiningXp(Material.DIAMOND_ORE))
        assertTrue(MiningXpSource.grantsMiningXp(Material.ANCIENT_DEBRIS))
    }

    @Test
    fun `grantsMiningXp returns false for non-mining blocks`() {
        assertFalse(MiningXpSource.grantsMiningXp(Material.DIRT))
        assertFalse(MiningXpSource.grantsMiningXp(Material.OAK_LOG))
        assertFalse(MiningXpSource.grantsMiningXp(Material.WHEAT))
    }

    @Test
    fun `getAllMiningMaterials returns all 20 materials`() {
        val materials = MiningXpSource.getAllMiningMaterials()
        assertEquals(20, materials.size)

        // Spot check a few
        assertTrue(materials.contains(Material.STONE))
        assertTrue(materials.contains(Material.ANCIENT_DEBRIS))
        assertTrue(materials.contains(Material.DIAMOND_ORE))
    }

    @Test
    fun `deepslate variants always grant more XP than regular variants`() {
        assertTrue(MiningXpSource.getXp(Material.DEEPSLATE_COAL_ORE) > MiningXpSource.getXp(Material.COAL_ORE))
        assertTrue(MiningXpSource.getXp(Material.DEEPSLATE_IRON_ORE) > MiningXpSource.getXp(Material.IRON_ORE))
        assertTrue(MiningXpSource.getXp(Material.DEEPSLATE_DIAMOND_ORE) > MiningXpSource.getXp(Material.DIAMOND_ORE))
    }
}
