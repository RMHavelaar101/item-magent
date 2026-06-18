# Commands

Base command: `/itemmagnet` (shortcut: `/im`, alias: `/magnet`)

Running `/itemmagnet` or `/im` with no arguments shows the full help menu.

## /itemmagnet startup-message [on|off|toggle]

Controls the console thank-you message logged when the plugin enables. The message includes the [ItemMagnet website](https://www.theryn.org/itemmagnet) and a reminder to star the project on GitHub.

- **Permission:** `itemmagnet.admin`
- **Default:** enabled (`startup-message.enabled` in `config.yml`)
- Also toggle from the main config GUI

Examples:

```
/itemmagnet startup-message off
/im startup-message toggle
```

## /itemmagnet reload

Reloads `config.yml` and `messages.yml`. Most settings apply immediately.

- **Permission:** `itemmagnet.reload`

## /itemmagnet config

Opens the in-game config editor (see [config-gui.md](config-gui.md)). Edit tier display names, stats, integrations, and more.

- **Permission:** `itemmagnet.config`
- **Player only**

## /itemmagnet filter

Opens your personal item blacklist GUI. Server rules appear read-only at the top; your choices and tag rules are editable below. Use the preset button to merge built-in filter presets — you'll see a preview/confirm screen before rules are applied.

- **Permission:** `itemmagnet.filter` (default: all players with `itemmagnet.use`)
- **Player only**
- **Help:** `/itemmagnet filter help` or `/itemmagnet help filter`

### /itemmagnet filter clear

Removes all of your personal filter materials and tags. Server blacklist rules and tier filters are unchanged. Your “first filter hint” flag is preserved.

- **Permission:** `itemmagnet.filter`

```
/itemmagnet filter clear
```

## /itemmagnet import \<blacklist|filter|filter-preset\> …

Bulk-merge materials or presets.

| Subcommand | Permission | Description |
|------------|------------|-------------|
| `blacklist <mat1,mat2,…>` | `itemmagnet.import` | Merge into server `settings.item-blacklist` |
| `filter <mat1,mat2,…>` | `itemmagnet.filter` | Merge into your personal filter |
| `filter-preset <name>` | `itemmagnet.filter` | Merge a named preset (`mining`, `farming`, …) |

Examples:

```
/itemmagnet import blacklist DIRT,COBBLESTONE
/itemmagnet import filter WHEAT_SEEDS,CARROT
/itemmagnet import filter-preset mining
```

## /itemmagnet version

Shows plugin version, Paper version, and hook availability.

- **Permission:** `itemmagnet.admin`

## /itemmagnet give \<player\> \<tier\|all\> [charge]

Gives a magnet item to a player.

- **Permission:** `itemmagnet.give`
- **Tiers:** `fragment`, `survey`, `anchor`, or `all`
- **charge:** Optional starting charge (default: half of max)

Examples:

```
/itemmagnet give Steve fragment 1000
/itemmagnet give Steve all 500
```

## /itemmagnet giveall \<player\> [charge]

Gives all tier resonators at once.

- **Permission:** `itemmagnet.give`

Example:

```
/itemmagnet giveall Steve 500
```

## /itemmagnet unlock \<player\> \<tier\|all\>

Unlocks a tier recipe for a player.

- **Permission:** `itemmagnet.unlock`

Example:

```
/itemmagnet unlock Steve all
```

## /itemmagnet unlockall \<player\>

Unlocks every configured tier recipe for a player.

- **Permission:** `itemmagnet.unlock`

## /itemmagnet debug [nearby]

Shows debug info for your **active** magnet (respects `hold-mode`): tier, slot, charge, boost, base vs effective radius, protection, AFK, world filter, game mode, and filter state (server/personal rule counts, tier filter mode).

Add `nearby` to scan up to 5 nearest dropped items and print each material with pull status (`OK` or a `PullBlockReason` name).

- **Permission:** `itemmagnet.debug`
- **Player only**

## /itemmagnet help

Shows the full command list and fuel transfer tip.

## Tab completion

Subcommands, online player names, tier IDs, and `all` are tab-completed.
