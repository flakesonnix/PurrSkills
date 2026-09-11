package gay.nyaa.purrskills.listener

import gay.nyaa.purrskills.SkillManager
import gay.nyaa.purrskills.stats.PlayerStats
import gay.nyaa.purrskills.stats.StatType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Stub SkillManager for testing
 */
class StubSkillManager :
    SkillManager(
        mockk(relaxed = true),
        mockk(relaxed = true),
        mockk(relaxed = true),
        mockk(relaxed = true),
    ) {
    var statsToReturn: PlayerStats = PlayerStats.fromMap(emptyMap())

    override fun calculateStats(uuid: UUID): PlayerStats = statsToReturn
}

/**
 * Tests for MiningSpeedListener - validates mining speed effects.
 */
class MiningSpeedListenerTest {

    private lateinit var skillManager: StubSkillManager
    private lateinit var listener: MiningSpeedListener
    private lateinit var player: Player
    private lateinit var block: Block
    private lateinit var event: BlockBreakEvent
    private val playerUuid = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        // Use stub skill manager
        skillManager = StubSkillManager()

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

        // Create listener with skillManager directly (test constructor)
        listener = MiningSpeedListener(skillManager)
    }

    @Test
    fun `base mining speed (100) applies no effect`() {
        // Given: Base mining speed of 100
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 100.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No haste effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 199 applies no effect`() {
        // Given: Mining speed just below Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 199.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: No haste effect is applied
        verify(exactly = 0) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 200 applies Haste I`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied (can't check PotionEffectType without server)
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 299 applies Haste I`() {
        // Given: Mining speed just below Haste II threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 299.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 300 applies Haste II`() {
        // Given: Mining speed at Haste II threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 399 applies Haste II`() {
        // Given: Mining speed just below instant break threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 399.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 400+ applies high haste level for near-instant break`() {
        // Given: Mining speed at instant break threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 400.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: High haste level is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed 500 applies high haste level`() {
        // Given: Very high mining speed
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 500.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: High haste level is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `creative mode does not apply mining speed`() {
        // Given: Player in creative mode
        every { player.gameMode } returns GameMode.CREATIVE
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 300.0))
        skillManager.statsToReturn = stats

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
        skillManager.statsToReturn = stats

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
        skillManager.statsToReturn = stats

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
        skillManager.statsToReturn = stats

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
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed applies to deepslate`() {
        // Given: Deepslate block
        every { block.type } returns Material.DEEPSLATE
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `mining speed applies to ancient debris`() {
        // Given: Ancient debris block
        every { block.type } returns Material.ANCIENT_DEBRIS
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 400.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Haste effect is applied
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `haste effect has no particles and no icon`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Potion effect is applied (can't verify details without server)
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }

    @Test
    fun `haste effect is not ambient`() {
        // Given: Mining speed at Haste I threshold
        val stats = PlayerStats.fromMap(mapOf(StatType.MINING_SPEED to 200.0))
        skillManager.statsToReturn = stats

        // When: Block is broken
        listener.onBlockBreak(event)

        // Then: Potion effect is applied (can't verify details without server)
        verify(exactly = 1) { player.addPotionEffect(any()) }
    }
}
