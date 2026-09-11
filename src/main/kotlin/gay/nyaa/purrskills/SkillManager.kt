package gay.nyaa.purrskills

import gay.nyaa.purrskills.db.SkillRepository
import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProgression
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * Manages player skills, XP awarding, notifications, and persistence.
 * Encapsulates skill-related business logic separately from plugin infrastructure.
 */
class SkillManager(
    private val config: FileConfiguration,
    private val i18n: com.purrcore.i18n.I18n,
    private val repository: SkillRepository,
) {

    // In-memory cache of player skills
    // Loaded from database on join, saved periodically and on quit
    private val playerSkillsCache = ConcurrentHashMap<UUID, PlayerSkills>()

    /**
     * Load player skills from database and cache them.
     * Called when player joins the server.
     *
     * @param uuid Player UUID
     * @return Loaded PlayerSkills (or defaults if new player)
     */
    fun loadPlayer(uuid: UUID): PlayerSkills = try {
        val skills = repository.loadPlayerSkills(uuid)
        playerSkillsCache[uuid] = skills
        skills
    } catch (e: Exception) {
        // On error, use default skills to prevent progression loss
        // Error already logged by repository
        val defaultSkills = PlayerSkills.create(uuid)
        playerSkillsCache[uuid] = defaultSkills
        defaultSkills
    }

    /**
     * Save player skills to database.
     * Called when player quits or during periodic save.
     *
     * @param uuid Player UUID
     * @return true if saved successfully, false on error
     */
    fun savePlayer(uuid: UUID): Boolean {
        val skills = playerSkillsCache[uuid] ?: return false

        return try {
            repository.savePlayerSkills(skills)
            true
        } catch (e: Exception) {
            // Error already logged by repository
            false
        }
    }

    /**
     * Remove player from cache after saving.
     * Called when player quits.
     *
     * @param uuid Player UUID
     */
    fun removePlayer(uuid: UUID) {
        savePlayer(uuid)
        playerSkillsCache.remove(uuid)
    }

    /**
     * Save all cached player skills to database.
     * Called on periodic save and plugin disable.
     *
     * @return Number of players successfully saved
     */
    fun saveAll(): Int {
        if (playerSkillsCache.isEmpty()) return 0

        return try {
            // Create snapshot to avoid concurrent modification
            val snapshot = HashMap(playerSkillsCache)
            repository.saveAll(snapshot)
        } catch (e: Exception) {
            // Error already logged by repository
            0
        }
    }

    /**
     * Get PlayerSkills for a player.
     * Returns cached skills if available, otherwise loads from database.
     *
     * For online players, skills should already be loaded.
     * For offline players, this loads fresh from database.
     */
    fun getPlayerSkills(player: Player): PlayerSkills = getPlayerSkills(player.uniqueId)

    /**
     * Get PlayerSkills by UUID.
     * Returns cached skills if available, otherwise loads from database.
     */
    fun getPlayerSkills(uuid: UUID): PlayerSkills = playerSkillsCache[uuid] ?: loadPlayer(uuid)

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
     * Should only be called after saveAll() on plugin disable.
     */
    fun clearCache() {
        playerSkillsCache.clear()
    }

    /**
     * Get the number of players currently cached.
     * Useful for monitoring and debugging.
     */
    fun getCachedPlayerCount(): Int = playerSkillsCache.size
}
