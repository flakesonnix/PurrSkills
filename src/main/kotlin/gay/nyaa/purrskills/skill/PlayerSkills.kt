package gay.nyaa.purrskills.skill

import java.util.UUID

/**
 * Container for all skill profiles of a single player.
 * Mirrors the Hypixel SkyBlock model where each player has independent progress in each skill.
 *
 * Immutable - modifications return new instances.
 */
data class PlayerSkills(
    val playerUuid: UUID,
    val skills: Map<Skill, SkillProfile>,
) {
    /**
     * Get a specific skill profile, or create a new one at level 1 if not present.
     */
    fun getSkill(skill: Skill): SkillProfile = skills[skill] ?: SkillProfile(skill, level = 1, xp = 0)

    /**
     * Add XP to a specific skill and return updated PlayerSkills.
     * This is the main API for awarding XP from gameplay events.
     *
     * The caller doesn't need to know about level-ups or XP overflow -
     * SkillProgression handles all of that internally.
     */
    fun addXp(skill: Skill, amount: Int): PlayerSkills {
        val current = getSkill(skill)
        val updated = current.addXp(amount, SkillProgression)
        return copy(skills = skills + (skill to updated))
    }

    /**
     * Set a skill profile directly (for database loading).
     */
    fun setSkill(profile: SkillProfile): PlayerSkills = copy(skills = skills + (profile.skill to profile))

    /**
     * Get total power level (sum of all skill levels).
     * Used for leaderboards and overall progression.
     */
    fun powerLevel(): Int = Skill.entries.sumOf { getSkill(it).level }

    companion object {
        /**
         * Create a new PlayerSkills with all skills at level 1.
         */
        fun create(playerUuid: UUID): PlayerSkills {
            val initialSkills = Skill.entries.associateWith { skill ->
                SkillProfile(skill, level = 1, xp = 0)
            }
            return PlayerSkills(playerUuid, initialSkills)
        }
    }
}
