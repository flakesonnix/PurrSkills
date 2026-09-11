package gay.nyaa.purrskills.skill

/**
 * Represents a player's progress in a single skill.
 * Immutable data class - modifications return new instances.
 */
data class SkillProfile(
    val skill: Skill,
    val level: Int = 1,
    val xp: Int = 0,
) {
    init {
        require(level >= 1) { "Level must be >= 1" }
        require(xp >= 0) { "XP must be >= 0" }
    }

    /**
     * Add XP and return a new SkillProfile with updated level/xp.
     * Handles multiple level-ups and preserves excess XP.
     */
    fun addXp(amount: Int, progression: SkillProgression): SkillProfile {
        require(amount >= 0) { "Cannot add negative XP" }
        return progression.addXp(this, amount)
    }

    /**
     * Get XP required for next level.
     */
    fun xpForNextLevel(progression: SkillProgression): Int = progression.xpRequiredForLevel(level + 1)

    /**
     * Create a copy with a new level.
     */
    fun withLevel(newLevel: Int): SkillProfile = copy(level = newLevel)

    /**
     * Create a copy with new XP.
     */
    fun withXp(newXp: Int): SkillProfile = copy(xp = newXp)

    /**
     * Create a copy with both level and XP updated.
     */
    fun withLevelAndXp(newLevel: Int, newXp: Int): SkillProfile = copy(level = newLevel, xp = newXp)
}
