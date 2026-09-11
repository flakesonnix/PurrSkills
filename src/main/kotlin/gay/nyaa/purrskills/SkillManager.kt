package gay.nyaa.purrskills

import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProgression
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * Manages player skills, XP awarding, and notifications.
 * Encapsulates skill-related business logic separately from plugin infrastructure.
 */
class SkillManager(
    private val config: FileConfiguration,
    private val i18n: com.purrcore.i18n.I18n,
) {

    // In-memory cache of player skills
    // TODO: Load from database on join, save to database periodically
    private val playerSkillsCache = ConcurrentHashMap<UUID, PlayerSkills>()

    /**
     * Get or create PlayerSkills for a player.
     * Currently in-memory only - no database persistence yet.
     */
    fun getPlayerSkills(player: Player): PlayerSkills = getPlayerSkills(player.uniqueId)

    /**
     * Get or create PlayerSkills by UUID.
     */
    fun getPlayerSkills(uuid: UUID): PlayerSkills = playerSkillsCache.computeIfAbsent(uuid) { PlayerSkills.create(uuid) }

    /**
     * Award XP to a player for a specific skill.
     * This is the main API used by XP sources (Mining, Farming, Foraging, etc.).
     *
     * Handles:
     * - Updating PlayerSkills
     * - Detecting level-ups
     * - Showing actionbar/title notifications
     */
    fun awardSkillXp(
        player: Player,
        skill: Skill,
        amount: Int,
    ) {
        val uuid = player.uniqueId
        val before = getPlayerSkills(uuid)
        val after = before.addXp(skill, amount)

        // Update cache
        playerSkillsCache[uuid] = after

        // Check if leveled up
        val beforeLevel = before.getSkill(skill).level
        val afterLevel = after.getSkill(skill).level
        val leveledUp = afterLevel > beforeLevel

        // Show actionbar XP gain (if enabled)
        if (config.getBoolean("skills.show-xp-actionbar", true)) {
            val skillProfile = after.getSkill(skill)
            val required = skillProfile.xpForNextLevel(SkillProgression)
            val message = i18n.t(
                "skills.xp-gain",
                "xp" to amount.toString(),
                "skill" to i18n.t("skills.${skill.displayName()}"),
                "current" to skillProfile.xp.toString(),
                "required" to required.toString(),
            )
            player.sendActionBar(message)
        }

        // Show level-up notification
        if (leveledUp && config.getBoolean("skills.show-levelup-title", true)) {
            val message = i18n.t(
                "skills.level-up",
                "skill" to i18n.t("skills.${skill.displayName()}"),
                "level" to afterLevel.toString(),
            )
            player.sendTitle("", message, 10, 40, 10)

            // Play level-up sound
            if (config.getBoolean("skills.levelup-sound", true)) {
                val sound = try {
                    org.bukkit.Sound.valueOf(
                        config.getString("skills.levelup-sound-type", "ENTITY_PLAYER_LEVELUP")!!,
                    )
                } catch (e: Exception) {
                    org.bukkit.Sound.ENTITY_PLAYER_LEVELUP
                }
                player.playSound(player.location, sound, 1.0f, 1.0f)
            }
        }
    }

    /**
     * Clear the player skills cache.
     * Called on plugin disable.
     */
    fun clearCache() {
        playerSkillsCache.clear()
    }
}
