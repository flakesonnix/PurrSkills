# PurrSkills Test Server - Quick Start

## ✅ Plugins Deployed

**Location:** `server/plugins/`
- ✅ purrcore-1.0.0.jar (24 MB)
- ✅ purrskills-1.0.0.jar (4.8 MB)

---

## 🚀 Server Setup Options

### Option 1: Manual Paper Server (Recommended)

```bash
cd server/

# Download Paper 1.21.1
wget -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/latest/downloads/paper-1.21.1-latest.jar

# Accept EULA
echo "eula=true" > eula.txt

# Start server (first time - generates configs)
java -Xmx2G -Xms2G -jar paper.jar nogui
```

**Wait for:** "Done! For help, type help" then stop with `stop`

**Configure Database:** Edit `plugins/PurrCore/config.yml`:
```yaml
database:
  type: postgresql
  host: localhost
  port: 5432
  name: minecraft
  user: minecraft
  password: minecraft_password
```

**Start again:**
```bash
java -Xmx2G -Xms2G -jar paper.jar nogui
```

---

### Option 2: Docker Compose (if Docker available)

```bash
cd /home/lucy/Documents/git/mcplugins/PurrSkills

# Start services
docker compose up -d

# Plugins already in server/plugins/
# Docker will mount ./server as /data

# View logs
docker compose logs -f minecraft

# Stop when done
docker compose down
```

---

### Option 3: Existing Server

If you already have a Paper 1.21+ server running elsewhere:

```bash
# Just copy plugins
cp server/plugins/*.jar /path/to/your/server/plugins/

# Restart your server
```

---

## ⚙️ Configuration

### PurrCore Database Config

**File:** `server/plugins/PurrCore/config.yml`

**PostgreSQL (recommended for testing):**
```yaml
database:
  type: postgresql
  host: localhost  # or 'postgres' if using Docker
  port: 5432
  name: minecraft
  user: minecraft
  password: minecraft_password
  pool:
    max-size: 10
    min-idle: 2
```

**SQLite (simpler, but less realistic):**
```yaml
database:
  type: sqlite
  path: plugins/PurrCore/data.db
```

---

## 🔌 PostgreSQL Setup (if not using Docker)

```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt install postgresql

# Create database & user
sudo -u postgres psql
CREATE DATABASE minecraft;
CREATE USER minecraft WITH PASSWORD 'minecraft_password';
GRANT ALL PRIVILEGES ON DATABASE minecraft TO minecraft;
\q

# Or use existing database with different credentials
```

---

## ✅ Verify Setup

Once server starts, check logs for:
```
[PurrCore] Enabling PurrCore v1.0.0
[PurrCore] Database connected: PostgreSQL
[PurrSkills] Enabling PurrSkills v1.0.0
[PurrSkills] SkillManager initialized
[PurrSkills] StatsManager initialized
[PurrSkills] Registered 5 skill listeners
```

**If errors:** Check `server/logs/latest.log`

---

## 🎮 Connect to Server

**Minecraft Client:** 1.21+
**Server Address:** `localhost:25565`
**Mode:** Offline (no authentication needed)

---

## 🧪 Ready for Critical Tests

Once connected, execute:

1. **T1.2:** Mine 20 Stone blocks → expect "LEVEL UP! Mining → Level 1"
2. **T2.14:** `/skills`, `/stats`, reconnect, check stats unchanged
3. **T3.2:** Gain XP, stop server, restart, verify persistence
4. **E5.8:** Reconnect 3x, verify no stat stacking
5. **P6.1:** Multiple clients (if available)

See **PLAYTEST.md** for detailed test cases.

---

## 🛠️ Troubleshooting

**Server won't start:**
- Check Java version: `java -version` (need Java 21)
- Check Paper JAR downloaded correctly
- EULA accepted? Check `eula.txt`

**Plugins not loading:**
- Check `plugins/` folder has both JARs
- Check logs: `tail -f logs/latest.log`
- PurrCore must load before PurrSkills

**Database errors:**
- PostgreSQL running? `systemctl status postgresql`
- Credentials correct in `plugins/PurrCore/config.yml`?
- Database created? `psql -U minecraft -d minecraft -c "SELECT 1;"`

**Can't connect:**
- Server started? Check console for "Done!"
- Port 25565 open? `netstat -tuln | grep 25565`
- Firewall blocking? `sudo ufw allow 25565`

---

## 📊 Monitoring

**In-game commands:**
```
/skills        # View your skills
/stats         # View your stats
/skillsmenu    # Open GUI
/tps           # Server performance
```

**Console commands:**
```
list           # Online players
tps            # Server TPS
memory         # Memory usage
```

**Database queries:**
```bash
# Connect to PostgreSQL
psql -U minecraft -d minecraft

# View player skills
SELECT * FROM player_skills;

# Check XP values
SELECT skill, level, current_xp FROM player_skills WHERE player_uuid = '<uuid>';
```

---

**Next:** Start server → Connect → Run 5 critical tests → Document in PLAYTEST.md
