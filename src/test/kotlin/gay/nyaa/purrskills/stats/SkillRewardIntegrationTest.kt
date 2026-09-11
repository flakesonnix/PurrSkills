package gay.nyaa.purrskills.stats

import gay.nyaa.purrskills.skill.Skill
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Integration tests for skill rewards system.
 * Tests idempotency, source isolation, and multi-level-up scenarios.
 */
class SkillRewardIntegrationTest {

    @Test
    fun `Refresh is idempotent - multiple refreshes produce same result`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Simulate Mining Level 10
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)

        // Refresh 1
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards)
        val stats1 = manager.calculateStats(uuid)

        // Refresh 2
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards)
        val stats2 = manager.calculateStats(uuid)

        // Refresh 3
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards)
        val stats3 = manager.calculateStats(uuid)

        // All should produce the same stats
        assertEquals(stats1.getStat(StatType.MINING_SPEED), stats2.getStat(StatType.MINING_SPEED))
        assertEquals(stats2.getStat(StatType.MINING_SPEED), stats3.getStat(StatType.MINING_SPEED))

        // Should be exactly +10 Mining Speed (not +30)
        assertEquals(110.0, stats3.getStat(StatType.MINING_SPEED)) // Base 100 + 10
    }

    @Test
    fun `Level-up replaces old rewards correctly`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Start at level 5
        val rewards5 = SkillRewardCalculator.calculateRewards(Skill.MINING, 5)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards5)

        val stats5 = manager.calculateStats(uuid)
        assertEquals(105.0, stats5.getStat(StatType.MINING_SPEED)) // Base 100 + 5

        // Level up to 10
        val rewards10 = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards10)

        val stats10 = manager.calculateStats(uuid)
        assertEquals(110.0, stats10.getStat(StatType.MINING_SPEED)) // Base 100 + 10 (not +15!)
    }

    @Test
    fun `Multi-level-up from 9 to 12 produces correct result`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Start at level 9
        val rewards9 = SkillRewardCalculator.calculateRewards(Skill.MINING, 9)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards9)

        val stats9 = manager.calculateStats(uuid)
        assertEquals(109.0, stats9.getStat(StatType.MINING_SPEED)) // Base 100 + 9

        // Jump to level 12 (multi-level-up)
        val rewards12 = SkillRewardCalculator.calculateRewards(Skill.MINING, 12)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, rewards12)

        val stats12 = manager.calculateStats(uuid)
        assertEquals(112.0, stats12.getStat(StatType.MINING_SPEED)) // Base 100 + 12 (not +9+3!)
    }

    @Test
    fun `Mining rewards do not affect Farming rewards`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add Mining Level 10 rewards
        val miningRewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        manager.addModifiers(uuid, miningRewards)

        // Add Farming Level 15 rewards
        val farmingRewards = SkillRewardCalculator.calculateRewards(Skill.FARMING, 15)
        manager.addModifiers(uuid, farmingRewards)

        val stats = manager.calculateStats(uuid)

        // Mining stats
        assertEquals(110.0, stats.getStat(StatType.MINING_SPEED))
        assertEquals(5.0, stats.getStat(StatType.MINING_FORTUNE))

        // Farming stats
        assertEquals(115.0, stats.getStat(StatType.FARMING_SPEED))
        assertEquals(15.0, stats.getStat(StatType.FARMING_FORTUNE))
    }

    @Test
    fun `Refreshing Mining does not affect Item modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add Mining Level 10 rewards
        val miningRewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        manager.addModifiers(uuid, miningRewards)

        // Add item modifier (pickaxe)
        val itemMod = StatModifier.additive(StatType.MINING_SPEED, 20.0, StatSource.ITEM_MAIN_HAND)
        manager.addModifier(uuid, itemMod)

        val statsBefore = manager.calculateStats(uuid)
        assertEquals(130.0, statsBefore.getStat(StatType.MINING_SPEED)) // Base 100 + Skill 10 + Item 20

        // Refresh mining skill (level unchanged)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, miningRewards)

        val statsAfter = manager.calculateStats(uuid)
        assertEquals(130.0, statsAfter.getStat(StatType.MINING_SPEED)) // Same result, item mod preserved
    }

    @Test
    fun `Refreshing Mining does not affect other skills`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add rewards for all skills
        for (skill in Skill.entries) {
            val rewards = SkillRewardCalculator.calculateRewards(skill, 10)
            manager.addModifiers(uuid, rewards)
        }

        val statsBefore = manager.calculateStats(uuid)

        // Refresh only Mining
        val miningRewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 15)
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)
        manager.addModifiers(uuid, miningRewards)

        val statsAfter = manager.calculateStats(uuid)

        // Mining should change (10 → 15)
        assertEquals(115.0, statsAfter.getStat(StatType.MINING_SPEED)) // Base 100 + 15

        // Other skills should be unchanged
        assertEquals(statsBefore.getStat(StatType.FARMING_SPEED), statsAfter.getStat(StatType.FARMING_SPEED))
        assertEquals(statsBefore.getStat(StatType.HEALTH), statsAfter.getStat(StatType.HEALTH))
    }

    @Test
    fun `Multiple skills can coexist with correct stats`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Mining Level 10
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.MINING, 10))

        // Farming Level 15
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.FARMING, 15))

        // Combat Level 20
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.COMBAT, 20))

        // Foraging Level 5
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.FORAGING, 5))

        // Fishing Level 8
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.FISHING, 8))

        val stats = manager.calculateStats(uuid)

        // Verify each skill's contribution
        assertEquals(110.0, stats.getStat(StatType.MINING_SPEED))
        assertEquals(115.0, stats.getStat(StatType.FARMING_SPEED))
        assertEquals(140.0, stats.getStat(StatType.HEALTH)) // Base 100 + 40 (Combat 20 × 2)
        assertEquals(105.0, stats.getStat(StatType.FORAGING_SPEED))
        assertEquals(108.0, stats.getStat(StatType.FISHING_SPEED))
    }

    @Test
    fun `Removing all modifiers resets to base values`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add rewards
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.MINING, 10))

        val statsBefore = manager.calculateStats(uuid)
        assertEquals(110.0, statsBefore.getStat(StatType.MINING_SPEED))

        // Remove all modifiers
        manager.clearModifiers(uuid)

        val statsAfter = manager.calculateStats(uuid)
        assertEquals(100.0, statsAfter.getStat(StatType.MINING_SPEED)) // Back to base
    }

    @Test
    fun `Source isolation - removing one skill does not affect others`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add Mining and Farming rewards
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.MINING, 10))
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.FARMING, 15))

        // Remove Mining rewards only
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)

        val stats = manager.calculateStats(uuid)

        // Mining should be back to base
        assertEquals(100.0, stats.getStat(StatType.MINING_SPEED))
        assertEquals(0.0, stats.getStat(StatType.MINING_FORTUNE))

        // Farming should be unchanged
        assertEquals(115.0, stats.getStat(StatType.FARMING_SPEED))
        assertEquals(15.0, stats.getStat(StatType.FARMING_FORTUNE))
    }

    @Test
    fun `Zero rewards after removal`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Add then remove
        manager.addModifiers(uuid, SkillRewardCalculator.calculateRewards(Skill.MINING, 10))
        manager.removeModifiersFromSource(uuid, StatSource.SKILL_MINING)

        val stats = manager.calculateStats(uuid)

        // Should have no Mining bonuses
        assertEquals(100.0, stats.getStat(StatType.MINING_SPEED))
        assertEquals(0.0, stats.getStat(StatType.MINING_FORTUNE))
    }
}
