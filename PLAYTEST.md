# PurrSkills - Manual Playtest Plan

**Version:** 1.0.0
**Date:** 2026-09-12
**Status:** Ready for testing
**Tester:** _____________

---

## Test Environment

- [ ] Paper 1.21.10+ server running
- [ ] PostgreSQL database accessible
- [ ] PurrCore 1.0.0 installed
- [ ] PurrSkills 1.0.0 installed
- [ ] Server starts without errors
- [ ] Database tables created (`player_skills`)

---

## Category 1: XP Gain & Level Progression (10 tests)

### T1.1: Mining XP - Basic
**Priority:** HIGH
**Setup:** Fresh player (Level 0 Mining)
**Action:** Mine 50 Stone blocks
**Expected:**
- Actionbar shows "+1 Mining XP" per block (Stone = 1 XP)
- No level-up yet (need 100 XP for Level 1)
- `/skills` shows "Mining Lv 0 - 50/100 XP (50%)"

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.2: Mining XP - Level Up
**Priority:** HIGH (CRITICAL TEST)
**Setup:** Player with 99 Mining XP (Level 0) - mine 99 Stone first
**Action:** Mine 1 more Stone block (+1 XP = 100 total)
**Expected:**
- Title: "LEVEL UP! Mining → Level 1"
- Subtitle: "Keep grinding to unlock more bonuses!"
- `/skills` shows "Mining Lv 1 - 0/100 XP (0%)" (Level 1→2 also needs 100 XP)
- **No Haste effect yet** (need 200 Mining Speed = Level 100+)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.3: Mining XP - Valuable Ores
**Priority:** HIGH
**Setup:** Fresh player
**Action:** Mine 1 Diamond Ore
**Expected:**
- Actionbar shows "+50 Mining XP" (more than Stone)
- XP scales with ore value

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.4: Farming XP - Crop Maturity
**Priority:** HIGH
**Setup:** Wheat farm (mature crops)
**Action:** Break 1 immature wheat, then 1 mature wheat
**Expected:**
- Immature: **No XP**
- Mature: "+10 Farming XP"
- Only fully grown crops grant XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.5: Combat XP - Mob Types
**Priority:** HIGH
**Setup:** Spawn Zombie, Enderman, Wither Skeleton
**Action:** Kill each mob
**Expected:**
- Zombie: "+10 Combat XP"
- Enderman: "+30 Combat XP"
- Wither Skeleton: "+50 Combat XP"
- Stronger mobs = more XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.6: Fishing XP - Item Types
**Priority:** MEDIUM
**Setup:** Fish in ocean
**Action:** Catch 1 Cod, 1 Treasure (Saddle), 1 Junk (Stick)
**Expected:**
- Cod: "+20 Fishing XP"
- Saddle: "+100 Fishing XP" (treasure)
- Stick: "+5 Fishing XP" (junk)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.7: Foraging XP - Log Types
**Priority:** MEDIUM
**Setup:** Oak tree, Spruce tree
**Action:** Break 5 Oak logs, 5 Spruce logs
**Expected:**
- Each log: "+10 Foraging XP"
- All log types grant XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.8: Creative Mode - No XP
**Priority:** HIGH
**Setup:** Switch to Creative mode
**Action:** Mine Stone, kill Zombie, break Wheat
**Expected:**
- **No XP gained** in Creative
- **No XP gained** in Spectator
- Only Survival/Adventure grant XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.9: Multi-Level Level-Up
**Priority:** MEDIUM
**Setup:** Player at Mining Level 9 with 2400/2500 XP
**Action:** Mine Ancient Debris (+100 XP)
**Expected:**
- Level 9 → 10 instantly
- Correct title/subtitle
- Stats updated for Level 10 (not Level 9)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T1.10: Exponential XP Scaling
**Priority:** LOW
**Setup:** Check required XP for Level 1, 5, 10, 20
**Action:** `/skills` at each level
**Expected:**
- Level 1: 100 XP
- Level 5: 500 XP
- Level 10: 2500 XP
- Level 20: ~15000 XP (check formula: `level * 100 * pow(1.1, level - 1)`)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Category 2: Stats & Gameplay Effects (15 tests)

