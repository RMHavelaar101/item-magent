# WorldGuard Integration

ItemMagnet integrates with [WorldGuard](https://enginehub.org/worldguard) via reflection.

## Enable

```yaml
integrations:
  worldguard:
    enabled: true
```

## item-pickup flag

When `respect-item-pickup-flag: true`, the magnet respects WorldGuard's `item-pickup` flag at the checked location.

## Region modes

| Mode | Behavior |
|------|----------|
| `NONE` | No region list filtering (flag checks only) |
| `WHITELIST` | Magnet only works inside listed regions |
| `BLACKLIST` | Magnet blocked inside listed regions |

## Global regions

```yaml
integrations:
  worldguard:
    enabled: true
    region-mode: BLACKLIST
    regions:
      - spawn
      - archive_landing
```

## Per-world overrides

```yaml
integrations:
  worldguard:
    worlds:
      world_nether:
        region-mode: WHITELIST
        regions:
          - nether_farm
```

## Bypass

Players with `itemmagnet.bypass.regions` skip region list checks (not the item-pickup flag).
