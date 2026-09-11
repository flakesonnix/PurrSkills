package gay.nyaa.purrskills.gui

import com.purrcore.i18n.I18n
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

/**
 * Listener for skill menu GUI interactions.
 * Prevents players from taking items out of the menu.
 */
class SkillMenuListener(
    private val i18n: I18n,
) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val view = event.view
        val title = view.title

        // Check if this is a skills menu
        // Match against the translated title pattern
        val menuTitleKey = i18n.t("gui.skills.title", "player" to "")
        if (!title.contains(menuTitleKey.substringBefore("'").trim())) {
            return // Not a skills menu
        }

        // Cancel all clicks in the skills menu to prevent item theft
        if (event.clickedInventory?.type != InventoryType.PLAYER) {
            event.isCancelled = true
        }
    }
}
