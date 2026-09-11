package gay.nyaa.purrskills.command

import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.gui.SkillMenuGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command executor for /skillsmenu
 * Opens the visual GUI for viewing skills.
 */
class SkillsMenuCommand(
    private val gui: SkillMenuGUI,
    private val i18n: I18n,
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(i18n.t("commands.skills.player-only"))
            return true
        }

        // Open the GUI
        gui.openMenu(sender)
        return true
    }
}
