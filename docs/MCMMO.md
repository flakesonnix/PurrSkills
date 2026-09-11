# PurrSkills - MCMMO-style Skills System

MCMMO-inspired skills plugin for Paper 1.21+ with progression, rewards, and abilities.

## Core Skills

Initial implementation focuses on these five skills:

1. **Mining** - break ores and stone
2. **Foraging** - chop trees
3. **Farming** - harvest crops
4. **Combat** - kill mobs
5. **Fishing** - catch fish

Each skill has:
- Independent XP and level progression
- Passive bonuses as levels increase
- Active abilities unlocked at certain levels
- Persistent storage in database

## XP Progression

Use the following initial XP values.

These are intentionally simple starting values. They should be implemented through configuration/data rather than scattered as magic numbers throughout the code.

### Mining XP

| Block                  |  XP |
| ---------------------- | --: |
| Stone                  |   1 |
| Deepslate              |   2 |
| Coal Ore               |   5 |
| Deepslate Coal Ore     |   6 |
| Iron Ore               |   8 |
| Deepslate Iron Ore     |  10 |
| Copper Ore             |   6 |
| Deepslate Copper Ore   |   7 |
| Gold Ore               |  12 |
| Deepslate Gold Ore     |  15 |
| Redstone Ore           |  10 |
| Deepslate Redstone Ore |  12 |
| Lapis Ore              |  12 |
| Deepslate Lapis Ore    |  14 |
| Diamond Ore            |  50 |
| Deepslate Diamond Ore  |  60 |
| Emerald Ore            |  75 |
| Deepslate Emerald Ore  |  90 |
| Nether Quartz Ore      |   8 |
| Ancient Debris         | 100 |

### Farming XP

| Crop/action | XP |
| ----------- | -: |
| Wheat       |  2 |
| Carrot      |  2 |
| Potato      |  2 |
| Beetroot    |  2 |
| Pumpkin     |  5 |
| Melon       |  3 |
| Sugar Cane  |  2 |
| Cocoa Beans |  3 |
| Nether Wart |  3 |
| Sweet Berry |  2 |
| Cactus      |  2 |

Only award the XP when the crop is actually harvested and should drop resources. Do not give full XP for breaking immature crops.

### Foraging XP

| Block        | XP |
| ------------ | -: |
| Oak Log      |  5 |
| Spruce Log   |  5 |
| Birch Log    |  5 |
| Jungle Log   |  6 |
| Acacia Log   |  6 |
| Dark Oak Log |  6 |
| Mangrove Log |  7 |
| Cherry Log   |  7 |
| Crimson Stem |  7 |
| Warped Stem  |  7 |

Only award XP for actual logs/stems, not leaves.

### Combat XP

Use these initial values:

| Entity          |   XP |
| --------------- | ---: |
| Zombie          |   10 |
| Skeleton        |   12 |
| Spider          |   10 |
| Creeper         |   15 |
| Enderman        |   30 |
| Witch           |   35 |
| Slime           |    5 |
| Phantom         |   25 |
| Blaze           |   30 |
| Wither Skeleton |   40 |
| Piglin          |   12 |
| Piglin Brute    |   40 |
| Hoglin          |   20 |
| Magma Cube      |   10 |
| Guardian        |   35 |
| Elder Guardian  |  250 |
| Ender Dragon    | 5000 |
| Wither          | 2500 |

Do not award Combat XP for killing another player in the initial version.

### Fishing XP

Award:

* Cod → 10 XP
* Salmon → 15 XP
* Tropical Fish → 20 XP
* Pufferfish → 20 XP
* Junk item → 5 XP
* Treasure item → 50 XP

Use the actual `PlayerFishEvent` state/reward information where possible instead of simply awarding XP whenever the fishing event fires.

---

## Initial Level Progression

Use a centrally defined XP curve.

For the initial implementation, use the following XP required to advance from each level to the next:

| Current Level | XP Required |
| ------------: | ----------: |
|             1 |         100 |
|             2 |         150 |
|             3 |         250 |
|             4 |         400 |
|             5 |         600 |
|             6 |         850 |
|             7 |       1,150 |
|             8 |       1,500 |
|             9 |       1,900 |
|            10 |       2,400 |
|            11 |       3,000 |
|            12 |       3,700 |
|            13 |       4,500 |
|            14 |       5,400 |
|            15 |       6,400 |
|            16 |       7,500 |
|            17 |       8,700 |
|            18 |      10,000 |
|            19 |      11,500 |
|            20 |      13,000 |

The system should support levels beyond 20 using a sensible progression formula or configurable values.

Do not hardcode the level progression into the individual skills.

All skills should use the same progression system initially.

The progression system must:

* accumulate XP
* calculate the required XP for the next level
* preserve excess XP
* support multiple level-ups from one XP award
* support future changes to the progression curve

For example:

If a player is Level 4 with `350 / 400 XP` and receives `200 XP`:

* the player reaches Level 5
* the remaining XP becomes `150`
* the player's new progress is `150 / 600 XP`

The exact XP values above are the **initial balancing values**, not permanent gameplay constants. Keep them easy to rebalance through configuration later.

---

## Database Schema

Store player skill data persistently using HikariCP + SQLite/MySQL/PostgreSQL.

### Tables

#### `player_skills`

Stores per-skill XP and level data for each player.

