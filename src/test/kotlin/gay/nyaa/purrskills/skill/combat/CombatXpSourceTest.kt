package gay.nyaa.purrskills.skill.combat

import org.bukkit.entity.EntityType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for CombatXpSource - validates Combat XP values.
 * Pure test - no Bukkit mocking required.
 */
class CombatXpSourceTest {

    @Test
    fun `ZOMBIE grants 10 XP`() {
        assertEquals(10, CombatXpSource.getXp(EntityType.ZOMBIE))
    }

    @Test
    fun `SKELETON grants 12 XP`() {
        assertEquals(12, CombatXpSource.getXp(EntityType.SKELETON))
    }

    @Test
    fun `SPIDER grants 10 XP`() {
        assertEquals(10, CombatXpSource.getXp(EntityType.SPIDER))
    }

    @Test
    fun `CREEPER grants 15 XP`() {
        assertEquals(15, CombatXpSource.getXp(EntityType.CREEPER))
    }

    @Test
    fun `ENDERMAN grants 30 XP`() {
        assertEquals(30, CombatXpSource.getXp(EntityType.ENDERMAN))
    }

    @Test
    fun `WITCH grants 35 XP`() {
        assertEquals(35, CombatXpSource.getXp(EntityType.WITCH))
    }

    @Test
    fun `SLIME grants 5 XP`() {
        assertEquals(5, CombatXpSource.getXp(EntityType.SLIME))
    }

    @Test
    fun `PHANTOM grants 25 XP`() {
        assertEquals(25, CombatXpSource.getXp(EntityType.PHANTOM))
    }

    @Test
    fun `BLAZE grants 30 XP`() {
        assertEquals(30, CombatXpSource.getXp(EntityType.BLAZE))
    }

    @Test
    fun `WITHER_SKELETON grants 40 XP`() {
        assertEquals(40, CombatXpSource.getXp(EntityType.WITHER_SKELETON))
    }

    @Test
    fun `PIGLIN grants 12 XP`() {
        assertEquals(12, CombatXpSource.getXp(EntityType.PIGLIN))
    }

    @Test
    fun `PIGLIN_BRUTE grants 40 XP`() {
        assertEquals(40, CombatXpSource.getXp(EntityType.PIGLIN_BRUTE))
    }

    @Test
    fun `HOGLIN grants 20 XP`() {
        assertEquals(20, CombatXpSource.getXp(EntityType.HOGLIN))
    }

    @Test
    fun `MAGMA_CUBE grants 10 XP`() {
        assertEquals(10, CombatXpSource.getXp(EntityType.MAGMA_CUBE))
    }

    @Test
    fun `GUARDIAN grants 35 XP`() {
        assertEquals(35, CombatXpSource.getXp(EntityType.GUARDIAN))
    }

    @Test
    fun `ELDER_GUARDIAN grants 250 XP`() {
        assertEquals(250, CombatXpSource.getXp(EntityType.ELDER_GUARDIAN))
    }

    @Test
    fun `WITHER grants 2500 XP`() {
        assertEquals(2500, CombatXpSource.getXp(EntityType.WITHER))
    }

    @Test
    fun `ENDER_DRAGON grants 5000 XP`() {
        assertEquals(5000, CombatXpSource.getXp(EntityType.ENDER_DRAGON))
    }

    @Test
    fun `PLAYER grants 0 XP (no PvP)`() {
        assertEquals(0, CombatXpSource.getXp(EntityType.PLAYER))
    }

    @Test
    fun `non-combat entities grant 0 XP`() {
        assertEquals(0, CombatXpSource.getXp(EntityType.COW))
        assertEquals(0, CombatXpSource.getXp(EntityType.PIG))
        assertEquals(0, CombatXpSource.getXp(EntityType.CHICKEN))
        assertEquals(0, CombatXpSource.getXp(EntityType.VILLAGER))
    }

    @Test
    fun `grantsCombatXp returns true for combat entities`() {
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.ZOMBIE))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.SKELETON))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.ENDER_DRAGON))
    }

    @Test
    fun `grantsCombatXp returns false for players`() {
        assertFalse(CombatXpSource.grantsCombatXp(EntityType.PLAYER))
    }

    @Test
    fun `grantsCombatXp returns false for peaceful entities`() {
        assertFalse(CombatXpSource.grantsCombatXp(EntityType.COW))
        assertFalse(CombatXpSource.grantsCombatXp(EntityType.PIG))
        assertFalse(CombatXpSource.grantsCombatXp(EntityType.VILLAGER))
    }

    @Test
    fun `getAllCombatEntityTypes returns all 18 entities`() {
        val entities = CombatXpSource.getAllCombatEntityTypes()
        assertEquals(18, entities.size)

        // Spot check
        assertTrue(entities.contains(EntityType.ZOMBIE))
        assertTrue(entities.contains(EntityType.ENDER_DRAGON))
        assertTrue(entities.contains(EntityType.WITHER))
    }

    @Test
    fun `combat XP is independent from mining XP`() {
        // Combat entities should not grant mining XP
        assertEquals(0, gay.nyaa.purrskills.skill.mining.MiningXpSource.getXp(org.bukkit.Material.ZOMBIE_HEAD))

        // Mining materials should not exist as entities
        // (No cross-contamination possible - different type systems)
    }

    @Test
    fun `combat XP is independent from farming XP`() {
        // Combat entities should not grant farming XP
        // (No cross-contamination possible - different type systems)

        // Farming materials should not exist as entities
        assertEquals(0, gay.nyaa.purrskills.skill.farming.FarmingXpSource.getXp(org.bukkit.Material.ZOMBIE_HEAD))
    }

    @Test
    fun `combat XP is independent from foraging XP`() {
        // Combat entities should not grant foraging XP
        // (No cross-contamination possible - different type systems)

        // Foraging materials should not exist as entities
        assertEquals(0, gay.nyaa.purrskills.skill.foraging.ForagingXpSource.getXp(org.bukkit.Material.ZOMBIE_HEAD))
    }

    @Test
    fun `boss mobs grant significantly more XP than regular mobs`() {
        val regularMobs = listOf(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER)
        val eliteMobs = listOf(EntityType.WITHER_SKELETON, EntityType.PIGLIN_BRUTE, EntityType.ELDER_GUARDIAN)
        val bosses = listOf(EntityType.WITHER, EntityType.ENDER_DRAGON)

        // Regular mobs: 10-15 XP
        regularMobs.forEach { assertTrue(CombatXpSource.getXp(it) in 10..15) }

        // Elite mobs: 40-250 XP
        eliteMobs.forEach { assertTrue(CombatXpSource.getXp(it) in 40..250) }

        // Bosses: 2500+ XP
        bosses.forEach { assertTrue(CombatXpSource.getXp(it) >= 2500) }
    }

    @Test
    fun `nether mobs exist in XP table`() {
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.BLAZE))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.WITHER_SKELETON))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.PIGLIN))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.HOGLIN))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.MAGMA_CUBE))
    }

    @Test
    fun `end mobs exist in XP table`() {
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.ENDERMAN))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.ENDER_DRAGON))
    }

    @Test
    fun `ocean mobs exist in XP table`() {
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.GUARDIAN))
        assertTrue(CombatXpSource.grantsCombatXp(EntityType.ELDER_GUARDIAN))
    }
}
