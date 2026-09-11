package gay.nyaa.purrskills.stats

import gay.nyaa.purrskills.skill.Skill
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for SkillRewardCalculator.
 * Validates reward formulas and idempotency.
 */
class SkillRewardCalculatorTest {

    @Test
    fun `Mining Level 1 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 1)

        assertEquals(2, rewards.size)

        val speed = rewards.find { it.stat == StatType.MINING_SPEED }!!
        assertEquals(1.0, speed.value)
        assertEquals(StatSource.SKILL_MINING, speed.source)

        val fortune = rewards.find { it.stat == StatType.MINING_FORTUNE }!!
        assertEquals(0.5, fortune.value)
        assertEquals(StatSource.SKILL_MINING, fortune.source)
    }

    @Test
    fun `Mining Level 10 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)

        assertEquals(2, rewards.size)

        val speed = rewards.find { it.stat == StatType.MINING_SPEED }!!
        assertEquals(10.0, speed.value)

        val fortune = rewards.find { it.stat == StatType.MINING_FORTUNE }!!
        assertEquals(5.0, fortune.value)
    }

    @Test
    fun `Farming Level 10 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.FARMING, 10)

        assertEquals(2, rewards.size)

        val fortune = rewards.find { it.stat == StatType.FARMING_FORTUNE }!!
        assertEquals(10.0, fortune.value)
        assertEquals(StatSource.SKILL_FARMING, fortune.source)

        val speed = rewards.find { it.stat == StatType.FARMING_SPEED }!!
        assertEquals(10.0, speed.value)
    }

    @Test
    fun `Foraging Level 10 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.FORAGING, 10)

        assertEquals(2, rewards.size)

        val fortune = rewards.find { it.stat == StatType.FORAGING_FORTUNE }!!
        assertEquals(10.0, fortune.value)
        assertEquals(StatSource.SKILL_FORAGING, fortune.source)

        val speed = rewards.find { it.stat == StatType.FORAGING_SPEED }!!
        assertEquals(10.0, speed.value)
    }

    @Test
    fun `Combat Level 10 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.COMBAT, 10)

        assertEquals(4, rewards.size)

        val health = rewards.find { it.stat == StatType.HEALTH }!!
        assertEquals(20.0, health.value)
        assertEquals(StatSource.SKILL_COMBAT, health.source)

        val damage = rewards.find { it.stat == StatType.DAMAGE }!!
        assertEquals(10.0, damage.value)

        val strength = rewards.find { it.stat == StatType.STRENGTH }!!
        assertEquals(10.0, strength.value)

        val critDamage = rewards.find { it.stat == StatType.CRIT_DAMAGE }!!
        assertEquals(5.0, critDamage.value)
    }

    @Test
    fun `Fishing Level 10 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.FISHING, 10)

        assertEquals(2, rewards.size)

        val speed = rewards.find { it.stat == StatType.FISHING_SPEED }!!
        assertEquals(10.0, speed.value)
        assertEquals(StatSource.SKILL_FISHING, speed.source)

        val seaCreatureChance = rewards.find { it.stat == StatType.SEA_CREATURE_CHANCE }!!
        assertEquals(5.0, seaCreatureChance.value)
    }

    @Test
    fun `Level 0 returns empty rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 0)
        assertTrue(rewards.isEmpty())
    }

    @Test
    fun `Negative level returns empty rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, -1)
        assertTrue(rewards.isEmpty())
    }

    @Test
    fun `All skills have rewards at level 1`() {
        for (skill in Skill.entries) {
            val rewards = SkillRewardCalculator.calculateRewards(skill, 1)
            assertTrue(rewards.isNotEmpty(), "Skill $skill should have rewards at level 1")
        }
    }

    @Test
    fun `All rewards use additive modifiers`() {
        for (skill in Skill.entries) {
            val rewards = SkillRewardCalculator.calculateRewards(skill, 10)
            for (reward in rewards) {
                assertEquals(
                    StatModifier.ModifierType.ADDITIVE,
                    reward.type,
                    "Skill $skill reward should be additive",
                )
            }
        }
    }

    @Test
    fun `Mining rewards use SKILL_MINING source`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 5)
        for (reward in rewards) {
            assertEquals(StatSource.SKILL_MINING, reward.source)
        }
    }

    @Test
    fun `Idempotency - same level produces same rewards`() {
        val rewards1 = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        val rewards2 = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        val rewards3 = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)

        assertEquals(rewards1, rewards2)
        assertEquals(rewards2, rewards3)
    }

    @Test
    fun `Progressive scaling - higher levels grant more rewards`() {
        val level1 = SkillRewardCalculator.calculateRewards(Skill.MINING, 1)
        val level10 = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
        val level50 = SkillRewardCalculator.calculateRewards(Skill.MINING, 50)

        val speed1 = level1.find { it.stat == StatType.MINING_SPEED }!!.value
        val speed10 = level10.find { it.stat == StatType.MINING_SPEED }!!.value
        val speed50 = level50.find { it.stat == StatType.MINING_SPEED }!!.value

        assertTrue(speed1 < speed10)
        assertTrue(speed10 < speed50)
    }

    @Test
    fun `Combat Level 20 grants correct rewards`() {
        val rewards = SkillRewardCalculator.calculateRewards(Skill.COMBAT, 20)

        val health = rewards.find { it.stat == StatType.HEALTH }!!
        assertEquals(40.0, health.value) // 20 × 2

        val damage = rewards.find { it.stat == StatType.DAMAGE }!!
        assertEquals(20.0, damage.value) // 20 × 1

        val strength = rewards.find { it.stat == StatType.STRENGTH }!!
        assertEquals(20.0, strength.value) // 20 × 1

        val critDamage = rewards.find { it.stat == StatType.CRIT_DAMAGE }!!
        assertEquals(10.0, critDamage.value) // 20 × 0.5
    }
}