### T2.1: Mining Speed - Level 1
**Priority:** HIGH
**Setup:** Player at Mining Level 1 (Mining Speed = 101)
**Action:** Mine Stone block
**Expected:**
- No visible speed boost (threshold is 200)
- Breaks at normal speed

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.2: Mining Speed - Level 20 (Haste I)
**Priority:** HIGH
**Setup:** Player at Mining Level 20 (Mining Speed = 120)
**Action:** Mine Stone block
**Expected:**
- Still no Haste (threshold is 200)
- Speed unchanged

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.3: Mining Speed - Level 100 (Haste I)
**Priority:** HIGH
**Setup:** Set Mining Level 100 (+100 speed = 200 total)
**Action:** Mine Stone block
**Expected:**
- **Haste I** effect applied
- Noticeable speed increase
- `/stats` shows "Mining Speed: 200 (+100)"

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.4: Mining Speed - Level 200 (Haste II)
**Priority:** HIGH
**Setup:** Set Mining Level 200 (+200 speed = 300 total)
**Action:** Mine Stone block
**Expected:**
- **Haste II** effect applied
- Much faster mining
- `/stats` shows "Mining Speed: 300 (+200)"

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.5: Combat - Health Bonus
**Priority:** HIGH
**Setup:** Player at Combat Level 10 (+20 Health)
**Action:** Check health bar
**Expected:**
- Max health = 120 HP (100 base + 20 from Combat skill)
- Health bar shows 6 extra hearts
- `/stats` shows "Health: 120 (+20)"

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.6: Combat - Damage Bonus
**Priority:** HIGH
**Setup:** Player at Combat Level 10 (+10 Damage)
**Action:** Hit Zombie with fist (no weapon)
**Expected:**
- Base damage: 1 HP
- With stat: ~11 HP damage
- Zombie takes more damage than vanilla

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.7: Combat - Strength (% Damage)
**Priority:** HIGH
**Setup:** Player at Combat Level 10 (+10 Strength)
**Action:** Hit Zombie with Diamond Sword (7 base damage)
**Expected:**
- Damage = 7 * (1 + 10/100) + 10 (damage stat)
- Formula: base * (1 + strength%) + damage
- Significantly higher than vanilla

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.8: Combat - Defense
**Priority:** HIGH
**Setup:** Player at Combat Level 10 (+0 Defense initially)
**Action:** Take hit from Zombie
**Expected:**
- Damage taken = normal (no defense yet)
- Note: Defense stat requires items/buffs (not from Combat skill)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.9: Combat - Crit Chance
**Priority:** MEDIUM
**Setup:** Player at Combat Level 10 (+0 Crit Chance initially)
**Action:** Hit Zombie 20 times, count crits
**Expected:**
- Base crit chance: 5% (1 in 20)
- Observe yellow particle/sound on crit
- Note: Crit Chance stat requires items/buffs

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.10: Combat - Crit Damage
**Priority:** MEDIUM
**Setup:** Player at Combat Level 10 (+5 Crit Damage)
**Action:** Land critical hit on Zombie
**Expected:**
- Crit multiplier = 150% base + 5% = 155%
- Damage is 1.55x normal hit
- Compare crit vs non-crit damage

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.11: Farming Speed - Haste
**Priority:** MEDIUM
**Setup:** Player at Farming Level 100 (+100 speed = 200 total)
**Action:** Break mature Wheat
**Expected:**
- **Haste I** applied when breaking crops
- Faster breaking animation
- Only applies to farming blocks

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.12: Foraging Speed - Haste
**Priority:** MEDIUM
**Setup:** Player at Foraging Level 100 (+100 speed = 200 total)
**Action:** Break Oak Log
**Expected:**
- **Haste I** applied when breaking logs
- Faster breaking
- Only applies to logs

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.13: Fishing Speed - Wait Time
**Priority:** MEDIUM
**Setup:** Player at Fishing Level 100 (+100 speed = 200 total)
**Action:** Cast fishing rod 5 times, measure wait time
**Expected:**
- Wait time reduced by ~50% (200 speed = 2x multiplier)
- Faster catches than vanilla
- Average wait time: 2-3 seconds (vs 5-6 vanilla)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.14: Stats - Idempotency on Join
**Priority:** HIGH
**Setup:** Player at Mining Level 10 (+10 speed)
**Action:** `/stats`, disconnect, reconnect, `/stats` again
**Expected:**
- Stats before disconnect: "Mining Speed: 110 (+10)"
- Stats after reconnect: "Mining Speed: 110 (+10)"
- **No doubling** (120, 130, etc.)
- Idempotent stat calculation

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T2.15: Stats - Level-Up Refresh
**Priority:** HIGH
**Setup:** Player at Mining Level 9 (Mining Speed: 109)
**Action:** Level up to 10, then `/stats`
**Expected:**
- Mining Speed: 110 (+10)
- Stats updated immediately on level-up
- No delay, no need to relog

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Category 3: Persistence & Database (8 tests)

