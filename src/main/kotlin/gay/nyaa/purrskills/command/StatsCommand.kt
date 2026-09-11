package gay.nyaa.purrskills.command

import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.SkillManager
import gay.nyaa.purrskills.stats.PlayerStats
import gay.nyaa.purrskills.stats.StatType
import gay.nyaa.purrskills.stats.StatsManager
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Command executor for /stats [player]
 *
 * Shows calculated stats from all sources (skills, items, buffs).
 * Stats are grouped by category for readability.
 */
class StatsCommand(
    private val skillManager: SkillManager,
    private val statsManager: StatsManager,
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
                // /stats - show own stats
                if (sender !is Player) {
                    sender.sendMessage(i18n.t("commands.stats.player-only"))
                    return true
                }
                target = sender.uniqueId
                targetName = sender.name
            }
            args.size == 1 -> {
                // /stats <player> - show other player's stats
                if (!sender.hasPermission("purrskills.stats.others")) {
                    sender.sendMessage(i18n.t("commands.stats.no-permission-others"))
                    return true
                }

                val targetPlayer = Bukkit.getPlayerExact(args[0])
                if (targetPlayer == null) {
                    sender.sendMessage(i18n.t("commands.stats.player-not-found", "player" to args[0]))
                    return true
                }

                target = targetPlayer.uniqueId
                targetName = targetPlayer.name
            }
            else -> {
                sender.sendMessage(i18n.t("commands.stats.usage"))
                return true
            }
        }

        // Calculate stats
        val stats = statsManager.calculateStats(target)

        // Header
        sender.sendMessage(i18n.t("commands.stats.header", "player" to targetName))
        sender.sendMessage("")

        // Display stats by category
        displayMiningStats(sender, stats)
        displayCombatStats(sender, stats)
        displayFarmingStats(sender, stats)
        displayForagingStats(sender, stats)
        displayFishingStats(sender, stats)
        displayGeneralStats(sender, stats)

        return true
    }

    private fun displayMiningStats(sender: CommandSender, stats: PlayerStats) {
        val speed = stats.getStat(StatType.MINING_SPEED)
        val fortune = stats.getStat(StatType.MINING_FORTUNE)

        if (speed == StatType.MINING_SPEED.baseValue && fortune == StatType.MINING_FORTUNE.baseValue) {
            return // No mining stats, skip section
        }

        sender.sendMessage(i18n.t("commands.stats.category-mining"))
        displayStat(sender, StatType.MINING_SPEED, speed)
        displayStat(sender, StatType.MINING_FORTUNE, fortune)
        sender.sendMessage("")
    }

    private fun displayCombatStats(sender: CommandSender, stats: PlayerStats) {
        val health = stats.getStat(StatType.HEALTH)
        val damage = stats.getStat(StatType.DAMAGE)
        val strength = stats.getStat(StatType.STRENGTH)
        val defense = stats.getStat(StatType.DEFENSE)
        val critChance = stats.getStat(StatType.CRIT_CHANCE)
        val critDamage = stats.getStat(StatType.CRIT_DAMAGE)

        val hasAny = health != StatType.HEALTH.baseValue ||
            damage != StatType.DAMAGE.baseValue ||
            strength != StatType.STRENGTH.baseValue ||
            defense != StatType.DEFENSE.baseValue ||
            critChance != StatType.CRIT_CHANCE.baseValue ||
            critDamage != StatType.CRIT_DAMAGE.baseValue

        if (!hasAny) return

        sender.sendMessage(i18n.t("commands.stats.category-combat"))
        displayStat(sender, StatType.HEALTH, health)
        displayStat(sender, StatType.DAMAGE, damage)
        displayStat(sender, StatType.STRENGTH, strength)
        displayStat(sender, StatType.DEFENSE, defense)
        displayStat(sender, StatType.CRIT_CHANCE, critChance)
        displayStat(sender, StatType.CRIT_DAMAGE, critDamage)
        sender.sendMessage("")
    }

    private fun displayFarmingStats(sender: CommandSender, stats: PlayerStats) {
        val fortune = stats.getStat(StatType.FARMING_FORTUNE)
        val speed = stats.getStat(StatType.FARMING_SPEED)

        if (speed == StatType.FARMING_SPEED.baseValue && fortune == StatType.FARMING_FORTUNE.baseValue) {
            return
        }

        sender.sendMessage(i18n.t("commands.stats.category-farming"))
        displayStat(sender, StatType.FARMING_FORTUNE, fortune)
        displayStat(sender, StatType.FARMING_SPEED, speed)
        sender.sendMessage("")
    }

    private fun displayForagingStats(sender: CommandSender, stats: PlayerStats) {
        val fortune = stats.getStat(StatType.FORAGING_FORTUNE)
        val speed = stats.getStat(StatType.FORAGING_SPEED)

        if (speed == StatType.FORAGING_SPEED.baseValue && fortune == StatType.FORAGING_FORTUNE.baseValue) {
            return
        }

        sender.sendMessage(i18n.t("commands.stats.category-foraging"))
        displayStat(sender, StatType.FORAGING_FORTUNE, fortune)
        displayStat(sender, StatType.FORAGING_SPEED, speed)
        sender.sendMessage("")
    }

    private fun displayFishingStats(sender: CommandSender, stats: PlayerStats) {
        val speed = stats.getStat(StatType.FISHING_SPEED)
        val seaCreature = stats.getStat(StatType.SEA_CREATURE_CHANCE)

        if (speed == StatType.FISHING_SPEED.baseValue && seaCreature == StatType.SEA_CREATURE_CHANCE.baseValue) {
            return
        }

        sender.sendMessage(i18n.t("commands.stats.category-fishing"))
        displayStat(sender, StatType.FISHING_SPEED, speed)
        displayStat(sender, StatType.SEA_CREATURE_CHANCE, seaCreature)
        sender.sendMessage("")
    }

    private fun displayGeneralStats(sender: CommandSender, stats: PlayerStats) {
        val speed = stats.getStat(StatType.SPEED)
        val magicFind = stats.getStat(StatType.MAGIC_FIND)

        if (speed == StatType.SPEED.baseValue && magicFind == StatType.MAGIC_FIND.baseValue) {
            return
        }

        sender.sendMessage(i18n.t("commands.stats.category-general"))
        displayStat(sender, StatType.SPEED, speed)
        displayStat(sender, StatType.MAGIC_FIND, magicFind)
        sender.sendMessage("")
    }

    private fun displayStat(sender: CommandSender, type: StatType, value: Double) {
        val bonus = value - type.baseValue
        val bonusFormatted = if (bonus > 0) {
            "+${formatNumber(bonus)}"
        } else if (bonus < 0) {
            formatNumber(bonus)
        } else {
            return // No bonus, skip displaying
        }

        val statName = i18n.t(type.i18nKey())
        val line = i18n.t(
            "commands.stats.stat-line",
            "stat" to statName,
            "value" to formatNumber(value),
            "bonus" to bonusFormatted,
        )
        sender.sendMessage(line)
    }

    private fun formatNumber(number: Double): String = if (number == number.toLong().toDouble()) {
        // Whole number
        String.format("%,d", number.toLong())
    } else {
        // Has decimal
        String.format("%,.1f", number)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> {
        // Tab complete player names for /stats <player>
        if (args.size == 1 && sender.hasPermission("purrskills.stats.others")) {
            val prefix = args[0].lowercase()
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.lowercase().startsWith(prefix) }
                .sorted()
        }
        return emptyList()
    }
}
