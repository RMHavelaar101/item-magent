# Lands Integration

ItemMagnet integrates with [Lands](https://www.spigotmc.org/resources/lands.53313/) via reflection. No Lands JAR is bundled.

## Enable

```yaml
integrations:
  lands:
    enabled: true
```

## check-at

| Value | Behavior |
|-------|----------|
| `ITEM` | Check permissions at the dropped item's location |
| `PLAYER` | Check at the player's feet |
| `BOTH` | Both locations must pass |

## wilderness

Controls behavior in **unclaimed** land:

| Value | Behavior |
|-------|----------|
| `ALLOW` | Magnet works in wilderness |
| `DENY` | Magnet blocked in wilderness |
| `PERMISSION` | Requires `wilderness-permission` node |

## claimed-land

Controls behavior in **claimed** land:

| Value | Behavior |
|-------|----------|
| `RESPECT_FLAGS` | Uses Lands `ITEM_PICKUP` role flag |
| `OWNER_ONLY` | Item must be in land the player owns |
| `MEMBER_ONLY` | Item must be in land player owns or is trusted in |
| `DENY` | Never works in claimed land |

## Theryn example (member lands only)

```yaml
integrations:
  lands:
    enabled: true
    check-at: BOTH
    wilderness: DENY
    claimed-land: MEMBER_ONLY
    require-player-in-allowed-land: true
```

## Bypass

Players with `itemmagnet.bypass.lands` skip all Lands checks.
