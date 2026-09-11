package gay.nyaa.purrskills

import com.purrcore.PurrCorePlugin
import com.purrcore.db.Database
import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.command.SkillsCommand
import gay.nyaa.purrskills.command.SkillsMenuCommand
import gay.nyaa.purrskills.db.SkillRepository
import gay.nyaa.purrskills.gui.SkillMenuGUI
import gay.nyaa.purrskills.gui.SkillMenuListener
import gay.nyaa.purrskills.listener.PlayerLifecycleListener
import gay.nyaa.purrskills.skill.combat.CombatListener
import gay.nyaa.purrskills.skill.farming.FarmingListener
import gay.nyaa.purrskills.skill.fishing.FishingListener
import gay.nyaa.purrskills.skill.foraging.ForagingListener
import gay.nyaa.purrskills.skill.mining.MiningListener
import gay.nyaa.purrskills.stats.StatsManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

/**
 * PurrSkills - MCMMO-style skills system.
 * Depends on PurrCore for database and i18n infrastructure.
 */
class PurrSkillsPlugin : JavaPlugin() {

    private lateinit var core: PurrCorePlugin
    private lateinit var database: Database
    private lateinit var i18n: I18n
    private lateinit var repository: SkillRepository
    lateinit var statsManager: StatsManager
        private set
    lateinit var skillManager: SkillManager
        private set

    private var periodicSaveTask: BukkitTask? = null

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

        // Initialize repository
        repository = SkillRepository(database, logger)

        // Initialize stats manager
        statsManager = StatsManager()

        // Initialize skill manager
        skillManager = SkillManager(config, i18n, repository, statsManager)

        // Register listeners
        registerListeners()

        // Register commands
        registerCommands()

        // Start periodic save task
        startPeriodicSave()

        logger.info("PurrSkills enabled - using PurrCore (DB=${database.isConnected()})")
    }

    override fun onDisable() {
        // Stop periodic save task
        periodicSaveTask?.cancel()

        // Save all cached player skills to database (synchronous on shutdown)
        if (::skillManager.isInitialized) {
            logger.info("Saving all player skills...")
            val saved = skillManager.saveAll()
            logger.info("Saved skills for $saved players")
            skillManager.clearCache()
        }
        logger.info("PurrSkills disabled")
    }

    /**
     * Register all event listeners.
     */
    private fun registerListeners() {
        // Player lifecycle (join/quit)
        server.pluginManager.registerEvents(PlayerLifecycleListener(this), this)

        // Skill XP sources
        server.pluginManager.registerEvents(MiningListener(this), this)
        server.pluginManager.registerEvents(FarmingListener(this), this)
        server.pluginManager.registerEvents(ForagingListener(this), this)
        server.pluginManager.registerEvents(CombatListener(this), this)
        server.pluginManager.registerEvents(FishingListener(this), this)

        // Speed effect listeners
        server.pluginManager.registerEvents(
            gay.nyaa.purrskills.listener.MiningSpeedListener(this),
            this,
        )
        server.pluginManager.registerEvents(
            gay.nyaa.purrskills.listener.FarmingSpeedListener(this),
            this,
        )
        server.pluginManager.registerEvents(
            gay.nyaa.purrskills.listener.ForagingSpeedListener(this),
            this,
        )
        server.pluginManager.registerEvents(
            gay.nyaa.purrskills.listener.FishingSpeedListener(this),
            this,
        )

        // GUI click listener
        server.pluginManager.registerEvents(SkillMenuListener(i18n), this)
    }

    /**
     * Register all commands.
     */
    private fun registerCommands() {
        val skillsCommand = SkillsCommand(skillManager, i18n)
        getCommand("skills")?.setExecutor(skillsCommand)
        getCommand("skills")?.tabCompleter = skillsCommand

        val statsCommand = gay.nyaa.purrskills.command.StatsCommand(skillManager, statsManager, i18n)
        getCommand("stats")?.setExecutor(statsCommand)
        getCommand("stats")?.tabCompleter = statsCommand

        // Skills menu GUI command
        val gui = SkillMenuGUI(skillManager, i18n)
        val skillsMenuCommand = SkillsMenuCommand(gui, i18n)
        getCommand("skillsmenu")?.setExecutor(skillsMenuCommand)
    }

    /**
     * Start periodic autosave task.
     * Saves all cached player skills to database asynchronously.
     */
    private fun startPeriodicSave() {
        val intervalMinutes = config.getInt("skills.autosave-interval-minutes", 5)
        val intervalTicks = intervalMinutes * 60 * 20L // minutes to ticks

        periodicSaveTask = server.scheduler.runTaskTimerAsynchronously(
            this,
            Runnable {
                try {
                    val count = skillManager.getCachedPlayerCount()
                    if (count == 0) return@Runnable

                    val saved = skillManager.saveAll()
                    logger.info("Autosave: Saved skills for $saved/$count players")
                } catch (e: Exception) {
                    logger.severe("Autosave failed: ${e.message}")
                    e.printStackTrace()
                }
            },
            intervalTicks,
            intervalTicks,
        )

        logger.info("Periodic autosave started (interval: ${intervalMinutes}min)")
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
