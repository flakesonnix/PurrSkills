package gay.nyaa.purrskills.skill

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test

/**
 * Tests for PlayerSkills - Hypixel SkyBlock-style multi-skill management.
 * Ensures each player can have independent progress in multiple skills.
 */
class PlayerSkillsTest {

    private val testUuid = UUID.randomUUID()

    @Test
    fun `create new PlayerSkills starts all skills at level 1`() {
        val playerSkills = PlayerSkills.create(testUuid)

        assertEquals(testUuid, playerSkills.playerUuid)

        // All skills should be level 1 with 0 XP
        for (skill in Skill.entries) {
            val profile = playerSkills.getSkill(skill)
            assertEquals(1, profile.level)
            assertEquals(0, profile.xp)
            assertEquals(skill, profile.skill)
        }
    }

    @Test
    fun `addXp to one skill does not affect other skills`() {
        val playerSkills = PlayerSkills.create(testUuid)

        // Add XP to Mining only
        val updated = playerSkills.addXp(Skill.MINING, 200)

        // Mining should have gained XP
        val mining = updated.getSkill(Skill.MINING)
        assertEquals(2, mining.level)
        assertEquals(100, mining.xp)

        // Other skills should remain at level 1, 0 XP
        assertEquals(1, updated.getSkill(Skill.FARMING).level)
        assertEquals(1, updated.getSkill(Skill.COMBAT).level)
        assertEquals(1, updated.getSkill(Skill.FISHING).level)
        assertEquals(1, updated.getSkill(Skill.FORAGING).level)
    }

    @Test
    fun `addXp multiple times to same skill accumulates correctly`() {
        var playerSkills = PlayerSkills.create(testUuid)

        // Add 50 XP three times = 150 XP total
        playerSkills = playerSkills.addXp(Skill.MINING, 50)
        playerSkills = playerSkills.addXp(Skill.MINING, 50)
        playerSkills = playerSkills.addXp(Skill.MINING, 50)

        val mining = playerSkills.getSkill(Skill.MINING)
        assertEquals(2, mining.level) // Leveled up from 100 XP
        assertEquals(50, mining.xp) // 50 remaining
    }

    @Test
    fun `addXp to multiple different skills independently`() {
        var playerSkills = PlayerSkills.create(testUuid)

        // Add XP to different skills
        playerSkills = playerSkills.addXp(Skill.MINING, 200) // Mining → Lvl 2 (100/150)
        playerSkills = playerSkills.addXp(Skill.COMBAT, 500) // Combat → Lvl 4 (0/400)
        playerSkills = playerSkills.addXp(Skill.FARMING, 50) // Farming → Lvl 1 (50/100)

        assertEquals(2, playerSkills.getSkill(Skill.MINING).level)
        assertEquals(100, playerSkills.getSkill(Skill.MINING).xp)

        assertEquals(4, playerSkills.getSkill(Skill.COMBAT).level)
        assertEquals(0, playerSkills.getSkill(Skill.COMBAT).xp)

        assertEquals(1, playerSkills.getSkill(Skill.FARMING).level)
        assertEquals(50, playerSkills.getSkill(Skill.FARMING).xp)

        // Untouched skills remain at level 1, 0 XP
        assertEquals(1, playerSkills.getSkill(Skill.FISHING).level)
        assertEquals(0, playerSkills.getSkill(Skill.FISHING).xp)
    }

    @Test
    fun `powerLevel sums all skill levels`() {
        var playerSkills = PlayerSkills.create(testUuid)

        // All skills start at level 1 → power level = 5
        assertEquals(5, playerSkills.powerLevel())

        // Level up Mining to 3
        playerSkills = playerSkills.addXp(Skill.MINING, 250) // Lvl 1→2→3
        assertEquals(7, playerSkills.powerLevel()) // 3+1+1+1+1

        // Level up Combat to 2
        playerSkills = playerSkills.addXp(Skill.COMBAT, 100)
        assertEquals(8, playerSkills.powerLevel()) // 3+1+1+2+1
    }

    @Test
    fun `immutability - addXp returns new instance`() {
        val original = PlayerSkills.create(testUuid)
        val updated = original.addXp(Skill.MINING, 100)

        // Original should not be modified
        assertEquals(1, original.getSkill(Skill.MINING).level)
        assertEquals(0, original.getSkill(Skill.MINING).xp)

        // Updated should have changes
        assertEquals(2, updated.getSkill(Skill.MINING).level)
        assertEquals(0, updated.getSkill(Skill.MINING).xp)

        // Should be different instances
        assertNotSame(original, updated)
    }

    @Test
    fun `getSkill returns default level 1 profile for missing skill`() {
        val emptyPlayerSkills = PlayerSkills(testUuid, emptyMap())

        val mining = emptyPlayerSkills.getSkill(Skill.MINING)
        assertEquals(Skill.MINING, mining.skill)
        assertEquals(1, mining.level)
        assertEquals(0, mining.xp)
    }

    @Test
    fun `setSkill updates specific skill profile`() {
        val playerSkills = PlayerSkills.create(testUuid)

        val customProfile = SkillProfile(Skill.MINING, level = 10, xp = 1500)
        val updated = playerSkills.setSkill(customProfile)

        assertEquals(10, updated.getSkill(Skill.MINING).level)
        assertEquals(1500, updated.getSkill(Skill.MINING).xp)

        // Other skills unchanged
        assertEquals(1, updated.getSkill(Skill.FARMING).level)
    }
}
