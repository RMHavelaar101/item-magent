# ItemMagnet

**Tiered item magnets with redstone fuel, claim-aware protection, visible pull physics, and an in-game config editor.**

> **Paper 1.21.1+** · **Java 21+** · Soft deps: Lands, WorldGuard, Towny, GriefPrevention, PlaceholderAPI, CMI (all optional)

<!-- Replace with your hosted demo GIF once captured -->
<!-- ![Demo](https://raw.githubusercontent.com/RMHavelaar101/item-magent/main/media/demo/item-magnet-demo.gif) -->

## Why ItemMagnet?

Most magnet plugins teleport items through walls or ignore land claims. ItemMagnet is built for **survival SMPs**:

- **Visible physics** — line-of-sight pull; items slide around corners
- **Claim-aware** — Lands, WorldGuard, Towny, GriefPrevention
- **Redstone fuel** — dust recharge, block power surge, live lore with radius
- **Admin-friendly** — `/itemmagnet config` GUI, presets, hot reload

## Features

### Gameplay

- Three configurable tiers (Fragment, Survey, Anchor)
- Hold modes: main hand, hotbar, or inventory
- XP orb pulling (optional)
- Redstone dust fuel + redstone block power surge
- Sneak + right-click recharge (either hand) or auto-absorb drops
- Per-fuel recharge sounds; pull, depleted, and denied cues
- Live lore: charge, boost, and current pull radius

### Protection & integrations

- **Lands** — wilderness, owner, member, flag modes
- **WorldGuard** — region whitelist/blacklist, item-pickup flag
- **Towny** — town plot protection
- **GriefPrevention** — claim respect
- **PlaceholderAPI** — charge, radius, tier, boost
- **CMI** — stat/rank unlock gates

### Admin tools

- `/itemmagnet config` — in-game editor for settings, tiers, fuel, integrations
- Rename tier display names from the GUI
- Unlock gates: permission, advancement, CMI, or command (persisted)
- Anti-AFK with one-time notify
- World blacklist/whitelist filter
- Presets: `theryn`, `testing`, or custom

### Developer API

Cancellable events: pull, fuel absorb, deplete, XP pull. See [API docs](https://github.com/RMHavelaar101/item-magent/blob/main/docs/api.md).

## Compatibility

| | |
|---|---|
| **Server** | Paper **1.21.1+** (not Spigot) |
| **Java** | **21+** |
| **Tested** | Paper 1.21.1, 1.21.4, 26.1 |
| **Folia** | Not supported |

## Quick start

1. Download and place `ItemMagnet-1.2.1.jar` in `plugins/`
2. Restart server
3. `/itemmagnet give <player> fragment 500`
4. Sneak + right-click with redstone in either hand to fuel
5. Hold magnet near dropped items — they pull with visible motion

Optional: `/itemmagnet config` to open the in-game editor.

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/itemmagnet` | — | Help (permission-filtered) |
| `/itemmagnet reload` | `itemmagnet.reload` | Reload config |
| `/itemmagnet config` | `itemmagnet.config` | In-game config GUI |
| `/itemmagnet version` | `itemmagnet.admin` | Version and hooks |
| `/itemmagnet give <player> <tier\|all> [charge]` | `itemmagnet.give` | Give resonator |
| `/itemmagnet giveall <player> [charge]` | `itemmagnet.give` | Give all tiers |
| `/itemmagnet unlock <player> <tier\|all>` | `itemmagnet.unlock` | Unlock recipe |
| `/itemmagnet unlockall <player>` | `itemmagnet.unlock` | Unlock all |
| `/itemmagnet debug` | `itemmagnet.debug` | Active magnet stats |

Aliases: `/im`, `/magnet`

## Permissions

- `itemmagnet.use` — use magnets (default: true)
- `itemmagnet.use.<tier>` — per-tier use
- `itemmagnet.wilderness` — Lands/Towny wilderness (default: op)
- `itemmagnet.admin` — admin commands (default: op)
- `itemmagnet.bypass.lands` / `itemmagnet.bypass.regions` — skip protection checks

[Full permission list](https://github.com/RMHavelaar101/item-magent/blob/main/docs/permissions.md)

## Documentation

- [Installation](https://github.com/RMHavelaar101/item-magent/blob/main/docs/installation.md)
- [Configuration](https://github.com/RMHavelaar101/item-magent/blob/main/docs/configuration.md)
- [Config GUI](https://github.com/RMHavelaar101/item-magent/blob/main/docs/config-gui.md)
- [Integrations](https://github.com/RMHavelaar101/item-magent/tree/main/docs/integrations)
- [FAQ](https://github.com/RMHavelaar101/item-magent/blob/main/docs/faq.md)
- [Version compatibility](https://github.com/RMHavelaar101/item-magent/blob/main/docs/marketplace/compatibility.md)

## Support

- **Product site:** https://itemmagnet.theryn.org (docs, download links, support form)
- Report issues: https://github.com/RMHavelaar101/item-magent/issues

Include Paper version, Java version, and steps to reproduce.

## Changelog

**1.2.1** — Config GUI tier renaming; metrics removed from GUI menu  
**1.2.0** — Config GUI, live radius lore, fuel sounds, AFK notify-once, permission-filtered help  
**1.1.1** — XP pull, giveall/unlockall, fuel transfer fixes  
**1.1.0** — Paper 1.21.1+, hold modes, Towny/GP/PAPI, developer API

Full changelog: [CHANGELOG.md](https://github.com/RMHavelaar101/item-magent/blob/main/CHANGELOG.md)
