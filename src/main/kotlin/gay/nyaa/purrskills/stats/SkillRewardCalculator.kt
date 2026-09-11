package gay.nyaa.purrskills.stats

import gay.nyaa.purrskills.skill.Skill

/**
 * Calculates stat modifiers granted by skill levels.
 *
 * Pure Kotlin - no Bukkit/Paper dependencies.
 * Deterministic - same skill/level always produces same modifiers.
 *
 * Rewards are calculated from current level, not accumulated per level-up.
 * This ensures idempotency: calling calculateRewards(MINING, 10) multiple
 * times always produces the same modifiers.
 *
 * Example:
 * ```
 * val rewards = SkillRewardCalculator.calculateRewards(Skill.MINING, 10)
 * // Always returns: [+10 Mining Speed, +5 Mining Fortune]
 * ```
 */
object SkillRewardCalculator {

    /**
     * Calculate all stat modifiers for a skill at a specific level.
     *
     * @param skill The skill
     * @param level The skill level (1+)
     * @return List of stat modifiers (empty if level < 1)
     */
    fun calculateRewards(skill: Skill, level: Int): List<StatModifier> {
        if (level < 1) return emptyList()

        return when (skill) {
            Skill.MINING -> calculateMiningRewards(level)
            Skill.FARMING -> calculateFarmingRewards(level)
            Skill.FORAGING -> calculateForagingRewards(level)
            Skill.COMBAT -> calculateCombatRewards(level)
            Skill.FISHING -> calculateFishingRewards(level)
        }
    }

    /**
     * Mining rewards:
     * - +1 Mining Speed per level
     * - +0.5 Mining Fortune per level
     *
     * Level 10: +10 Speed, +5 Fortune
     */
    private fun calculateMiningRewards(level: Int): List<StatModifier> {
        val speed = level * 1.0
        val fortune = level * 0.5

        return listOf(
            StatModifier.additive(StatType.MINING_SPEED, speed, StatSource.SKILL_MINING),
            StatModifier.additive(StatType.MINING_FORTUNE, fortune, StatSource.SKILL_MINING),
        )
    }

    /**
     * Farming rewards:
     * - +1 Farming Fortune per level
     * - +1 Farming Speed per level
     *
     * Level 10: +10 Fortune, +10 Speed
     */
    private fun calculateFarmingRewards(level: Int): List<StatModifier> {
        val fortune = level * 1.0
        val speed = level * 1.0

        return listOf(
            StatModifier.additive(StatType.FARMING_FORTUNE, fortune, StatSource.SKILL_FARMING),
            StatModifier.additive(StatType.FARMING_SPEED, speed, StatSource.SKILL_FARMING),
        )
    }

    /**
     * Foraging rewards:
     * - +1 Foraging Fortune per level
     * - +1 Foraging Speed per level
     *
     * Level 10: +10 Fortune, +10 Speed
     */
    private fun calculateForagingRewards(level: Int): List<StatModifier> {
        val fortune = level * 1.0
        val speed = level * 1.0

        return listOf(
            StatModifier.additive(StatType.FORAGING_FORTUNE, fortune, StatSource.SKILL_FORAGING),
            StatModifier.additive(StatType.FORAGING_SPEED, speed, StatSource.SKILL_FORAGING),
        )
    }

    /**
     * Combat rewards:
     * - +2 Health per level
     * - +1 Damage per level
     * - +1 Strength per level
     * - +0.5 Crit Damage per level
     *
     * Level 10: +20 Health, +10 Damage, +10 Strength, +5 Crit Damage
     */
    private fun calculateCombatRewards(level: Int): List<StatModifier> {
        val health = level * 2.0
        val damage = level * 1.0
        val strength = level * 1.0
        val critDamage = level * 0.5

        return listOf(
            StatModifier.additive(StatType.HEALTH, health, StatSource.SKILL_COMBAT),
            StatModifier.additive(StatType.DAMAGE, damage, StatSource.SKILL_COMBAT),
            StatModifier.additive(StatType.STRENGTH, strength, StatSource.SKILL_COMBAT),
            StatModifier.additive(StatType.CRIT_DAMAGE, critDamage, StatSource.SKILL_COMBAT),
        )
    }

    /**
     * Fishing rewards:
     * - +1 Fishing Speed per level
     * - +0.5 Sea Creature Chance per level
     *
     * Level 10: +10 Speed, +5 Sea Creature Chance
     */
    private fun calculateFishingRewards(level: Int): List<StatModifier> {
        val speed = level * 1.0
        val seaCreatureChance = level * 0.5

        return listOf(
            StatModifier.additive(StatType.FISHING_SPEED, speed, StatSource.SKILL_FISHING),
            StatModifier.additive(StatType.SEA_CREATURE_CHANCE, seaCreatureChance, StatSource.SKILL_FISHING),
        )
    }
}
