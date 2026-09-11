package gay.nyaa.purrskills.stats

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests for the stats system.
 * Validates stat calculations, modifier stacking, and source management.
 */
class StatsTest {

    @Test
    fun `StatType has correct base values`() {
        assertEquals(100.0, StatType.MINING_SPEED.baseValue)
        assertEquals(0.0, StatType.MINING_FORTUNE.baseValue)
        assertEquals(100.0, StatType.HEALTH.baseValue)
        assertEquals(0.0, StatType.DAMAGE.baseValue)
        assertEquals(50.0, StatType.CRIT_DAMAGE.baseValue)
    }

    @Test
    fun `StatType i18nKey formats correctly`() {
        assertEquals("stats.mining-speed", StatType.MINING_SPEED.i18nKey())
        assertEquals("stats.health", StatType.HEALTH.i18nKey())
        assertEquals("stats.crit-chance", StatType.CRIT_CHANCE.i18nKey())
    }

    @Test
    fun `StatModifier additive factory creates correct modifier`() {
        val mod = StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT)

        assertEquals(StatType.HEALTH, mod.stat)
        assertEquals(50.0, mod.value)
        assertEquals(StatModifier.ModifierType.ADDITIVE, mod.type)
        assertEquals(StatSource.SKILL_COMBAT, mod.source)
    }

    @Test
    fun `StatModifier multiplicative factory creates correct modifier`() {
        val mod = StatModifier.multiplicative(StatType.DAMAGE, 0.1, StatSource.BUFF_POTION)

        assertEquals(StatType.DAMAGE, mod.stat)
        assertEquals(0.1, mod.value)
        assertEquals(StatModifier.ModifierType.MULTIPLICATIVE, mod.type)
        assertEquals(StatSource.BUFF_POTION, mod.source)
    }

    @Test
    fun `PlayerStats empty returns base values`() {
        val stats = PlayerStats.empty()

        assertEquals(100.0, stats.getStat(StatType.HEALTH))
        assertEquals(0.0, stats.getStat(StatType.DAMAGE))
        assertEquals(100.0, stats.getStat(StatType.MINING_SPEED))
    }

    @Test
    fun `PlayerStats withStat updates value`() {
        val stats = PlayerStats.empty()
            .withStat(StatType.HEALTH, 150.0)

        assertEquals(150.0, stats.getStat(StatType.HEALTH))
        assertEquals(0.0, stats.getStat(StatType.DAMAGE)) // Other stats unchanged
    }

    @Test
    fun `PlayerStats hasStat returns true only for set stats`() {
        val stats = PlayerStats.empty()
            .withStat(StatType.HEALTH, 150.0)

        assertTrue(stats.hasStat(StatType.HEALTH))
        assertFalse(stats.hasStat(StatType.DAMAGE))
    }

    @Test
    fun `StatsManager addModifier stores modifier`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()
        val mod = StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT)

        manager.addModifier(uuid, mod)

        val modifiers = manager.getModifiers(uuid)
        assertEquals(1, modifiers.size)
        assertEquals(mod, modifiers[0])
    }

    @Test
    fun `StatsManager addModifiers stores multiple modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()
        val mods = listOf(
            StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT),
            StatModifier.additive(StatType.DAMAGE, 10.0, StatSource.SKILL_COMBAT),
        )

        manager.addModifiers(uuid, mods)

        val modifiers = manager.getModifiers(uuid)
        assertEquals(2, modifiers.size)
    }

    @Test
    fun `StatsManager removeModifiersFromSource removes only specified source`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        manager.addModifier(uuid, StatModifier.additive(StatType.DAMAGE, 10.0, StatSource.ITEM_HELMET))

        manager.removeModifiersFromSource(uuid, StatSource.SKILL_COMBAT)

        val modifiers = manager.getModifiers(uuid)
        assertEquals(1, modifiers.size)
        assertEquals(StatSource.ITEM_HELMET, modifiers[0].source)
    }

    @Test
    fun `StatsManager clearModifiers removes all modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        manager.addModifier(uuid, StatModifier.additive(StatType.DAMAGE, 10.0, StatSource.ITEM_HELMET))

        manager.clearModifiers(uuid)

        assertEquals(0, manager.getModifiers(uuid).size)
    }

    @Test
    fun `StatsManager calculateStats applies single additive modifier`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))

        val stats = manager.calculateStats(uuid)

        // Base 100 + 50 = 150
        assertEquals(150.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats stacks multiple additive modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 30.0, StatSource.ITEM_HELMET))

        val stats = manager.calculateStats(uuid)

        // Base 100 + 50 + 30 = 180
        assertEquals(180.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats applies multiplicative modifier`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.multiplicative(StatType.HEALTH, 0.5, StatSource.BUFF_POTION))

        val stats = manager.calculateStats(uuid)

        // Base 100 × (1 + 0.5) = 150
        assertEquals(150.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats applies additive before multiplicative`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        manager.addModifier(uuid, StatModifier.multiplicative(StatType.HEALTH, 0.2, StatSource.BUFF_POTION))

        val stats = manager.calculateStats(uuid)

        // (Base 100 + 50) × (1 + 0.2) = 150 × 1.2 = 180
        assertEquals(180.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats stacks multiplicative modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        manager.addModifier(uuid, StatModifier.multiplicative(StatType.HEALTH, 0.2, StatSource.BUFF_POTION))
        manager.addModifier(uuid, StatModifier.multiplicative(StatType.HEALTH, 0.3, StatSource.BUFF_EFFECT))

        val stats = manager.calculateStats(uuid)

        // Base 100 × (1 + 0.2 + 0.3) = 100 × 1.5 = 150
        assertEquals(150.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats handles complex scenario`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        // Mining Speed: 100 base
        manager.addModifier(uuid, StatModifier.additive(StatType.MINING_SPEED, 20.0, StatSource.SKILL_MINING))
        manager.addModifier(uuid, StatModifier.additive(StatType.MINING_SPEED, 10.0, StatSource.ITEM_MAIN_HAND))
        manager.addModifier(uuid, StatModifier.multiplicative(StatType.MINING_SPEED, 0.15, StatSource.BUFF_POTION))

        // Health: 100 base
        manager.addModifier(uuid, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))

        val stats = manager.calculateStats(uuid)

        // Mining Speed: (100 + 20 + 10) × (1 + 0.15) = 130 × 1.15 = 149.5
        assertEquals(149.5, stats.getStat(StatType.MINING_SPEED))

        // Health: 100 + 50 = 150
        assertEquals(150.0, stats.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager calculateStats returns empty for no modifiers`() {
        val manager = StatsManager()
        val uuid = UUID.randomUUID()

        val stats = manager.calculateStats(uuid)

        // No modifiers, so all stats are at base values
        assertEquals(100.0, stats.getStat(StatType.HEALTH))
        assertEquals(0.0, stats.getStat(StatType.DAMAGE))
    }

    @Test
    fun `StatsManager handles multiple players independently`() {
        val manager = StatsManager()
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        manager.addModifier(uuid1, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        manager.addModifier(uuid2, StatModifier.additive(StatType.HEALTH, 100.0, StatSource.SKILL_COMBAT))

        val stats1 = manager.calculateStats(uuid1)
        val stats2 = manager.calculateStats(uuid2)

        assertEquals(150.0, stats1.getStat(StatType.HEALTH))
        assertEquals(200.0, stats2.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatsManager getCachedPlayerCount returns correct count`() {
        val manager = StatsManager()
        assertEquals(0, manager.getCachedPlayerCount())

        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        manager.addModifier(uuid1, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        assertEquals(1, manager.getCachedPlayerCount())

        manager.addModifier(uuid2, StatModifier.additive(StatType.HEALTH, 50.0, StatSource.SKILL_COMBAT))
        assertEquals(2, manager.getCachedPlayerCount())

        manager.clearModifiers(uuid1)
        assertEquals(1, manager.getCachedPlayerCount())
    }

    @Test
    fun `PlayerStats is immutable`() {
        val original = PlayerStats.empty()
        val modified = original.withStat(StatType.HEALTH, 150.0)

        // Original should be unchanged
        assertEquals(100.0, original.getStat(StatType.HEALTH))

        // Modified should have new value
        assertEquals(150.0, modified.getStat(StatType.HEALTH))
    }

    @Test
    fun `StatModifier requires finite values`() {
        try {
            StatModifier.additive(StatType.HEALTH, Double.NaN, StatSource.SKILL_COMBAT)
            assert(false) { "Should throw exception for NaN" }
        } catch (e: IllegalArgumentException) {
            // Expected
        }

        try {
            StatModifier.additive(StatType.HEALTH, Double.POSITIVE_INFINITY, StatSource.SKILL_COMBAT)
            assert(false) { "Should throw exception for infinity" }
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}
