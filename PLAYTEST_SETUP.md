# PurrSkills Test Server Setup

Quick setup for playtesting PurrSkills on a local Paper server with PostgreSQL.

## Prerequisites

- Docker & Docker Compose installed
- Minecraft Java client (1.21+)

## Setup Steps

### 1. Build Plugin JARs

```bash
# Build PurrCore
cd ../PurrCore
./gradlew build
cd ../PurrSkills

# Build PurrSkills
./gradlew build

# Verify JARs exist
ls -lh build/libs/purrskills-1.0.0.jar
ls -lh ../PurrCore/build/libs/purrcore-1.0.0.jar
```

### 2. Start Test Server

```bash
# Start PostgreSQL + Paper server
docker-compose up -d

# View logs
docker-compose logs -f minecraft

# Wait for "Done! For help, type help"
```

### 3. Copy Plugins

```bash
# Create plugins directory if not exists
mkdir -p ./server/plugins

# Copy JARs
cp ../PurrCore/build/libs/purrcore-1.0.0.jar ./server/plugins/
cp build/libs/purrskills-1.0.0.jar ./server/plugins/

# Restart server to load plugins
docker-compose restart minecraft
```

### 4. Configure Database

The server/plugins/PurrCore/config.yml should be auto-created with defaults:

```yaml
database:
  host: postgres  # Docker service name
  port: 5432
  name: minecraft
  user: minecraft
  password: minecraft_password
  pool:
    max-size: 10
    min-idle: 2
```

**Note:** If PurrCore doesn't connect, check `server/logs/latest.log` for errors.

### 5. Connect to Server

- Minecraft version: **1.21+**
- Server address: **localhost:25565**
- Join as any username (offline mode enabled for testing)

## Test Server Commands

### Server Console

```bash
# Attach to server console
docker attach purrskills-minecraft-1

# Detach: Ctrl+P, Ctrl+Q (don't use Ctrl+C, it stops server!)

# Stop server gracefully
docker-compose exec minecraft rcon-cli stop

# Or force stop
docker-compose down
```

### Grant OP Permissions

```bash
# From host
docker-compose exec minecraft rcon-cli "op YourUsername"

# Now you have access to all commands in-game
```

### Useful In-Game Commands

```bash
/gamemode creative
/gamemode survival
/time set day
/weather clear
/tp 0 100 0

# PurrSkills commands
/skills
/stats
/skillsmenu

# Check other player's skills (requires OP)
/skills PlayerName
/stats PlayerName
```

## Database Access

### PostgreSQL Client

```bash
# Connect to database
docker-compose exec postgres psql -U minecraft -d minecraft

# View player skills
SELECT * FROM player_skills;

# Count total players
SELECT COUNT(DISTINCT player_uuid) FROM player_skills;

# Top Mining players
SELECT player_uuid, level, current_xp
FROM player_skills
WHERE skill = 'MINING'
ORDER BY level DESC, current_xp DESC
LIMIT 10;

# Exit psql
\q
```

### Manual Data Manipulation (for testing)

```sql
-- Set player's Mining level to 100
UPDATE player_skills
SET level = 100, current_xp = 0
WHERE player_uuid = '<uuid>' AND skill = 'MINING';

-- Reset all skills for a player
DELETE FROM player_skills WHERE player_uuid = '<uuid>';

-- Grant max levels to all skills
UPDATE player_skills SET level = 100, current_xp = 0;
```

**Note:** After manual DB changes, player must reconnect to see updates.

## Testing Checklist

Follow the test plan in **PLAYTEST.md**:

```bash
# View test plan
cat PLAYTEST.md

# Or open in editor
nano PLAYTEST.md
```

**Recommended workflow:**
1. Start with Category 1 (XP Gain & Level Progression)
2. Test Category 2 (Stats & Gameplay Effects) - requires high levels, use DB to set levels
3. Test Category 3 (Persistence) - requires reconnects
4. Test Category 4 (Commands & GUI)
5. Test Category 5 (Exploits) - critical for production
6. Test Category 6 (Performance) - requires multiple clients

## Troubleshooting

### Plugin Not Loading

```bash
# Check logs
docker-compose logs minecraft | grep -i "purrskills\|purrcore"

# Common issues:
# - PurrCore not loaded first (dependency missing)
# - Database connection failed (check postgres is running)
# - Java version mismatch (Paper requires Java 21)
```

### Database Connection Failed

```bash
# Check postgres is running
docker-compose ps

# Check postgres logs
docker-compose logs postgres

# Test connection manually
docker-compose exec postgres psql -U minecraft -d minecraft -c "SELECT 1;"
```

### Stats Not Updating

```bash
# Check if stats system is initialized
/stats

# If error, check logs:
docker-compose logs minecraft | grep -i "stats\|skill"

# Try reconnecting (stats refresh on join)
```

### Performance Issues

```bash
# Monitor server TPS
/tps

# Check memory usage
docker stats purrskills-minecraft-1

# View Java heap
docker-compose exec minecraft rcon-cli "memory"
```

## Clean Slate (Reset Everything)

```bash
# Stop and remove containers + volumes
docker-compose down -v

# Remove server files
rm -rf ./server/

# Rebuild and restart
docker-compose up -d
```

## Next Steps After Testing

1. **Document Results:** Fill out PLAYTEST.md with PASS/FAIL for each test
2. **Balance Tweaks:** Create BALANCE.md with XP/stat adjustments needed
3. **Bug Fixes:** Open issues for any critical failures
4. **Integration Tests:** After gameplay validated, add MockBukkit for 12 tests
5. **Production Deploy:** Only after all critical tests pass

---

**Happy Testing!** 🎮