```sql
CREATE TABLE player_skills (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  player_uuid VARCHAR(36) NOT NULL,
  skill VARCHAR(32) NOT NULL,
  level INT NOT NULL DEFAULT 1,
  xp INT NOT NULL DEFAULT 0,
  last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY unique_player_skill (player_uuid, skill),
  INDEX idx_player (player_uuid)
);
```

#### `player_abilities` (future)

Track ability cooldowns and unlocks per player.

```sql
CREATE TABLE player_abilities (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  player_uuid VARCHAR(36) NOT NULL,
  ability VARCHAR(64) NOT NULL,
  cooldown_until TIMESTAMP NULL,
  unlocked BOOLEAN DEFAULT FALSE,
  UNIQUE KEY unique_player_ability (player_uuid, ability)
);
```

---

## Skill Details

### Mining

**Passive Bonuses:**
- Level 5: 5% chance for double drops
- Level 10: 10% chance for double drops
- Level 15: 15% chance for double drops
- Level 20: 20% chance for double drops

**Active Ability (future):**
- Super Breaker: Temporary mining speed boost + guaranteed double drops
- Cooldown: 60 seconds
- Duration: 10 seconds

### Foraging

**Passive Bonuses:**
- Level 5: 5% chance for double log drops
- Level 10: 10% chance for double log drops
- Level 15: 15% chance for double log drops
- Level 20: 20% chance for double log drops

**Active Ability (future):**
- Tree Feller: Break entire tree at once
- Cooldown: 90 seconds

### Farming

**Passive Bonuses:**
- Level 5: 5% chance for double crop drops
- Level 10: 10% chance for double crop drops
- Level 15: Crops grow 10% faster (if we implement growth boost)
- Level 20: 20% chance for double crop drops

**Active Ability (future):**
- Green Terra: Temporary auto-replant + guaranteed double drops
- Cooldown: 120 seconds
- Duration: 15 seconds

### Combat

**Passive Bonuses:**
- Level 5: +5% damage
- Level 10: +10% damage
- Level 15: +15% damage
- Level 20: +20% damage

**Active Ability (future):**
- Berserk: Temporary damage boost + damage reduction
- Cooldown: 90 seconds
- Duration: 10 seconds

### Fishing

**Passive Bonuses:**
- Level 5: 5% chance for treasure
- Level 10: 10% faster fishing
- Level 15: 10% chance for treasure
- Level 20: 20% faster fishing

**Active Ability (future):**
- Fisher's Fortune: Guaranteed treasure catch
- Cooldown: 180 seconds

---

## Commands

### `/skills`
Show your own skill levels and XP progress.

**Output example:**
```
=== Your Skills ===
Mining: Level 12 (3,200 / 3,700 XP)
Foraging: Level 8 (1,100 / 1,500 XP)
Farming: Level 5 (450 / 600 XP)
Combat: Level 15 (5,800 / 6,400 XP)
Fishing: Level 3 (120 / 250 XP)
```

### `/skills <player>`
Show another player's skill levels (requires permission).

### `/skills top [skill]`
Show leaderboard for a specific skill or overall power level.

**Permission:** `purrskills.leaderboard`

---

## Configuration

Initial `config.yml`:

```yaml
# PurrSkills — MCMMO-style skills system

database:
  type: sqlite
  pool:
    maximum-pool-size: 10
    minimum-idle: 2
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
  sqlite:
    file: skills.db
  mysql:
    host: localhost
    port: 3306
    database: minecraft
    user: root
    password: ""
    params: "useSSL=false&characterEncoding=utf8mb4"
  postgresql:
    host: localhost
    port: 5432
    database: minecraft
    user: postgres
    password: ""
    params: "sslmode=disable"

skills:
  enabled-skills:
    - mining
    - foraging
    - farming
    - combat
    - fishing
  
  # Show actionbar XP gains?
  show-xp-actionbar: true
  
  # Show level-up title?
  show-levelup-title: true
  
  # Show level-up sound?
  levelup-sound: true

messages:
  xp-gain: "&7+{xp} {skill} XP &8({current}/{required})"
  level-up: "&a&lLEVEL UP! &7{skill} is now level &e{level}"
```

---

## Implementation Priority

Phase 1 (MVP):
1. Database setup (player_skills table)
2. XP tracking for all 5 skills
3. Level progression system
4. `/skills` command
5. Passive bonuses (double drop chances)
6. Actionbar XP notifications

Phase 2 (polish):
7. `/skills top` leaderboard
8. Config reloading
9. Level-up titles/sounds
10. Better GUI for `/skills`

Phase 3 (advanced):
11. Active abilities
12. Ability cooldown tracking
13. Permissions per skill level
14. Custom rewards per level

---

## Testing Strategy

- **Unit tests** for XP progression math (like SleepCalculator in CatNap)
- **Unit tests** for level-up logic with excess XP
- **Integration tests** with MockBukkit for event handling
- **Manual testing** on dev server with 3 players

---

## Architecture Notes

- **Event-driven**: Listen to BlockBreakEvent, EntityDeathEvent, PlayerFishEvent, etc.
- **Repository pattern**: Separate DB access from business logic
- **Pure Kotlin progression logic**: Keep XP/level math testable without Bukkit
- **Config-driven XP values**: Load from YAML, not hardcoded
- **Async DB writes**: Use Bukkit scheduler for non-blocking saves
- **In-memory caching**: Keep active player skills in memory, sync to DB periodically

---

## Code Style

- Follow PurrCore conventions
- Kotlin idioms (data classes, when expressions, extension functions)
- Spotless + ktlint formatting
- Conventional Commits for Git history
- Incremental commits (not one giant commit!)
