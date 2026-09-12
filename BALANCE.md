# PurrSkills - Balance Tracking

**Purpose:** Track balance issues found during playtesting and document adjustments.

---

## Current Balance (Pre-Playtest)

### XP Values

#### Mining
- Stone: 5 XP
- Coal Ore: 10 XP
- Iron Ore: 20 XP
- Gold Ore: 30 XP
- Diamond Ore: 50 XP
- Ancient Debris: 100 XP
- **Balance:** Unknown (not tested)

#### Farming
- Wheat/Carrot/Potato (mature): 10 XP
- Beetroot (mature): 10 XP
- Melon/Pumpkin: 5 XP
- **Balance:** Unknown

#### Foraging
- Oak/Spruce/Birch/etc. Log: 10 XP per log
- **Balance:** Unknown

#### Combat
- Zombie/Skeleton/Creeper: 10 XP
- Spider/Cave Spider: 15 XP
- Enderman: 30 XP
- Blaze/Wither Skeleton: 50 XP
- **Balance:** Unknown

#### Fishing
- Fish (Cod/Salmon): 20 XP
- Treasure (Saddle/Bow): 100 XP
- Junk (Stick/Leather): 5 XP
- **Balance:** Unknown

### Level Scaling

**Formula:** `requiredXP = level * 100 * pow(1.1, level - 1)`

| Level | Required XP | Cumulative XP |
|-------|-------------|---------------|
| 1 | 100 | 100 |
| 5 | 500 | ~2,000 |
| 10 | 2,500 | ~12,000 |
| 20 | 15,000 | ~150,000 |
| 50 | ~1,000,000 | ~50,000,000 |
| 100 | ~1,000,000,000 | ??? |

**Balance:** Unknown (exponential may be too steep)

### Stat Bonuses

#### Mining
- Per Level: +1 Speed, +0.5 Fortune
- Level 10: +10 Speed, +5 Fortune
- Level 100: +100 Speed, +50 Fortune
- **Balance:** Unknown

#### Farming
- Per Level: +1 Fortune, +1 Speed
- Level 10: +10 Fortune, +10 Speed
- **Balance:** Unknown

#### Foraging
- Per Level: +1 Fortune, +1 Speed
- Level 10: +10 Fortune, +10 Speed
- **Balance:** Unknown

#### Combat
- Per Level: +2 Health, +1 Damage, +1 Strength, +0.5 Crit Damage
- Level 10: +20 Health, +10 Damage, +10 Strength, +5 Crit Damage
- Level 100: +200 Health, +100 Damage, +100 Strength, +50 Crit Damage
- **Balance:** Unknown (likely OP at high levels)

#### Fishing
- Per Level: +1 Speed, +0.5 Sea Creature Chance
- Level 10: +10 Speed, +5 Sea Creature Chance
- **Balance:** Unknown

### Speed Effect Thresholds

- 100-199: No effect
- 200-299: Haste I
- 300-399: Haste II
- 400+: Haste III+ (very fast)

**Balance:** Unknown (may need tuning)

---

## Playtest Findings

### Issues Found

#### XP Balance

**Issue ID:** B-001
**Category:** XP
**Description:** _____________
**Severity:** 🔴 Critical / 🟡 High / 🟢 Medium / ⚪ Low
**Recommendation:** _____________

---

**Issue ID:** B-002
**Category:** XP
**Description:** _____________
**Severity:** 🔴 Critical / 🟡 High / 🟢 Medium / ⚪ Low
**Recommendation:** _____________

---

#### Stat Balance

**Issue ID:** B-003
**Category:** Stats
**Description:** _____________
**Severity:** 🔴 Critical / 🟡 High / 🟢 Medium / ⚪ Low
**Recommendation:** _____________

---

#### Exploits

**Issue ID:** B-004
**Category:** Exploit
**Description:** _____________
**Severity:** 🔴 Critical / 🟡 High / 🟢 Medium / ⚪ Low
**Recommendation:** _____________

