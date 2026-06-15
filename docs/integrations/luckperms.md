# LuckPerms Integration

ItemMagnet does not call the LuckPerms API directly. LuckPerms is listed as a soft dependency for unlock gates and permission nodes.

## LP_GROUP unlock type

Gate a tier behind a LuckPerms group:

```yaml
tiers:
  survey:
    unlock:
      type: LP_GROUP
      group: seeker
```

Checks `group.seeker` via Bukkit permissions (LuckPerms standard).

## Permission nodes

All `itemmagnet.*` nodes work with LuckPerms tracks and groups. See [permissions.md](../permissions.md).

## Tested version

LuckPerms **5.5.x** on Paper 1.21.x / 26.x.
