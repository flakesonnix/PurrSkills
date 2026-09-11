package gay.nyaa.purrskills

import com.purrcore.PurrCorePlugin
import com.purrcore.db.Database
import com.purrcore.i18n.I18n
import org.bukkit.plugin.java.JavaPlugin

/**
 * PurrSkills - MCMMO-style skills system.
 * Depends on PurrCore for database and i18n infrastructure.
 */
class PurrSkillsPlugin : JavaPlugin() {

    private lateinit var core: PurrCorePlugin
    private lateinit var database: Database
    private lateinit var i18n: I18n

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

        logger.info("PurrSkills enabled - using PurrCore (DB=${database.isConnected()})")
    }

    override fun onDisable() {
        logger.info("PurrSkills disabled")
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
