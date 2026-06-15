# Pull-blocked bridges (Quests & CMI)

ItemMagnet fires `ItemMagnetPullBlockedEvent` when a pull is denied (player filter, server blacklist, protection, inventory full, etc.). Optional config rules can award Quests or CMI progress when specific block reasons occur.

Both bridges default **off**. Rule editing is YAML-only in v1.6 (Integrations GUI shows toggles/docs links).

## Matching

A rule fires when:

1. The block **reason** is listed in `reasons`, and
2. `materials` is empty **or** the dropped item's material is in the list

Progress is throttled to **once per second** per player, rule, and material to avoid spam.

## Quests progress

Requires PikaMug Quests with an active **custom objective** on the configured quest.

```yaml
integrations:
  quests:
    enabled: true
    unlock-on-complete: {}
    progress-on-blocked:
      enabled: true
      rules:
        filtered_iron:
          quest-id: iron_collector
          reasons: [PLAYER_BLACKLIST]
          materials: [IRON_ORE, DEEPSLATE_IRON_ORE]
          amount: 1
```

When a matching block occurs, ItemMagnet increments the first active custom objective on that quest for the player.

## CMI stats

Requires CMI. Increments a configured stat by `amount` (default `1`).

```yaml
integrations:
  cmi:
    progress-on-blocked:
      enabled: true
      rules:
        filtered_drops:
          stat: blocksmined
          sub-stat: unknown
          amount: 1
          reasons: [PLAYER_BLACKLIST, TIER_BLACKLIST]
          materials: []
```

Empty `materials` matches any material for the listed reasons.

## API

Other plugins can listen to `ItemMagnetPullBlockedEvent` directly — see [api.md](../api.md).

## Tested versions

- Quests **5.2.9**
- CMI (reflection-based stat API)
