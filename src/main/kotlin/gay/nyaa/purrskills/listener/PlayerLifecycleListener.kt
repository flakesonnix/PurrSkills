package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Handles player join/quit for loading and saving skills.
 *
 * Join: Load skills from database into cache
 * Quit: Save skills to database and remove from cache
 */
class PlayerLifecycleListener(private val plugin: PurrSkillsPlugin) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId

        // Load skills asynchronously to avoid blocking the join event
        plugin.server.scheduler.runTaskAsynchronously(
            plugin,
            Runnable {
                try {
                    plugin.skillManager.loadPlayer(uuid)
                    plugin.logger.fine("Loaded skills for ${player.name} ($uuid)")
                } catch (e: Exception) {
                    plugin.logger.severe("Failed to load skills for ${player.name} ($uuid): ${e.message}")
                    e.printStackTrace()
                }
            },
        )
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId

        // Save and remove from cache synchronously to ensure data is saved
        // This runs on the main thread but should be fast (single player save)
        try {
            plugin.skillManager.removePlayer(uuid)
            plugin.logger.fine("Saved and unloaded skills for ${player.name} ($uuid)")
        } catch (e: Exception) {
            plugin.logger.severe("Failed to save skills for ${player.name} ($uuid): ${e.message}")
            e.printStackTrace()
        }
    }
}
