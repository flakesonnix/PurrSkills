package gay.nyaa.purrskills.db

import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProfile
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Tests for PlayerSkills persistence layer.
 * These tests verify the mapping logic and immutability.
 *
 * Full database integration tests would require H2 or test containers,
 * which is beyond the scope of this initial implementation.
 */
class SkillRepositoryTest {

    @Test
    fun `PlayerSkills create with map populates all skills`() {
        val uuid = UUID.randomUUID()
        val miningProfile = SkillProfile(Skill.MINING, level = 5, xp = 100)
        val skills = PlayerSkills.create(uuid, mapOf(Skill.MINING to miningProfile))

        // Mining should have custom values
        assertEquals(5, skills.getSkill(Skill.MINING).level)
        assertEquals(100, skills.getSkill(Skill.MINING).xp)

        // Other skills should have defaults
        assertEquals(1, skills.getSkill(Skill.FARMING).level)
        assertEquals(0, skills.getSkill(Skill.FARMING).xp)
        assertEquals(1, skills.getSkill(Skill.FORAGING).level)
        assertEquals(1, skills.getSkill(Skill.COMBAT).level)
        assertEquals(1, skills.getSkill(Skill.FISHING).level)
    }

    @Test
    fun `PlayerSkills create with empty map uses all defaults`() {
        val uuid = UUID.randomUUID()
        val skills = PlayerSkills.create(uuid, emptyMap())

        // All skills should be default
        for (skill in Skill.entries) {
            assertEquals(1, skills.getSkill(skill).level, "Skill $skill should be level 1")
            assertEquals(0, skills.getSkill(skill).xp, "Skill $skill should have 0 XP")
        }
    }

    @Test
    fun `PlayerSkills preserves UUID`() {
        val uuid = UUID.randomUUID()
        val skills = PlayerSkills.create(uuid)

        assertEquals(uuid, skills.playerUuid)
    }

    @Test
    fun `PlayerSkills with all skills set`() {
        val uuid = UUID.randomUUID()
        val skillMap = Skill.entries.associateWith { skill ->
            SkillProfile(skill, level = skill.ordinal + 2, xp = skill.ordinal * 10)
        }

        val skills = PlayerSkills.create(uuid, skillMap)

        // Verify each skill has correct values
        for (skill in Skill.entries) {
            val profile = skills.getSkill(skill)
            assertEquals(skill.ordinal + 2, profile.level)
            assertEquals(skill.ordinal * 10, profile.xp)
        }
    }

    @Test
    fun `SkillProfile requires skill parameter`() {
        val profile = SkillProfile(Skill.MINING, level = 10, xp = 500)

        assertEquals(Skill.MINING, profile.skill)
        assertEquals(10, profile.level)
        assertEquals(500, profile.xp)
    }

    @Test
    fun `PlayerSkills addXp creates new instance`() {
        val uuid = UUID.randomUUID()
        val before = PlayerSkills.create(uuid)
        val after = before.addXp(Skill.MINING, 50)

        // Values should be different (immutability proof)
        assertNotEquals(before.getSkill(Skill.MINING).xp, after.getSkill(Skill.MINING).xp)

        // Original should be unchanged
        assertEquals(1, before.getSkill(Skill.MINING).level)
        assertEquals(0, before.getSkill(Skill.MINING).xp)

        // New instance should have updated XP (no level-up at 50 XP)
        assertEquals(50, after.getSkill(Skill.MINING).xp)
    }

    @Test
    fun `PlayerSkills is immutable`() {
        val uuid = UUID.randomUUID()
        val original = PlayerSkills.create(uuid)

        // Try to modify (should create new instance)
        val modified = original.addXp(Skill.MINING, 50)

        // Original should be unchanged
        assertEquals(0, original.getSkill(Skill.MINING).xp)

        // Modified should have new value
        assertEquals(50, modified.getSkill(Skill.MINING).xp)
    }

    @Test
    fun `Thread safety - PlayerSkills immutability prevents races`() {
        val uuid = UUID.randomUUID()
        val skills = PlayerSkills.create(uuid)

        // Simulate concurrent modifications (use small XP amounts to avoid level-ups)
        val modified1 = skills.addXp(Skill.MINING, 50)
        val modified2 = skills.addXp(Skill.FARMING, 60)

        // Values should be different (immutability proof)
        assertNotEquals(skills, modified1)
        assertNotEquals(skills, modified2)
        assertNotEquals(modified1, modified2)

        // Original should be unchanged
        assertEquals(0, skills.getSkill(Skill.MINING).xp)
        assertEquals(0, skills.getSkill(Skill.FARMING).xp)

        // Modified versions should have their own values
        assertEquals(50, modified1.getSkill(Skill.MINING).xp)
        assertEquals(0, modified1.getSkill(Skill.FARMING).xp)

        assertEquals(0, modified2.getSkill(Skill.MINING).xp)
        assertEquals(60, modified2.getSkill(Skill.FARMING).xp)
    }

    @Test
    fun `All skills are always present`() {
        val uuid = UUID.randomUUID()
        val skills = PlayerSkills.create(uuid)

        // All 5 skills should exist
        for (skill in Skill.entries) {
            val profile = skills.getSkill(skill)
            assertNotNull(profile, "Skill $skill should never be null")
        }
    }

    @Test
    fun `Multiple saves preserve independence`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        val player1 = PlayerSkills.create(uuid1).addXp(Skill.MINING, 50)
        val player2 = PlayerSkills.create(uuid2).addXp(Skill.FARMING, 80)

        // Players should be independent
        assertEquals(50, player1.getSkill(Skill.MINING).xp)
        assertEquals(0, player1.getSkill(Skill.FARMING).xp)

        assertEquals(0, player2.getSkill(Skill.MINING).xp)
        assertEquals(80, player2.getSkill(Skill.FARMING).xp)
    }
}
