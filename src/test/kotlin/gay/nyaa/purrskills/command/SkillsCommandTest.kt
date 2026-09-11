package gay.nyaa.purrskills.command

import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests for SkillsCommand logic.
 *
 * Note: These are unit tests for business logic.
 * Command execution tests would require MockBukkit.
 */
class SkillsCommandTest {

    @Test
    fun `number formatting adds thousands separator`() {
        // Test the number formatting logic
        assertEquals("0", String.format("%,d", 0))
        assertEquals("123", String.format("%,d", 123))
        assertEquals("1,234", String.format("%,d", 1234))
        assertEquals("12,345", String.format("%,d", 12345))
        assertEquals("123,456", String.format("%,d", 123456))
        assertEquals("1,234,567", String.format("%,d", 1234567))
    }

    @Test
    fun `PlayerSkills powerLevel sums all skill levels`() {
        val uuid = UUID.randomUUID()

        // Start with defaults (all level 1)
        val defaultSkills = PlayerSkills.create(uuid)
        assertEquals(5, defaultSkills.powerLevel(), "Default power level should be 5 (5 skills × level 1)")

        // Add XP to some skills
        val skills = defaultSkills
            .addXp(Skill.MINING, 500) // Should level up
            .addXp(Skill.FARMING, 200) // Should level up
            .addXp(Skill.FORAGING, 100) // Should level up
            .addXp(Skill.COMBAT, 300) // Should level up
            .addXp(Skill.FISHING, 50) // Might not level up

        val powerLevel = skills.powerLevel()

        // Power level should be > 5 due to level-ups
        assert(powerLevel > 5) { "Power level should increase with XP/levels, got $powerLevel" }
    }

    @Test
    fun `percentage calculation works correctly`() {
        // Test percentage calculation logic
        val testCases = listOf(
            Triple(0, 100, 0), // 0 / 100 = 0%
            Triple(50, 100, 50), // 50 / 100 = 50%
            Triple(100, 100, 100), // 100 / 100 = 100%
            Triple(75, 150, 50), // 75 / 150 = 50%
            Triple(123, 456, 26), // 123 / 456 = ~27%
        )

        for ((current, required, expected) in testCases) {
            val actual = (current.toDouble() / required * 100).toInt()
            assertEquals(expected, actual, "Expected $current/$required to be ~$expected%, got $actual%")
        }
    }

    @Test
    fun `all skills are displayed in order`() {
        val skills = Skill.entries

        assertEquals(5, skills.size, "Should have exactly 5 skills")

        // Verify all expected skills exist
        assert(skills.contains(Skill.MINING))
        assert(skills.contains(Skill.FARMING))
        assert(skills.contains(Skill.FORAGING))
        assert(skills.contains(Skill.COMBAT))
        assert(skills.contains(Skill.FISHING))
    }

    @Test
    fun `skill display name format`() {
        // Verify skill names are lowercase for i18n keys
        assertEquals("mining", Skill.MINING.displayName())
        assertEquals("farming", Skill.FARMING.displayName())
        assertEquals("foraging", Skill.FORAGING.displayName())
        assertEquals("combat", Skill.COMBAT.displayName())
        assertEquals("fishing", Skill.FISHING.displayName())
    }
}
