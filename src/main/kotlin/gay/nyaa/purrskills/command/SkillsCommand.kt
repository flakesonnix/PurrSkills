package gay.nyaa.purrskills.command

import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.SkillManager
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProgression
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Command executor for /skills [player]
 *
 * Shows skill levels, XP, and progress for a player.
 * Players can view their own skills by default.
 * Viewing other players requires purrskills.skills.others permission.
 */
class SkillsCommand(
    private val skillManager: SkillManager,
    private val i18n: I18n,
) : CommandExecutor,
    TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        // Determine target player
        val target: UUID
        val targetName: String

        when {
            args.isEmpty() -> {
                // /skills - show own skills
                if (sender !is Player) {
                    sender.sendMessage(i18n.t("commands.skills.player-only"))
                    return true
                }
                target = sender.uniqueId
                targetName = sender.name
            }
            args.size == 1 -> {
                // /skills <player> - show other player's skills
                if (!sender.hasPermission("purrskills.skills.others")) {
                    sender.sendMessage(i18n.t("commands.skills.no-permission-others"))
                    return true
                }

                val targetPlayer = Bukkit.getPlayerExact(args[0])
                if (targetPlayer == null) {
                    sender.sendMessage(i18n.t("commands.skills.player-not-found", "player" to args[0]))
                    return true
                }

                target = targetPlayer.uniqueId
                targetName = targetPlayer.name
            }
            else -> {
                sender.sendMessage(i18n.t("commands.skills.usage"))
                return true
            }
        }

        // Load skills (from cache or database)
        val skills = skillManager.getPlayerSkills(target)

        // Header
        sender.sendMessage(i18n.t("commands.skills.header", "player" to targetName))
        sender.sendMessage("")

        // Display each skill
        for (skill in Skill.entries) {
            val profile = skills.getSkill(skill)
            val level = profile.level
            val currentXp = profile.xp
            val requiredXp = profile.xpForNextLevel(SkillProgression)
            val percentage = if (requiredXp > 0) {
                (currentXp.toDouble() / requiredXp * 100).toInt()
            } else {
                100 // Max level
            }

            val skillName = i18n.t("skills.${skill.displayName()}")
            val line = i18n.t(
                "commands.skills.skill-line",
                "skill" to skillName,
                "level" to level.toString(),
                "xp" to formatNumber(currentXp),
                "required" to formatNumber(requiredXp),
                "percent" to percentage.toString(),
            )
            sender.sendMessage(line)
        }

        // Power level (sum of all levels)
        sender.sendMessage("")
        val powerLevel = skills.powerLevel()
        sender.sendMessage(i18n.t("commands.skills.power-level", "level" to powerLevel.toString()))

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> {
        // Tab complete player names for /skills <player>
        if (args.size == 1 && sender.hasPermission("purrskills.skills.others")) {
            val prefix = args[0].lowercase()
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.lowercase().startsWith(prefix) }
                .sorted()
        }
        return emptyList()
    }

    /**
     * Format number with thousands separator.
     * 1234 -> "1,234"
     */
    private fun formatNumber(number: Int): String = String.format("%,d", number)
}
