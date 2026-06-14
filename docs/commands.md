# Commands

Base command: `/itemmagnet` (aliases: `/im`, `/magnet`)

## /itemmagnet reload

Reloads `config.yml` and `messages.yml`.

- **Permission:** `itemmagnet.reload`

## /itemmagnet version

Shows plugin version, Paper version, and hook availability (Lands, WorldGuard, CMI).

- **Permission:** `itemmagnet.admin`

## /itemmagnet give \<player\> \<tier\> [charge]

Gives a magnet item to a player.

- **Permission:** `itemmagnet.give`
- **Tiers:** `fragment`, `survey`, `anchor`
- **charge:** Optional starting charge (default: half of max)

Example:

```
/itemmagnet give Steve fragment 1000
```

## /itemmagnet unlock \<player\> \<tier\>

Unlocks a tier recipe for a player (for `COMMAND` unlock type or manual grants).

- **Permission:** `itemmagnet.unlock`

Example:

```
/itemmagnet unlock Steve fragment
```

## /itemmagnet debug

Shows debug info for the magnet in your main hand: tier, charge, boost, effective radius, protection status, AFK state.

- **Permission:** `itemmagnet.debug`
- **Player only**

## /itemmagnet help

Shows command usage.

## Tab completion

Subcommands, online player names, and tier IDs are tab-completed.
