# SuperiorSkyblock2 Integration

ItemMagnet integrates with [SuperiorSkyblock2](https://hangar.papermc.io/BG-Software/SuperiorSkyblock2) via reflection.

## Enable

```yaml
integrations:
  superiorskyblock:
    enabled: true
    wilderness: ALLOW
    claimed-land: RESPECT_FLAGS
    island-permission: PICKUP_ITEMS
```

## claimed-land

| Value | Behavior |
|-------|----------|
| `RESPECT_FLAGS` | Checks `island-permission` for the player |
| `OWNER_ONLY` | Player must be island owner |
| `MEMBER_ONLY` | Player must be island member |
| `DENY` | Never pull on islands |

## Bypass

`itemmagnet.bypass.superiorskyblock`

## island-permission

Default `PICKUP_ITEMS`. Override if your SSB2 config uses a different permission key.
