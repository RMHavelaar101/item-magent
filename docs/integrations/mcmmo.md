# mcMMO Integration

Gate tier recipes behind mcMMO skill levels.

## MCMMO_SKILL unlock type

```yaml
tiers:
  survey:
    unlock:
      type: MCMMO_SKILL
      skill: MINING
      amount: 100
```

`skill` must match a `PrimarySkillType` enum name (e.g. `MINING`, `EXCAVATION`, `HERBALISM`).

## Requirements

- mcMMO plugin installed
- Player must meet or exceed `amount` (skill level)

## Tested version

mcMMO **2.2.x** on Paper 26.1.

See [mcMMO API](https://wiki.mcmmo.org/en/api/mcmmo-api).
