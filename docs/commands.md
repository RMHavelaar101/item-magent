# Commands

Base command: `/itemmagnet` (aliases: `/im`, `/magnet`)

Running `/itemmagnet` with no arguments shows the full help menu.

## /itemmagnet reload

Reloads `config.yml` and `messages.yml`. Most settings apply immediately.

- **Permission:** `itemmagnet.reload`

## /itemmagnet config

Opens the in-game config editor (see [config-gui.md](config-gui.md)). Edit tier display names, stats, integrations, and more.

- **Permission:** `itemmagnet.config`
- **Player only**

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

## /itemmagnet debug

Shows debug info for your **active** magnet (respects `hold-mode`): tier, slot, charge, boost, base vs effective radius, protection, AFK, world filter, and game mode.

- **Permission:** `itemmagnet.debug`
- **Player only**

## /itemmagnet help

Shows the full command list and fuel transfer tip.

## Tab completion

Subcommands, online player names, tier IDs, and `all` are tab-completed.
