# Phase 2 Playtest - Current Status

## ✅ Preparation Complete

**Documentation:**
- ✅ PLAYTEST.md (50 test cases, 831 lines)
- ✅ PLAYTEST_SETUP.md (setup guide, 764 lines)
- ✅ BALANCE.md (tracking template, 582 lines)
- ✅ TESTING.md (unit/integration split)

**Build Artifacts:**
- ✅ purrskills-1.0.0.jar (104 KB)
- ✅ purrcore-1.0.0.jar (24 MB) - dependency ready
- ✅ 220/220 unit tests passing

**Docker Setup:**
- ✅ docker-compose.yml created (PostgreSQL + Paper server)
- ⚠️ Docker not available in current shell (Nix environment)

---

## ⚠️ Docker Limitation

**Issue:** `docker` command not found in current environment

**Possible causes:**
1. Docker not installed on system
2. Docker daemon not running
3. Nix shell doesn't have Docker in PATH
4. User not in `docker` group

**Solutions:**

### Option A: Manual Docker Setup (if Docker installed)
```bash
# Exit OpenCode, start Docker manually
sudo systemctl start docker
docker compose up -d

# Or use your system's Docker tooling
```

### Option B: Use Existing Minecraft Server
If you have a Paper 1.21+ server running locally:
```bash
# Copy plugins
cp /home/lucy/Documents/git/mcplugins/PurrCore/build/libs/purrcore-1.0.0.jar /path/to/server/plugins/
cp /home/lucy/Documents/git/mcplugins/PurrSkills/build/libs/purrskills-1.0.0.jar /path/to/server/plugins/

# Configure PurrCore database (server/plugins/PurrCore/config.yml)
database:
  type: postgresql
  host: localhost
  port: 5432
  name: minecraft
  user: minecraft
  password: minecraft_password
```

### Option C: Local Paper Server (No Docker)
```bash
# Download Paper
wget https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/latest/downloads/paper-1.21.1-latest.jar

# Setup directory
mkdir -p ~/minecraft-test-server/plugins
cd ~/minecraft-test-server

# Copy plugins
cp /home/lucy/Documents/git/mcplugins/PurrCore/build/libs/purrcore-1.0.0.jar plugins/
cp /home/lucy/Documents/git/mcplugins/PurrSkills/build/libs/purrskills-1.0.0.jar plugins/

# Start server (accept EULA first)
echo "eula=true" > eula.txt
java -Xmx2G -Xms2G -jar paper-1.21.1-latest.jar nogui
```

---

## 🎯 Critical Tests Ready

Once server is running, execute these 5 tests first:

### T1.2: Level-Up Mechanism
1. Join server as fresh player
2. Gain 95 Mining XP (mine 19 Stone blocks)
3. Mine 1 more Stone (+5 XP = 100 total)
4. **Expect:** Title "LEVEL UP! Mining → Level 1", stats updated

### T2.14: Stats Idempotency
1. Reach Mining Level 10 (+10 speed = 110 total)
2. `/stats` → verify "Mining Speed: 110 (+10)"
3. Disconnect, reconnect
4. `/stats` again
5. **Expect:** Still 110 (not 120, 130, etc.)

### T3.2: XP Persistence
1. Gain Mining Level 5 (500 XP)
2. `/skills` → verify Level 5
3. Stop server completely
4. Restart server, rejoin
5. `/skills` → **Expect:** Still Level 5, data persisted

### E5.8: No Stat Stacking
1. Mining Level 10 (Mining Speed: 110)
2. Disconnect/reconnect 3 times
3. Check `/stats` after each reconnect
4. **Expect:** Always 110, no increase

### P6.1: Multi-Player Performance
1. Connect 5+ players (or spawn 5 fake players via plugin)
2. All players mine/farm/fight simultaneously
3. Monitor `/tps`
4. **Expect:** TPS stays >19.5, no lag

---

## 📊 Handoff to Manual Testing

**Status:** Code frozen, ready for gameplay validation

**Next Actions (Manual):**
1. Start Minecraft server (Docker or local)
2. Configure PostgreSQL connection (if using DB)
3. Copy plugins to `plugins/` folder
4. Restart server
5. Connect with Minecraft 1.21+ client
6. Execute 5 critical tests
7. Document results in PLAYTEST.md

**If critical tests pass:** Continue with remaining 45 tests
**If critical tests fail:** Stop, document in BALANCE.md, fix bugs, re-test

---

## 🛑 Blocker

**Cannot proceed with automated testing** due to Docker unavailability.

**Resolution required:** User must manually start server environment.

---

**Current commit:** `docker-compose.yml` created but not tested
**Files ready:** All JARs built, docs complete, test plan ready
**Waiting on:** Server startup (manual action required)
