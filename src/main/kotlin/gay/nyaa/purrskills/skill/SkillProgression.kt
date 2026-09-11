package gay.nyaa.purrskills.skill

/**
 * Pure Kotlin progression logic - no Bukkit dependencies.
 * Handles XP accumulation, level-ups, and progression curve.
 *
 * This class is testable without running a Minecraft server.
 */
object SkillProgression {

    /**
     * XP required to advance from each level to the next.
     * Index 0 = Level 1 → 2 (100 XP)
     * Index 1 = Level 2 → 3 (150 XP)
     * etc.
     */
    private val XP_CURVE = intArrayOf(
        100, // Level 1 → 2
        150, // Level 2 → 3
        250, // Level 3 → 4
        400, // Level 4 → 5
        600, // Level 5 → 6
        850, // Level 6 → 7
        1_150, // Level 7 → 8
        1_500, // Level 8 → 9
        1_900, // Level 9 → 10
        2_400, // Level 10 → 11
        3_000, // Level 11 → 12
        3_700, // Level 12 → 13
        4_500, // Level 13 → 14
        5_400, // Level 14 → 15
        6_400, // Level 15 → 16
        7_500, // Level 16 → 17
        8_700, // Level 17 → 18
        10_000, // Level 18 → 19
        11_500, // Level 19 → 20
        13_000, // Level 20 → 21
    )

    /**
     * Maximum level defined in XP curve.
     */
    const val MAX_DEFINED_LEVEL = 20

    /**
     * Get XP required to reach a specific level (from level 1).
     * For example: xpRequiredForLevel(5) = 100 + 150 + 250 + 400 = 900
     */
    fun xpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        if (level > MAX_DEFINED_LEVEL + 1) {
            // Beyond defined curve - use formula (1.5x growth)
            val lastDefined = xpRequiredForLevel(MAX_DEFINED_LEVEL + 1)
            val additional = (level - MAX_DEFINED_LEVEL - 1) * 15_000
            return lastDefined + additional
        }
        return XP_CURVE.take(level - 1).sum()
    }

    /**
     * Get XP required to advance from current level to next level.
     * For example: xpForNextLevel(4) = 400 (Level 4 → 5)
     */
    fun xpForNextLevel(currentLevel: Int): Int {
        if (currentLevel < 1) return XP_CURVE[0]
        if (currentLevel > MAX_DEFINED_LEVEL) {
            // Beyond curve - use linear formula
            return 15_000
        }
        return XP_CURVE[currentLevel - 1]
    }

    /**
     * Calculate what level a player should be given total accumulated XP.
     * Returns (level, remainingXP)
     */
    fun calculateLevel(totalXp: Int): Pair<Int, Int> {
        if (totalXp < 0) return Pair(1, 0)

        var level = 1
        var remaining = totalXp

        // Find the highest level achievable with this XP
        while (remaining >= xpForNextLevel(level)) {
            remaining -= xpForNextLevel(level)
            level++
            if (level > 1000) break // Safety limit
        }

        return Pair(level, remaining)
    }

    /**
     * Add XP to a SkillProfile and return updated profile.
     * Handles multiple level-ups and preserves excess XP.
     *
     * Example:
     * - Level 4 with 350/400 XP
     * - Add 200 XP
     * - Result: Level 5 with 150/600 XP
     */
    fun addXp(profile: SkillProfile, amount: Int): SkillProfile {
        if (amount <= 0) return profile

        var currentLevel = profile.level
        var currentXp = profile.xp + amount
        var levelsGained = 0

        // Check for level-ups
        while (currentXp >= xpForNextLevel(currentLevel)) {
            currentXp -= xpForNextLevel(currentLevel)
            currentLevel++
            levelsGained++

            // Safety limit
            if (currentLevel > 1000) {
                currentLevel = 1000
                currentXp = 0
                break
            }
        }

        return profile.withLevelAndXp(currentLevel, currentXp)
    }

    /**
     * Calculate progress percentage for a level.
     * Returns 0.0 to 1.0 (0% to 100%)
     */
    fun progressPercentage(profile: SkillProfile): Double {
        val required = xpForNextLevel(profile.level)
        if (required <= 0) return 1.0
        return (profile.xp.toDouble() / required).coerceIn(0.0, 1.0)
    }
}
