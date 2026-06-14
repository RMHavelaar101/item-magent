# CMI Unlock Gates

ItemMagnet supports CMI-based unlock conditions via reflection. CMI is optional.

## Unlock types

Configure per tier under `tiers.<id>.unlock`:

### NONE

Always craftable (default for tier 1 on generic servers).

```yaml
unlock:
  type: NONE
```

### PERMISSION

```yaml
unlock:
  type: PERMISSION
  permission: itemmagnet.craft.survey
```

### ADVANCEMENT

Requires a completed Bukkit advancement (datapack or plugin):

```yaml
unlock:
  type: ADVANCEMENT
  advancement: itemmagnet:unlock_survey
```

### CMI_STAT

Requires a CMI statistic threshold:

```yaml
unlock:
  type: CMI_STAT
  stat: blocksmined
  sub: DEEPSLATE
  amount: 5000
```

### CMI_RANK

Requires CMI rank permission:

```yaml
unlock:
  type: CMI_RANK
  rank: Seeker
```

Checks `cmi.rank.Seeker` permission.

### COMMAND

Recipe hidden until an admin or automation grants unlock:

```yaml
unlock:
  type: COMMAND
```

Grant via:

```
/itemmagnet unlock PlayerName fragment
```

## CMI event command example

In CMI `eventCommands.yml`:

```yaml
- "itemmagnet unlock [playerName] fragment"
```

Run this when a player completes your custom achievement or milestone.

## Recipe discovery

When a player meets the unlock condition (or is granted via command), ItemMagnet calls `player.discoverRecipe()` so the recipe appears in the recipe book.
