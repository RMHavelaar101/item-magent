# Quests Integration (PikaMug)

Optional bridge: grant magnet tier unlocks when players complete Quests.

## Enable

```yaml
integrations:
  quests:
    enabled: true
    unlock-on-complete:
      my_first_quest: fragment
      mining_milestone: survey
```

When a player completes `my_first_quest`, ItemMagnet runs `grantUnlock` for tier `fragment`.

## Requirements

- [Quests](https://modrinth.com/plugin/quests.classic) (PikaMug) installed
- Quest IDs must match your `quests.yml` entries exactly

## Default

Disabled with empty `unlock-on-complete` — safe for servers using command-based unlocks (e.g. TherynFragments milestones).

## Tested version

Quests **5.2.9** on Paper 1.21.x / 26.x.
