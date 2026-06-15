# CMI Unlock Gates

ItemMagnet supports CMI-based unlock conditions via reflection. CMI is optional.

**Tested against:** CMI **9.8.7.x** + CMILib **1.5.9.3** on Paper 26.x (see [compatibility.md](../compatibility.md)).

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

Or from another plugin (ItemMagnet **v1.3.0+**):

```java
ItemMagnetApi.grantUnlock(player, "fragment");
ItemMagnetApi.giveMagnet(player, "fragment", -1); // -1 = half max charge
```

## LP_GROUP

Requires LuckPerms (or any plugin that exposes `group.<name>` permissions):

```yaml
unlock:
  type: LP_GROUP
  group: seeker
```

Checks `player.hasPermission("group.seeker")`.

See [luckperms.md](luckperms.md).

## MCMMO_SKILL

Requires mcMMO:

```yaml
unlock:
  type: MCMMO_SKILL
  skill: MINING
  level: 100
```

See [mcmmo.md](mcmmo.md).

## CMI event command example

In CMI `eventCommands.yml`:

```yaml
- "itemmagnet unlock [playerName] fragment"
```

Run this when a player completes your custom achievement or milestone.

## Recipe discovery

When a player meets the unlock condition (or is granted via command), ItemMagnet calls `player.discoverRecipe()` so the recipe appears in the recipe book.