### T3.1: First Join - Default Skills
**Priority:** HIGH
**Setup:** Fresh player (never joined before)
**Action:** Join server, `/skills`
**Expected:**
- All skills at Level 0, 0 XP
- Power Level: 0
- Database creates new row in `player_skills`

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.2: Disconnect/Reconnect - XP Persists
**Priority:** HIGH
**Setup:** Player with Mining Level 5 (500 XP)
**Action:** Disconnect, reconnect, `/skills`
**Expected:**
- Mining still Level 5, 500 XP
- All skills unchanged
- Data loaded from database

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.3: Server Restart - XP Persists
**Priority:** HIGH
**Setup:** Player with Combat Level 10, stop server
**Action:** Restart server, player rejoins, `/skills`
**Expected:**
- Combat still Level 10
- All XP and levels intact
- No data loss

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.4: Autosave - Periodic Save
**Priority:** MEDIUM
**Setup:** Player gains Mining XP, wait 5 minutes (default autosave interval)
**Action:** Check server logs for "Autosaved X player skills"
**Expected:**
- Log message appears every 5 minutes
- Database updated without disconnect
- No performance impact

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.5: Multiple Players - Isolation
**Priority:** HIGH
**Setup:** Player A at Mining Lv 10, Player B at Mining Lv 5
**Action:** Both do `/skills`
**Expected:**
- Player A sees Level 10
- Player B sees Level 5
- No cross-contamination
- Each player has separate database row

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.6: Database - Manual Verification
**Priority:** LOW
**Setup:** Player at Mining Lv 10
**Action:** Query database: `SELECT * FROM player_skills WHERE skill = 'MINING';`
**Expected:**
- Row exists with correct UUID
- `level` column = 10
- `current_xp` matches in-game value
- UNIQUE constraint on (player_uuid, skill)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.7: Crash During Save - Rollback
**Priority:** LOW
**Setup:** Player gains XP, kill server process during autosave
**Action:** Restart, check if XP is correct
**Expected:**
- Either old XP (before autosave) or new XP (after autosave)
- No corruption (partial data)
- Database transaction rollback works

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T3.8: Concurrent Saves - Thread Safety
**Priority:** MEDIUM
**Setup:** 5 players all gaining XP simultaneously
**Action:** All disconnect at same time
**Expected:**
- All 5 players' data saved correctly
- No race conditions
- No lost data
- ConcurrentHashMap handles concurrency

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Category 4: Commands & GUI (7 tests)

### T4.1: /skills - Own Skills
**Priority:** HIGH
**Setup:** Player with various skill levels
**Action:** `/skills`
**Expected:**
- Shows all 5 skills with levels, XP, progress bars
- Power Level at bottom
- Clean formatting with colors
- No errors

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.2: /skills <player> - View Others
**Priority:** MEDIUM
**Setup:** Player A and Player B online
**Action:** Player A does `/skills PlayerB`
**Expected:**
- Shows Player B's skills
- Header: "PlayerB's Skills"
- Requires permission: `purrskills.skills.others`

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.3: /skills - Permission Denied
**Priority:** MEDIUM
**Setup:** Player without `purrskills.skills.others` permission
**Action:** `/skills OtherPlayer`
**Expected:**
- Error: "You don't have permission to view other players' skills."
- Red color
- No data shown

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.4: /stats - Own Stats
**Priority:** HIGH
**Setup:** Player at Mining Lv 10, Combat Lv 5
**Action:** `/stats`
**Expected:**
- Shows stats grouped by category (Mining, Combat, etc.)
- Format: "Mining Speed: 110 (+10)"
- Only shows categories with non-base stats
- Clean formatting

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.5: /stats <player> - View Others
**Priority:** MEDIUM
**Setup:** Player A and Player B online
**Action:** Player A does `/stats PlayerB`
**Expected:**
- Shows Player B's calculated stats
- Requires permission: `purrskills.stats.others`

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.6: /skillsmenu - GUI Opens
**Priority:** HIGH
**Setup:** Player with various skill levels
**Action:** `/skillsmenu`
**Expected:**
- 27-slot chest GUI opens
- 5 skill icons (Diamond Pickaxe, Hoe, Axe, Sword, Fishing Rod)
- Item lore shows level, XP, progress bar
- Nether Star in bottom-right shows Power Level

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### T4.7: /skillsmenu - Click Protection
**Priority:** MEDIUM
**Setup:** GUI open
**Action:** Try to take item out of GUI
**Expected:**
- Cannot take items
- Click cancelled
- Items stay in GUI
- No item duplication

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Category 5: Exploits & Edge Cases (10 tests)

