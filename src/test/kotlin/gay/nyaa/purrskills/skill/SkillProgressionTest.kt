package gay.nyaa.purrskills.skill

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for SkillProgression - pure Kotlin, no Bukkit mocking needed.
 * Tests the Hypixel SkyBlock-style progression model.
 */
class SkillProgressionTest {

    @Test
    fun `level 1 requires 100 XP to reach level 2`() {
        assertEquals(100, SkillProgression.xpForNextLevel(1))
    }

    @Test
    fun `level 2 requires 150 XP to reach level 3`() {
        assertEquals(150, SkillProgression.xpForNextLevel(2))
    }

    @Test
    fun `level 20 requires 13000 XP to reach level 21`() {
        assertEquals(13_000, SkillProgression.xpForNextLevel(20))
    }

    @Test
    fun `xpRequiredForLevel calculates cumulative XP correctly`() {
        // Level 1 = 0 XP (starting point)
        assertEquals(0, SkillProgression.xpRequiredForLevel(1))

        // Level 2 = 100 XP
        assertEquals(100, SkillProgression.xpRequiredForLevel(2))

        // Level 5 = 100 + 150 + 250 + 400 = 900 XP
        assertEquals(900, SkillProgression.xpRequiredForLevel(5))
    }

    @Test
    fun `addXp simple case - no level up`() {
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 50)
        val updated = SkillProgression.addXp(profile, 30)

        assertEquals(1, updated.level)
        assertEquals(80, updated.xp)
    }

    @Test
    fun `addXp exactly reaches next level`() {
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 50)
        val updated = SkillProgression.addXp(profile, 50)

        assertEquals(2, updated.level)
        assertEquals(0, updated.xp)
    }

    @Test
    fun `addXp with excess XP preserved`() {
        // Level 4 with 350/400 XP, add 200 XP
        // → Level 5 with 150/600 XP
        val profile = SkillProfile(Skill.MINING, level = 4, xp = 350)
        val updated = SkillProgression.addXp(profile, 200)

        assertEquals(5, updated.level)
        assertEquals(150, updated.xp)
    }

    @Test
    fun `addXp multiple level-ups at once`() {
        // Level 1 with 0 XP, add 500 XP
        // Lvl 1→2: 100 (400 left)
        // Lvl 2→3: 150 (250 left)
        // Lvl 3→4: 250 (0 left)
        // → Level 4 with 0/400 XP
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 0)
        val updated = SkillProgression.addXp(profile, 500)

        assertEquals(4, updated.level)
        assertEquals(0, updated.xp)
    }

    @Test
    fun `addXp multiple level-ups with complex XP overflow`() {
        // Level 1, add 1000 XP
        // Lvl 1→2: 100 (900 left)
        // Lvl 2→3: 150 (750 left)
        // Lvl 3→4: 250 (500 left)
        // Lvl 4→5: 400 (100 left)
        // Result: Level 5 with 100/600 XP
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 0)
        val updated = SkillProgression.addXp(profile, 1000)

        assertEquals(5, updated.level)
        assertEquals(100, updated.xp)
    }

    @Test
    fun `addXp preserves excess XP across multiple levels`() {
        // Level 10 with 2000/2400 XP, add 5000 XP
        // Remaining: 2000 + 5000 = 7000
        // Lvl 10→11: 2400 (4600 left)
        // Lvl 11→12: 3000 (1600 left)
        // Result: Level 12 with 1600/3700 XP
        val profile = SkillProfile(Skill.MINING, level = 10, xp = 2000)
        val updated = SkillProgression.addXp(profile, 5000)

        assertEquals(12, updated.level)
        assertEquals(1600, updated.xp)
    }

    @Test
    fun `addXp zero amount returns same profile`() {
        val profile = SkillProfile(Skill.MINING, level = 5, xp = 200)
        val updated = SkillProgression.addXp(profile, 0)

        assertEquals(profile, updated)
    }

    @Test
    fun `progressPercentage calculates correctly`() {
        // Level 1 with 50/100 XP = 50%
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 50)
        assertEquals(0.5, SkillProgression.progressPercentage(profile), 0.01)
    }

    @Test
    fun `progressPercentage at 0 XP is 0 percent`() {
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 0)
        assertEquals(0.0, SkillProgression.progressPercentage(profile), 0.01)
    }

    @Test
    fun `progressPercentage at max XP is 100 percent`() {
        val profile = SkillProfile(Skill.MINING, level = 1, xp = 99)
        assertTrue(SkillProgression.progressPercentage(profile) < 1.0)

        val maxed = SkillProfile(Skill.MINING, level = 1, xp = 100)
        // Should trigger level-up, but if we manually test
        assertEquals(1.0, SkillProgression.progressPercentage(maxed), 0.01)
    }

    @Test
    fun `calculateLevel from total XP`() {
        // 0 XP = Level 1, 0 remaining
        assertEquals(Pair(1, 0), SkillProgression.calculateLevel(0))

        // 100 XP = Level 2, 0 remaining
        assertEquals(Pair(2, 0), SkillProgression.calculateLevel(100))

        // 150 XP = Level 2, 50 remaining
        assertEquals(Pair(2, 50), SkillProgression.calculateLevel(150))

        // 900 XP = Level 5, 0 remaining
        assertEquals(Pair(5, 0), SkillProgression.calculateLevel(900))

        // 1000 XP = Level 5, 100 remaining
        assertEquals(Pair(5, 100), SkillProgression.calculateLevel(1000))
    }
}
