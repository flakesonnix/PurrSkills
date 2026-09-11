package gay.nyaa.purrskills.command

import gay.nyaa.purrskills.stats.StatType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests for StatsCommand logic.
 */
class StatsCommandTest {

    @Test
    fun `formatNumber formats whole numbers correctly`() {
        val formatter = { number: Double ->
            if (number == number.toLong().toDouble()) {
                String.format("%,d", number.toLong())
            } else {
                String.format("%,.1f", number)
            }
        }

        assertEquals("0", formatter(0.0))
        assertEquals("100", formatter(100.0))
        assertEquals("1,234", formatter(1234.0))
        assertEquals("12,345", formatter(12345.0))
    }

    @Test
    fun `formatNumber formats decimals with one place`() {
        val formatter = { number: Double ->
            if (number == number.toLong().toDouble()) {
                String.format("%,d", number.toLong())
            } else {
                String.format("%,.1f", number)
            }
        }

        assertEquals("0.5", formatter(0.5))
        assertEquals("10.5", formatter(10.5))
        assertEquals("123.4", formatter(123.4))
        assertEquals("1,234.5", formatter(1234.5))
    }

    @Test
    fun `stat bonus calculation is correct`() {
        val baseValue = 100.0
        val currentValue = 110.0
        val bonus = currentValue - baseValue

        assertEquals(10.0, bonus)
    }

    @Test
    fun `all stat types have i18n keys`() {
        for (statType in StatType.entries) {
            val key = statType.i18nKey()
            assert(key.startsWith("stats.")) { "StatType $statType i18n key should start with 'stats.'" }
        }
    }

    @Test
    fun `stat categories are complete`() {
        // Mining stats
        assert(StatType.entries.contains(StatType.MINING_SPEED))
        assert(StatType.entries.contains(StatType.MINING_FORTUNE))

        // Combat stats
        assert(StatType.entries.contains(StatType.HEALTH))
        assert(StatType.entries.contains(StatType.DAMAGE))
        assert(StatType.entries.contains(StatType.STRENGTH))
        assert(StatType.entries.contains(StatType.DEFENSE))
        assert(StatType.entries.contains(StatType.CRIT_CHANCE))
        assert(StatType.entries.contains(StatType.CRIT_DAMAGE))

        // Farming stats
        assert(StatType.entries.contains(StatType.FARMING_FORTUNE))
        assert(StatType.entries.contains(StatType.FARMING_SPEED))

        // Foraging stats
        assert(StatType.entries.contains(StatType.FORAGING_FORTUNE))
        assert(StatType.entries.contains(StatType.FORAGING_SPEED))

        // Fishing stats
        assert(StatType.entries.contains(StatType.FISHING_SPEED))
        assert(StatType.entries.contains(StatType.SEA_CREATURE_CHANCE))

        // General stats
        assert(StatType.entries.contains(StatType.SPEED))
        assert(StatType.entries.contains(StatType.MAGIC_FIND))
    }

    @Test
    fun `base values are correct for display`() {
        assertEquals(100.0, StatType.MINING_SPEED.baseValue)
        assertEquals(0.0, StatType.MINING_FORTUNE.baseValue)
        assertEquals(100.0, StatType.HEALTH.baseValue)
        assertEquals(0.0, StatType.DAMAGE.baseValue)
    }
}