### E5.1: AFK Mining - No Exploit
**Priority:** HIGH
**Setup:** Player mines same block repeatedly (cobblestone generator)
**Action:** Mine 100 stone blocks in a row
**Expected:**
- XP granted for each block
- No XP cap or diminishing returns
- Valid gameplay (not an exploit)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.2: Hopper Farm - Crop Breaking
**Priority:** HIGH
**Setup:** Automatic wheat farm with hopper collection
**Action:** Trigger crop breaks via hopper/piston
**Expected:**
- **No XP** for automated breaks (player must break)
- Only BlockBreakEvent with player grants XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.3: Mob Farm - AFK Grinding
**Priority:** HIGH
**Setup:** Mob spawner farm, mobs killed by player
**Action:** Kill 50 Zombies from spawner
**Expected:**
- XP granted for each kill
- Valid gameplay (player must hit mob)
- No exploit if player actively kills

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.4: Environmental Kills - No XP
**Priority:** MEDIUM
**Setup:** Mob dies to fall damage, lava, drowning
**Action:** Push mob off cliff, check XP
**Expected:**
- **No Combat XP** for environmental kills
- Only EntityDamageByEntityEvent (player attacker) grants XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.5: Silk Touch - XP Still Granted
**Priority:** LOW
**Setup:** Player mines Diamond Ore with Silk Touch
**Action:** Break Diamond Ore
**Expected:**
- Mining XP still granted (+50 XP)
- Silk Touch doesn't prevent XP

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.6: Fortune Pickaxe - Single XP
**Priority:** LOW
**Setup:** Player mines Diamond Ore with Fortune III (drops 4 diamonds)
**Action:** Break 1 Diamond Ore
**Expected:**
- Mining XP granted once (+50 XP)
- Not 4x XP for 4x drops
- XP based on block, not drops

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.7: Fishing AFK - No Exploit
**Priority:** MEDIUM
**Setup:** Player fishes for 30 minutes straight
**Action:** Catch 50 fish
**Expected:**
- XP granted for each catch
- Fishing Speed reduces wait time (intended)
- No exploit

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.8: Duplicate Stat Modifiers
**Priority:** HIGH
**Setup:** Player at Mining Lv 10, disconnect/reconnect 3 times
**Action:** Check `/stats` after each reconnect
**Expected:**
- Mining Speed always 110 (+10)
- **No stacking** (120, 130, 140...)
- Idempotent refresh on join

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.9: Negative XP - Impossible
**Priority:** LOW
**Setup:** Player at Mining Lv 0 with 0 XP
**Action:** Try to manually set negative XP in database
**Expected:**
- Database constraint prevents negative XP
- Or plugin clamps to 0

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### E5.10: Max Level Cap
**Priority:** LOW
**Setup:** Player at very high level (e.g., Mining Lv 100)
**Action:** Continue mining, check if XP still gained
**Expected:**
- No hard level cap (can level infinitely)
- Or configured max level respected
- Check config: `max-level: -1` (unlimited)

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Category 6: Performance & Stability (5 tests)

### P6.1: Multiple Players - No Lag
**Priority:** HIGH
**Setup:** 5+ players online, all actively gaining XP
**Action:** Monitor server TPS (should stay 20.0)
**Expected:**
- No TPS drops below 19.5
- No lag spikes
- Async database saves don't block main thread

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### P6.2: Rapid XP Gain - No Issues
**Priority:** MEDIUM
**Setup:** Player with high Mining Speed breaks 100 blocks in 10 seconds
**Action:** Spam-mine cobblestone
**Expected:**
- All XP tracked correctly
- No XP loss
- No actionbar spam (debounced)
- No performance impact

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### P6.3: Database Connection - Failover
**Priority:** LOW
**Setup:** Stop PostgreSQL mid-game
**Action:** Player tries to gain XP
**Expected:**
- Plugin catches connection error
- Logs error message (not crash)
- XP cached in memory
- Saved when DB reconnects

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### P6.4: Memory Leak - Long-Running
**Priority:** LOW
**Setup:** Server runs for 24 hours with 10 players
**Action:** Monitor memory usage over time
**Expected:**
- No memory leak
- Heap usage stable
- ConcurrentHashMap cleaned on player quit

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

### P6.5: Plugin Reload - Safe
**Priority:** MEDIUM
**Setup:** Players online with active XP
**Action:** `/reload confirm` or restart plugin
**Expected:**
- All player data saved before unload
- Clean shutdown
- Data intact after reload
- No corruption

**Result:** ⬜ PASS / ⬜ FAIL / ⬜ SKIP
**Notes:** _____________

---

## Summary

**Total Tests:** 50
**Passed:** ___ / 50
**Failed:** ___ / 50
**Skipped:** ___ / 50

### Critical Failures (Must Fix Before Production)
- [ ] T1.2: Level-up not working
- [ ] T2.14: Stats duplicate on reconnect
- [ ] T3.2: XP not persisting
- [ ] E5.8: Stat modifiers stacking
- [ ] P6.1: Server lag with multiple players

### High Priority Fixes
- _____________

### Medium Priority Fixes
- _____________

### Balance Adjustments Needed
- _____________

### Notes
_____________
