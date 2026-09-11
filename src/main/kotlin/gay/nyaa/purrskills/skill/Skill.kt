package gay.nyaa.purrskills.skill

/**
 * Skill types in PurrSkills.
 * Each skill has independent XP and level progression.
 */
enum class Skill {
    MINING,
    FORAGING,
    FARMING,
    COMBAT,
    FISHING,
    ;

    /**
     * Get the display name for this skill (lowercase).
     * Used for translation keys and display.
     */
    fun displayName(): String = name.lowercase()

    companion object {
        /**
         * Get Skill from string name (case-insensitive).
         */
        fun fromString(name: String): Skill? = entries.find { it.name.equals(name, ignoreCase = true) }
    }
}
