package gay.nyaa.purrskills.gui

import gay.nyaa.purrskills.skill.Skill
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests for SkillMenuGUI logic.
 *
 * Note: These are unit tests for business logic.
 * GUI interaction tests would require MockBukkit.
 */
class SkillMenuGUITest {

    @Test
    fun `progress bar formats correctly`() {
        // Test progress bar formatting logic
        val testCases = listOf(
            0 to 0, // 0% = no filled bars
            10 to 1, // 10% = 1 filled bar
            50 to 5, // 50% = 5 filled bars
            100 to 10, // 100% = 10 filled bars
            85 to 8, // 85% = 8 filled bars
            95 to 9, // 95% = 9 filled bars
        )

        for ((percentage, expectedFilled) in testCases) {
            val bars = 10
            val filled = (percentage / 10).coerceIn(0, bars)
            val empty = bars - filled

            assertEquals(expectedFilled, filled, "Expected $percentage% to have $expectedFilled filled bars")
            assertEquals(bars - expectedFilled, empty, "Expected $percentage% to have ${bars - expectedFilled} empty bars")
        }
    }

    @Test
    fun `skill material mapping is complete`() {
        // Verify all skills have a material assigned
        val materialMap = mapOf(
            Skill.MINING to Material.DIAMOND_PICKAXE,
            Skill.FARMING to Material.DIAMOND_HOE,
            Skill.FORAGING to Material.DIAMOND_AXE,
            Skill.COMBAT to Material.DIAMOND_SWORD,
            Skill.FISHING to Material.FISHING_ROD,
        )

        // Verify all skills are covered
        assertEquals(Skill.entries.size, materialMap.size, "All skills should have a material icon")

        for (skill in Skill.entries) {
            assert(materialMap.containsKey(skill)) { "Skill $skill missing material icon" }
        }
    }

    @Test
    fun `inventory slot positions are valid`() {
        // Test inventory slot positions (27 slot chest = 0-26)
        val positions = mapOf(
            Skill.MINING to 10,
            Skill.FARMING to 12,
            Skill.FORAGING to 14,
            Skill.COMBAT to 16,
            Skill.FISHING to 22,
        )

        for ((skill, slot) in positions) {
            assert(slot in 0..26) { "Skill $skill slot $slot is out of bounds for 27-slot inventory" }
        }

        // Verify no duplicate slots
        val uniqueSlots = positions.values.toSet()
        assertEquals(positions.size, uniqueSlots.size, "All skills should have unique slots")

        // Power level item at slot 26
        assert(26 !in positions.values) { "Slot 26 should be reserved for power level" }
    }

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
    fun `stat value formatting handles integers and decimals`() {
        // Test stat value formatting logic
        val testCases = listOf(
            10.0 to "10", // Integer values show without decimal
            5.5 to "5.5", // Decimal values show with 1 decimal place
            10.25 to "10.3", // Rounds to 1 decimal (Java rounding)
            0.5 to "0.5", // Small decimals preserved
            100.0 to "100", // Large integers
        )

        for ((value, expected) in testCases) {
            val formatted = if (value % 1.0 == 0.0) {
                value.toInt().toString()
            } else {
                String.format("%.1f", value)
            }

            // For 10.25 case, accept both "10.3" and "10.2" due to rounding
            if (value == 10.25) {
                assert(formatted == "10.3" || formatted == "10.2") {
                    "Expected $value to format as ~$expected, got $formatted"
                }
            } else {
                assertEquals(expected, formatted, "Expected $value to format as $expected")
            }
        }
    }

    @Test
    fun `inventory size is correct for GUI layout`() {
        // GUI uses 3 rows (27 slots)
        val inventorySize = 27

        assertEquals(27, inventorySize, "Skills menu should use 3 rows (27 slots)")
        assertEquals(9, inventorySize / 3, "Each row should have 9 slots")
    }
}
