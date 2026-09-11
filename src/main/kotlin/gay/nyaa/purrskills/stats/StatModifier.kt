package gay.nyaa.purrskills.stats

/**
 * A single stat modifier from a specific source.
 *
 * Modifiers can be additive or multiplicative:
 * - ADDITIVE: Added to base value (e.g., +10 Health)
 * - MULTIPLICATIVE: Multiplied with final value (e.g., +10% = 0.1 multiplier)
 *
 * Example:
 * ```
 * StatModifier(StatType.HEALTH, 50.0, ModifierType.ADDITIVE, StatSource.SKILL_COMBAT)
 * // Adds +50 Health from Combat skill
 * ```
 *
 * Immutable data class - modifications return new instances.
 */
data class StatModifier(
    val stat: StatType,
    val value: Double,
    val type: ModifierType,
    val source: StatSource,
) {
    init {
        require(value.isFinite()) { "Stat modifier value must be finite" }
    }

    /**
     * Type of modifier application.
     */
    enum class ModifierType {
        /**
         * Added to base value.
         * Example: Base 100 + Modifier 20 = 120
         */
        ADDITIVE,

        /**
         * Multiplied with final value after additive modifiers.
         * Example: (Base 100 + Additive 20) × (1 + 0.1) = 132
         * Value 0.1 = +10%
         */
        MULTIPLICATIVE,
    }

    companion object {
        /**
         * Create an additive modifier.
         */
        fun additive(stat: StatType, value: Double, source: StatSource): StatModifier = StatModifier(stat, value, ModifierType.ADDITIVE, source)

        /**
         * Create a multiplicative modifier.
         * @param value The multiplier (0.1 = +10%, 0.5 = +50%)
         */
        fun multiplicative(stat: StatType, value: Double, source: StatSource): StatModifier = StatModifier(stat, value, ModifierType.MULTIPLICATIVE, source)
    }
}
