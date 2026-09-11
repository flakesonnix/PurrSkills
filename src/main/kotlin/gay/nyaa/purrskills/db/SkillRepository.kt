package gay.nyaa.purrskills.db

import com.purrcore.db.Database
import gay.nyaa.purrskills.skill.PlayerSkills
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProfile
import java.sql.SQLException
import java.util.UUID
import java.util.logging.Logger

/**
 * Repository for persisting player skills to database.
 * Uses PurrCore's shared database connection pool.
 *
 * Thread-safe: All operations use database connections from pool.
 * Does not hold state - pure data access layer.
 */
class SkillRepository(
    private val database: Database,
    private val logger: Logger,
) {

    /**
     * Load all skills for a player from database.
     * Returns PlayerSkills with all 5 skills populated.
     *
     * If no data exists for a skill, returns default (level 1, xp 0).
     * If player has never played, returns all defaults.
     *
     * @param uuid Player UUID
     * @return PlayerSkills with all skills loaded
     * @throws SQLException if database error occurs
     */
    fun loadPlayerSkills(uuid: UUID): PlayerSkills {
        val sql = """
            SELECT skill, level, xp
            FROM player_skills
            WHERE player_uuid = ?
        """.trimIndent()

        val skillMap = mutableMapOf<Skill, SkillProfile>()

        try {
            database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, uuid.toString())
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            val skillName = rs.getString("skill")
                            val level = rs.getInt("level")
                            val xp = rs.getInt("xp")

                            try {
                                val skill = Skill.valueOf(skillName.uppercase())
                                skillMap[skill] = SkillProfile(skill, level, xp)
                            } catch (e: IllegalArgumentException) {
                                logger.warning("Unknown skill '$skillName' for player $uuid - skipping")
                            }
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            logger.severe("Failed to load skills for $uuid: ${e.message}")
            throw e
        }

        // Create PlayerSkills with loaded data, filling in defaults for missing skills
        return PlayerSkills.create(uuid, skillMap)
    }

    /**
     * Save all skills for a player to database.
     * Uses UPSERT pattern (INSERT ... ON CONFLICT UPDATE or REPLACE INTO).
     *
     * @param playerSkills The player skills to save
     * @throws SQLException if database error occurs
     */
    fun savePlayerSkills(playerSkills: PlayerSkills) {
        // Use REPLACE INTO for SQLite, INSERT ... ON DUPLICATE KEY UPDATE for MySQL
        val sql = if (database.isSqlite()) {
            """
            REPLACE INTO player_skills (player_uuid, skill, level, xp, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            """.trimIndent()
        } else {
            """
            INSERT INTO player_skills (player_uuid, skill, level, xp, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE level = VALUES(level), xp = VALUES(xp), last_updated = CURRENT_TIMESTAMP
            """.trimIndent()
        }

        try {
            database.getConnection().use { conn ->
                conn.autoCommit = false
                try {
                    conn.prepareStatement(sql).use { stmt ->
                        // Save all 5 skills
                        for (skill in Skill.entries) {
                            val profile = playerSkills.getSkill(skill)
                            stmt.setString(1, playerSkills.playerUuid.toString())
                            stmt.setString(2, skill.name)
                            stmt.setInt(3, profile.level)
                            stmt.setInt(4, profile.xp)
                            stmt.addBatch()
                        }
                        stmt.executeBatch()
                    }
                    conn.commit()
                } catch (e: Exception) {
                    conn.rollback()
                    throw e
                }
            }
        } catch (e: SQLException) {
            logger.severe("Failed to save skills for ${playerSkills.playerUuid}: ${e.message}")
            throw e
        }
    }

    /**
     * Save multiple players' skills in a single transaction.
     * More efficient than calling savePlayerSkills() repeatedly.
     *
     * @param allSkills Map of UUID to PlayerSkills
     * @return Number of players successfully saved
     */
    fun saveAll(allSkills: Map<UUID, PlayerSkills>): Int {
        if (allSkills.isEmpty()) return 0

        val sql = if (database.isSqlite()) {
            """
            REPLACE INTO player_skills (player_uuid, skill, level, xp, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            """.trimIndent()
        } else {
            """
            INSERT INTO player_skills (player_uuid, skill, level, xp, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE level = VALUES(level), xp = VALUES(xp), last_updated = CURRENT_TIMESTAMP
            """.trimIndent()
        }

        var savedCount = 0

        try {
            database.getConnection().use { conn ->
                conn.autoCommit = false
                try {
                    conn.prepareStatement(sql).use { stmt ->
                        for ((uuid, playerSkills) in allSkills) {
                            for (skill in Skill.entries) {
                                val profile = playerSkills.getSkill(skill)
                                stmt.setString(1, uuid.toString())
                                stmt.setString(2, skill.name)
                                stmt.setInt(3, profile.level)
                                stmt.setInt(4, profile.xp)
                                stmt.addBatch()
                            }
                            savedCount++
                        }
                        stmt.executeBatch()
                    }
                    conn.commit()
                } catch (e: Exception) {
                    conn.rollback()
                    throw e
                }
            }
        } catch (e: SQLException) {
            logger.severe("Failed to save ${allSkills.size} players' skills: ${e.message}")
            throw e
        }

        return savedCount
    }

    /**
     * Delete all skills for a player.
     * Should rarely be used - mainly for admin commands or data cleanup.
     *
     * @param uuid Player UUID
     * @throws SQLException if database error occurs
     */
    fun deletePlayerSkills(uuid: UUID) {
        val sql = "DELETE FROM player_skills WHERE player_uuid = ?"

        try {
            database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, uuid.toString())
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            logger.severe("Failed to delete skills for $uuid: ${e.message}")
            throw e
        }
    }
}
