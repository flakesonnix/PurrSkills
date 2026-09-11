package gay.nyaa.purrskills.skill.farming

import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for FarmingXpSource - validates Farming XP values.
 */
class FarmingXpSourceTest {

    @Test
    fun `WHEAT grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.WHEAT))
    }

    @Test
    fun `CARROTS grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.CARROTS))
    }

    @Test
    fun `POTATOES grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.POTATOES))
    }

    @Test
    fun `BEETROOTS grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.BEETROOTS))
    }

    @Test
    fun `PUMPKIN grants 5 XP`() {
        assertEquals(5, FarmingXpSource.getXp(Material.PUMPKIN))
    }

    @Test
    fun `MELON grants 3 XP`() {
        assertEquals(3, FarmingXpSource.getXp(Material.MELON))
    }

    @Test
    fun `SUGAR_CANE grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.SUGAR_CANE))
    }

    @Test
    fun `COCOA grants 3 XP`() {
        assertEquals(3, FarmingXpSource.getXp(Material.COCOA))
    }

    @Test
    fun `NETHER_WART grants 3 XP`() {
        assertEquals(3, FarmingXpSource.getXp(Material.NETHER_WART))
    }

    @Test
    fun `SWEET_BERRY_BUSH grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.SWEET_BERRY_BUSH))
    }

    @Test
    fun `CACTUS grants 2 XP`() {
        assertEquals(2, FarmingXpSource.getXp(Material.CACTUS))
    }

    @Test
    fun `non-farming blocks grant 0 XP`() {
        assertEquals(0, FarmingXpSource.getXp(Material.DIRT))
        assertEquals(0, FarmingXpSource.getXp(Material.STONE))
        assertEquals(0, FarmingXpSource.getXp(Material.OAK_LOG))
        assertEquals(0, FarmingXpSource.getXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `grantsFarmingXp returns true for farming crops`() {
        assertTrue(FarmingXpSource.grantsFarmingXp(Material.WHEAT))
        assertTrue(FarmingXpSource.grantsFarmingXp(Material.PUMPKIN))
        assertTrue(FarmingXpSource.grantsFarmingXp(Material.MELON))
    }

    @Test
    fun `grantsFarmingXp returns false for non-farming blocks`() {
        assertFalse(FarmingXpSource.grantsFarmingXp(Material.DIRT))
        assertFalse(FarmingXpSource.grantsFarmingXp(Material.STONE))
        assertFalse(FarmingXpSource.grantsFarmingXp(Material.DIAMOND_ORE))
    }

    @Test
    fun `getAllFarmingMaterials returns all 11 materials`() {
        val materials = FarmingXpSource.getAllFarmingMaterials()
        assertEquals(11, materials.size)

        // Spot check
        assertTrue(materials.contains(Material.WHEAT))
        assertTrue(materials.contains(Material.PUMPKIN))
        assertTrue(materials.contains(Material.MELON))
    }

    @Test
    fun `isMature returns true for mature Ageable crop`() {
        val ageable = mockk<Ageable>()
        every { ageable.age } returns 7
        every { ageable.maximumAge } returns 7

        assertTrue(FarmingXpSource.isMature(ageable))
    }

    @Test
    fun `isMature returns false for immature Ageable crop`() {
        val ageable = mockk<Ageable>()
        every { ageable.age } returns 3
        every { ageable.maximumAge } returns 7

        assertFalse(FarmingXpSource.isMature(ageable))
    }

    @Test
    fun `isMature returns true for non-Ageable blocks`() {
        val blockData = mockk<org.bukkit.block.data.BlockData>()

        // Non-ageable blocks like pumpkin/melon are always "mature"
        assertTrue(FarmingXpSource.isMature(blockData))
    }

    @Test
    fun `farming XP is independent from mining XP`() {
        // Farming materials should not grant mining XP
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.WHEAT))
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(Material.PUMPKIN))

        // Mining materials should not grant farming XP
        assertEquals(0, FarmingXpSource.getXp(Material.STONE))
        assertEquals(0, FarmingXpSource.getXp(Material.DIAMOND_ORE))
    }
}
