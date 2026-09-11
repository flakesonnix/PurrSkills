package gay.nyaa.purrskills

import com.purrcore.PurrCorePlugin
import com.purrcore.db.Database
import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.farming.FarmingListener
import gay.nyaa.purrskills.skill.foraging.ForagingListener
import gay.nyaa.purrskills.skill.mining.MiningListener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * PurrSkills - MCMMO-style skills system.
 * Depends on PurrCore for database and i18n infrastructure.
 */
class PurrSkillsPlugin : JavaPlugin() {

    private lateinit var core: PurrCorePlugin
    private lateinit var database: Database
    private lateinit var i18n: I18n

    // In-memory cache of player skills
    // TODO: Load from database on join, save to database periodically
    private val playerSkillsCache = ConcurrentHashMap<UUID, PlayerSkills>()

    override fun onEnable() {
        // Get PurrCore instance
        core = PurrCorePlugin.get()
        database = core.database
        i18n = core.i18n

        // Save default config
        saveDefaultConfig()

        // Initialize database tables
        try {
            migrateDatabase()
        } catch (e: Exception) {
            logger.severe("Failed to initialize PurrSkills database: ${e.message}")
            e.printStackTrace()
        }

        // Register listeners
        registerListeners()

        logger.info("PurrSkills enabled - using PurrCore (DB=${database.isConnected()})")
    }

    override fun onDisable() {
        // TODO: Save all cached player skills to database
        playerSkillsCache.clear()
        logger.info("PurrSkills disabled")
    }

    /**
     * Register all event listeners.
     */
    private fun registerListeners() {
        server.pluginManager.registerEvents(MiningListener(this), this)
        server.pluginManager.registerEvents(FarmingListener(this), this)
        server.pluginManager.registerEvents(ForagingListener(this), this)
    }

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
     * This is the main API used by XP sources (Mining, Combat, etc.).
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
            val required = skillProfile.xpForNextLevel(gay.nyaa.purrskills.skill.SkillProgression)
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
     * Create PurrSkills-specific database tables.
     * Uses the shared PurrCore database connection.
     */
    private fun migrateDatabase() {
        val ddls = arrayOf(
            """
            CREATE TABLE IF NOT EXISTS player_skills (
              id ${if (database.isSqlite()) "INTEGER" else "INT"} PRIMARY KEY ${if (database.isSqlite()) "AUTOINCREMENT" else "AUTO_INCREMENT"},
              player_uuid VARCHAR(36) NOT NULL,
              skill VARCHAR(32) NOT NULL,
              level INT NOT NULL DEFAULT 1,
              xp INT NOT NULL DEFAULT 0,
              last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
              UNIQUE KEY unique_player_skill (player_uuid, skill)
            )
            """.trimIndent(),
            if (database.isSqlite()) {
                "CREATE INDEX IF NOT EXISTS idx_player_skill ON player_skills(player_uuid, skill)"
            } else {
                "CREATE INDEX idx_player_skill ON player_skills(player_uuid, skill)"
            },
        )

        database.getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                for (ddl in ddls) {
                    try {
                        stmt.execute(ddl)
                    } catch (e: Exception) {
                        val msg = e.message ?: ""
                        if (!msg.contains("already exists") && !msg.contains("Duplicate")) {
                            throw e
                        }
                    }
                }
            }
        }

        logger.info("PurrSkills database tables initialized")
    }

    /**
     * Get the shared database instance from PurrCore.
     */
    fun getDatabase(): Database = database

    /**
     * Get the i18n instance from PurrCore.
     */
    fun getI18n(): I18n = i18n
}
