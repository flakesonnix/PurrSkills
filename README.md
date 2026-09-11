# PurrSkills

MCMMO-style skills system for Paper 1.21+ — level up through gameplay and unlock passive bonuses.

## Features

- **5 Core Skills**: Mining, Foraging, Farming, Combat, Fishing
- **XP & Leveling**: Gain XP from actions, level up for rewards
- **Passive Bonuses**: Double drop chances, damage boosts, etc.
- **Persistent Storage**: HikariCP database (SQLite/MySQL/PostgreSQL)
- **Actionbar Notifications**: Real-time XP gains
- **Leaderboards**: Compare skills with other players

## Skills

| Skill     | Level Up By         | Passive Bonus Example        |
|-----------|---------------------|------------------------------|
| Mining    | Breaking ores/stone | 20% double drops at level 20 |
| Foraging  | Chopping trees      | 20% double logs at level 20  |
| Farming   | Harvesting crops    | 20% double crops at level 20 |
| Combat    | Killing mobs        | +20% damage at level 20      |
| Fishing   | Catching fish       | 20% faster fishing at level 20|

## Commands

- `/skills` - View your skill levels
- `/skills <player>` - View another player's skills (requires permission)
- `/skills top [skill]` - View leaderboards

## Installation

1. Download `purrskills-1.0.0.jar` from [Releases](https://github.com/flakesonnix/PurrSkills/releases)
2. Place in `plugins/` folder
3. Restart server
4. Configure in `plugins/PurrSkills/config.yml`

## Requirements

- Paper 1.21+ or Spigot 1.21+
- Java 21

## Building

```bash
nix develop
gradle shadowJar
# → build/libs/purrskills-1.0.0.jar
```

Or without Nix:

```bash
./gradlew shadowJar
```

## Development

Built with:
- **Kotlin 2.0.21**
- **Paper 1.21.10 API**
- **Java 21**
- **HikariCP** for database pooling
- **JUnit5 + MockK** for testing

See `docs/MCMMO.md` for complete specification and XP values.

## Configuration

```yaml
database:
  type: sqlite  # or mysql/postgresql
  sqlite:
    file: skills.db

skills:
  enabled-skills:
    - mining
    - foraging
    - farming
    - combat
    - fishing
  show-xp-actionbar: true
  show-levelup-title: true
```

## XP Examples

| Action              |   XP |
|---------------------|-----:|
| Stone               |    1 |
| Coal Ore            |    5 |
| Diamond Ore         |   50 |
| Ancient Debris      |  100 |
| Oak Log             |    5 |
| Wheat (mature)      |    2 |
| Zombie Kill         |   10 |
| Ender Dragon Kill   | 5000 |
| Cod Caught          |   10 |

See `docs/MCMMO.md` for complete XP tables.

## Level Progression

| Level | XP Required | Level | XP Required |
|------:|------------:|------:|------------:|
|     1 |         100 |    11 |       3,000 |
|     2 |         150 |    12 |       3,700 |
|     3 |         250 |    13 |       4,500 |
|     4 |         400 |    14 |       5,400 |
|     5 |         600 |    15 |       6,400 |
|     6 |         850 |    16 |       7,500 |
|     7 |       1,150 |    17 |       8,700 |
|     8 |       1,500 |    18 |      10,000 |
|     9 |       1,900 |    19 |      11,500 |
|    10 |       2,400 |    20 |      13,000 |

## License

MIT License - see [LICENSE](LICENSE)
