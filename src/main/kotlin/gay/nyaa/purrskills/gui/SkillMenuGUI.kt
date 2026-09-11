package gay.nyaa.purrskills.gui

import com.purrcore.i18n.I18n
import gay.nyaa.purrskills.SkillManager
import gay.nyaa.purrskills.skill.Skill
import gay.nyaa.purrskills.skill.SkillProgression
import gay.nyaa.purrskills.stats.SkillRewardCalculator
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Chest GUI for viewing skills.
 * Shows all 5 skills with icons, levels, XP progress, and stat rewards.
 */
class SkillMenuGUI(
    private val skillManager: SkillManager,
    private val i18n: I18n,
) {

    /**
     * Open the skills menu for a player.
     *
     * @param player The player to show the menu to
     */
    fun openMenu(player: Player) {
        val inventory = createInventory(player)
        player.openInventory(inventory)
    }

    /**
     * Create the inventory for the skills menu.
     *
     * Layout:
     * ```
     * [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * [ ] [M] [ ] [Fa] [ ] [Fo] [ ] [C] [ ]
     * [ ] [ ] [ ] [ ] [Fi] [ ] [ ] [ ] [ ]
     * ```
     *
     * M = Mining, Fa = Farming, Fo = Foraging, C = Combat, Fi = Fishing
     */
    private fun createInventory(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, 27, i18n.t("gui.skills.title", "player" to player.name))

        val skills = skillManager.getPlayerSkills(player)

        // Position each skill item in the inventory
        // Row 2 (slots 9-17): Mining, Farming, Foraging, Combat
        // Row 3 (slots 18-26): Fishing
        val positions = mapOf(
            Skill.MINING to 10,
            Skill.FARMING to 12,
            Skill.FORAGING to 14,
            Skill.COMBAT to 16,
            Skill.FISHING to 22,
        )

        for ((skill, slot) in positions) {
            val profile = skills.getSkill(skill)
            val item = createSkillItem(skill, profile.level, profile.xp)
            inventory.setItem(slot, item)
        }

        // Add power level display at bottom right
        val powerLevel = skills.powerLevel()
        val powerLevelItem = createPowerLevelItem(powerLevel)
        inventory.setItem(26, powerLevelItem)

        return inventory
    }

    /**
     * Create an ItemStack for a skill.
     *
     * @param skill The skill
     * @param level Current level
     * @param xp Current XP
     * @return ItemStack with appropriate icon and lore
     */
    private fun createSkillItem(skill: Skill, level: Int, xp: Int): ItemStack {
        val material = getSkillMaterial(skill)
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta ?: return item

        // Display name
        val skillName = i18n.t("skills.${skill.displayName()}")
        meta.setDisplayName(i18n.t("gui.skills.skill-name", "skill" to skillName, "level" to level.toString()))

        // Lore: XP progress + stats
        val lore = mutableListOf<String>()

        // XP progress bar
        val requiredXp = if (level < 50) {
            SkillProgression.xpRequiredForLevel(level + 1)
        } else {
            0 // Max level
        }

        if (requiredXp > 0) {
            val percentage = (xp.toDouble() / requiredXp * 100).toInt()
            lore.add(i18n.t("gui.skills.xp-progress", "xp" to formatNumber(xp), "required" to formatNumber(requiredXp)))
            lore.add(createProgressBar(percentage))
            lore.add("")
        } else {
            lore.add(i18n.t("gui.skills.max-level"))
            lore.add("")
        }

        // Stats granted by this skill
        lore.add(i18n.t("gui.skills.rewards-header"))
        val rewards = SkillRewardCalculator.calculateRewards(skill, level)
        for (modifier in rewards) {
            val statName = i18n.t(modifier.stat.i18nKey())
            val value = if (modifier.value % 1.0 == 0.0) {
                modifier.value.toInt().toString()
            } else {
                String.format("%.1f", modifier.value)
            }
            lore.add(i18n.t("gui.skills.stat-line", "stat" to statName, "value" to value))
        }

        meta.lore = lore
        item.itemMeta = meta
        return item
    }

    /**
     * Create the power level display item.
     */
    private fun createPowerLevelItem(powerLevel: Int): ItemStack {
        val item = ItemStack(Material.NETHER_STAR)
        val meta: ItemMeta = item.itemMeta ?: return item

        meta.setDisplayName(i18n.t("gui.skills.power-level-name"))
        meta.lore = listOf(
            i18n.t("gui.skills.power-level-value", "level" to powerLevel.toString()),
        )

        item.itemMeta = meta
        return item
    }

    /**
     * Get the material icon for a skill.
     */
    private fun getSkillMaterial(skill: Skill): Material = when (skill) {
        Skill.MINING -> Material.DIAMOND_PICKAXE
        Skill.FARMING -> Material.DIAMOND_HOE
        Skill.FORAGING -> Material.DIAMOND_AXE
        Skill.COMBAT -> Material.DIAMOND_SWORD
        Skill.FISHING -> Material.FISHING_ROD
    }

    /**
     * Create a progress bar visualization.
     * Example: "[||||||||--] 80%"
     */
    private fun createProgressBar(percentage: Int): String {
        val bars = 10
        val filled = (percentage / 10).coerceIn(0, bars)
        val empty = bars - filled

        return i18n.t(
            "gui.skills.progress-bar",
            "filled" to "|".repeat(filled),
            "empty" to "-".repeat(empty),
            "percent" to percentage.toString(),
        )
    }

    /**
     * Format number with thousands separator.
     * 1234 -> "1,234"
     */
    private fun formatNumber(number: Int): String = String.format("%,d", number)
}
