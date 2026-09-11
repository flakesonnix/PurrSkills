package gay.nyaa.purrskills

import gay.nyaa.purrskills.db.SkillRepository
import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProgression
import gay.nyaa.purrskills.stats.SkillRewardCalculator
import gay.nyaa.purrskills.stats.StatsManager
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * Manages player skills, XP awarding, notifications, persistence, and stat rewards.
 * Encapsulates skill-related business logic separately from plugin infrastructure.
 */
class SkillManager(
    private val config: FileConfiguration,
    private val i18n: com.purrcore.i18n.I18n,
    private val repository: SkillRepository,
    private val statsManager: StatsManager,
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
     * Also removes skill rewards from stats manager.
     *
     * Called when player quits.
     *
     * @param uuid Player UUID
     */
    fun removePlayer(uuid: UUID) {
        savePlayer(uuid)
        playerSkillsCache.remove(uuid)
        statsManager.clearModifiers(uuid)
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

        // If leveled up, refresh skill rewards
        if (leveledUp) {
            refreshSkillRewards(uuid, skill)
        }

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
     * Refresh skill rewards for a specific skill.
     * Removes old modifiers and applies new ones based on current level.
     *
     * This is idempotent: calling it multiple times with the same level
     * produces the same result.
     *
     * @param uuid Player UUID
     * @param skill The skill to refresh
     */
    fun refreshSkillRewards(uuid: UUID, skill: Skill) {
        val playerSkills = playerSkillsCache[uuid] ?: return
        val skillProfile = playerSkills.getSkill(skill)
        val level = skillProfile.level

        // Get the stat source for this skill
        val source = when (skill) {
            Skill.MINING -> gay.nyaa.purrskills.stats.StatSource.SKILL_MINING
            Skill.FARMING -> gay.nyaa.purrskills.stats.StatSource.SKILL_FARMING
            Skill.FORAGING -> gay.nyaa.purrskills.stats.StatSource.SKILL_FORAGING
            Skill.COMBAT -> gay.nyaa.purrskills.stats.StatSource.SKILL_COMBAT
            Skill.FISHING -> gay.nyaa.purrskills.stats.StatSource.SKILL_FISHING
        }

        // Remove old modifiers from this skill source
        statsManager.removeModifiersFromSource(uuid, source)

        // Calculate new modifiers for current level
        val newModifiers = SkillRewardCalculator.calculateRewards(skill, level)

        // Apply new modifiers
        statsManager.addModifiers(uuid, newModifiers)
    }

    /**
     * Refresh all skill rewards for a player.
     * Called on player join to sync stats with current skill levels.
     *
     * @param uuid Player UUID
     */
    fun refreshAllSkillRewards(uuid: UUID) {
        for (skill in Skill.entries) {
            refreshSkillRewards(uuid, skill)
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

    /**
     * Calculate final stats for a player from all sources.
     * Convenience method that delegates to StatsManager.
     *
     * @param uuid Player UUID
     * @return PlayerStats with calculated values
     */
    fun calculateStats(uuid: UUID) = statsManager.calculateStats(uuid)

    /**
     * Get calculated stats for a player.
     * Alias for calculateStats for backward compatibility.
     *
     * @param uuid Player UUID
     * @return PlayerStats with calculated values
     */
    fun getPlayerStats(uuid: UUID) = statsManager.calculateStats(uuid)
}
