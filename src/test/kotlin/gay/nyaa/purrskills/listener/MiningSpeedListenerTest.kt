package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.PurrSkillsPlugin
import gay.nyaa.purrskills.SkillManager
import gay.nyaa.purrskills.stats.PlayerStats
import gay.nyaa.purrskills.stats.StatType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

/**
 * Tests for MiningSpeedListener - validates mining speed effects.
 */
class MiningSpeedListenerTest {

    private lateinit var plugin: PurrSkillsPlugin
    private lateinit var skillManager: SkillManager
    private lateinit var listener: MiningSpeedListener
    private lateinit var player: Player
    private lateinit var block: Block
    private lateinit var event: BlockBreakEvent
    private val playerUuid = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        // Mock plugin and dependencies
        plugin = mockk<PurrSkillsPlugin>(relaxed = true)
        skillManager = mockk<SkillManager>(relaxed = true)
        every { plugin.skillManager } returns skillManager

        // Mock player
        player = mockk<Player>(relaxed = true)
        every { player.uniqueId } returns playerUuid
        every { player.gameMode } returns GameMode.SURVIVAL

        // Mock block
        block = mockk<Block>(relaxed = true)
        every { block.type } returns Material.STONE

        // Mock event
        event = mockk<BlockBreakEvent>(relaxed = true)
        every { event.player } returns player
        every { event.block } returns block
        every { event.isCancelled } returns false

        // Create listener
        listener = MiningSpeedListener(plugin)
    }

    @Test
    fun `base mining speed (100) applies no effect`() {
        // Given: Base mining speed of 100
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 100.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No haste effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 199 applies no effect`() {
        // Given: Mining speed just below Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 199.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No haste effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 200 applies Haste I`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste I effect is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 0 && // Haste I = amplifier 0
                        effect.duration == 40
                },
            )
        }
    }

    @Test
    fun `mining speed 299 applies Haste I`() {
        // Given: Mining speed just below Haste II threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 299.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste I effect is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 0
                },
            )
        }
    }

    @Test
    fun `mining speed 300 applies Haste II`() {
        // Given: Mining speed at Haste II threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste II effect is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 1 && // Haste II = amplifier 1
                        effect.duration == 40
                },
            )
        }
    }

    @Test
    fun `mining speed 399 applies Haste II`() {
        // Given: Mining speed just below instant break threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 399.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste II effect is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 1
                },
            )
        }
    }

    @Test
    fun `mining speed 400+ applies high haste level for near-instant break`() {
        // Given: Mining speed at instant break threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 400.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Very high haste level is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 9 // Level 10 = amplifier 9
                },
            )
        }
    }

    @Test
    fun `mining speed 500 applies high haste level`() {
        // Given: Very high mining speed
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 500.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Very high haste level is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 9
                },
            )
        }
    }

    @Test
    fun `creative mode does not apply mining speed`() {
        // Given: Player in creative mode
        every { player.gameMode } returns GameMode.CREATIVE
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `spectator mode does not apply mining speed`() {
        // Given: Player in spectator mode
        every { player.gameMode } returns GameMode.SPECTATOR
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `non-mining blocks do not apply mining speed`() {
        // Given: Non-mining block (dirt)
        every { block.type } returns Material.DIRT
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `cancelled event does not apply mining speed`() {
        // Given: Event is cancelled
        every { event.isCancelled } returns true
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed applies to diamond ore`() {
        // Given: Diamond ore block
        every { block.type } returns Material.DIAMOND_ORE
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste II is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 1
                },
            )
        }
    }

    @Test
    fun `mining speed applies to deepslate`() {
        // Given: Deepslate block
        every { block.type } returns Material.DEEPSLATE
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste I is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 0
                },
            )
        }
    }

    @Test
    fun `mining speed applies to ancient debris`() {
        // Given: Ancient debris block
        every { block.type } returns Material.ANCIENT_DEBRIS
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 400.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: High haste level is applied
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        effect.amplifier == 9
                },
            )
        }
    }

    @Test
    fun `haste effect has no particles and no icon`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect has no particles and no icon
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        !effect.hasParticles() &&
                        !effect.hasIcon()
                },
            )
        }
    }

    @Test
    fun `haste effect is not ambient`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        every { skillManager.calculateStats(playerUuid) } returns stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is not ambient
        verify(exactly = 1) {
            player.addPotionEffect(
                match { effect ->
                    effect.type == PotionEffectType.HASTE &&
                        !effect.isAmbient
                },
            )
        }
    }
}