---

#### Gameplay Feel

**Issue ID:** B-005
**Category:** Feel
**Description:** _____________
**Severity:** 🔴 Critical / 🟡 High / 🟢 Medium / ⚪ Low
**Recommendation:** _____________

---

## Proposed Adjustments

### XP Adjustments

| Source | Current XP | Proposed XP | Reason |
|--------|------------|-------------|--------|
| Stone Mining | 5 | ___ | _______________ |
| Diamond Ore | 50 | ___ | _______________ |
| Wheat (mature) | 10 | ___ | _______________ |
| Zombie Kill | 10 | ___ | _______________ |
| Fishing (Cod) | 20 | ___ | _______________ |

### Level Scaling Adjustments

| Change | Current | Proposed | Reason |
|--------|---------|----------|--------|
| Formula | `level * 100 * 1.1^(level-1)` | ___ | _______________ |
| Level 10 Req | 2,500 XP | ___ | Too easy/hard? |
| Level 20 Req | 15,000 XP | ___ | Too easy/hard? |

### Stat Bonus Adjustments

| Skill | Stat | Current/Level | Proposed/Level | Reason |
|-------|------|---------------|----------------|--------|
| Mining | Speed | +1 | ___ | _______________ |
| Mining | Fortune | +0.5 | ___ | _______________ |
| Combat | Health | +2 | ___ | Too OP at high levels? |
| Combat | Damage | +1 | ___ | _______________ |
| Combat | Strength | +1 | ___ | _______________ |

### Speed Threshold Adjustments

| Threshold | Current | Proposed | Reason |
|-----------|---------|----------|--------|
| Haste I | 200 | ___ | Too easy/hard to reach? |
| Haste II | 300 | ___ | _______________ |
| Haste III+ | 400 | ___ | _______________ |

---

## Implementation Priority

### Critical (Must Fix Before Launch)
- [ ] **B-XXX:** _____________
- [ ] **B-XXX:** _____________

### High Priority (Fix Soon)
- [ ] **B-XXX:** _____________
- [ ] **B-XXX:** _____________

### Medium Priority (Nice to Have)
- [ ] **B-XXX:** _____________
- [ ] **B-XXX:** _____________

### Low Priority (Future Updates)
- [ ] **B-XXX:** _____________
- [ ] **B-XXX:** _____________

---

## Testing Notes

### Playtime to Level 10 (Estimated)

| Skill | Actions Needed | Time Estimate | Actual Time |
|-------|----------------|---------------|-------------|
| Mining | ~1,200 Stone (12k XP) | ~20 min | ___ |
| Farming | ~1,200 Wheat (12k XP) | ~30 min | ___ |
| Foraging | ~1,200 Logs (12k XP) | ~40 min | ___ |
| Combat | ~1,200 Zombies (12k XP) | ~2 hours | ___ |
| Fishing | ~600 Fish (12k XP) | ~3 hours | ___ |

**Goal:** Level 10 should take 1-3 hours of focused play per skill.

### Playtime to Level 20 (Estimated)

| Skill | Actions Needed | Time Estimate | Actual Time |
|-------|----------------|---------------|-------------|
| Mining | ~15,000 Stone (150k XP) | ~5 hours | ___ |
| Combat | ~15,000 Zombies (150k XP) | ~25 hours | ___ |
| Fishing | ~7,500 Fish (150k XP) | ~50 hours | ___ |

**Goal:** Level 20 should take ~10-20 hours of dedicated grinding.

---

## Player Feedback

### Positive Feedback
- _____________

### Negative Feedback
- _____________

### Feature Requests
- _____________

---

## Changelog (Balance Updates)

### v1.0.1 (Post-Playtest)
- **TBD:** Adjustments based on playtest findings

### v1.0.0 (Initial Release)
- Initial balance values (untested)
