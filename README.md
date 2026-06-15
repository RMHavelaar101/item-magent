# ItemMagnet

**Tiered item magnets with redstone fuel, smart filters, visible pull physics, and claim-aware protection.**

[![Paper 1.21.1+](https://img.shields.io/badge/Paper-1.21.1%2B-blue)](https://papermc.io/)
[![Java 21+](https://img.shields.io/badge/Java-21%2B-orange)](https://adoptium.net/)
[![Version](https://img.shields.io/badge/Version-1.6.1-brightgreen)](CHANGELOG.md)
[![License MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Pull dropped loot toward you with magnets that feel physical — items slide around corners and stop at walls. Charge them with redstone, respect Lands and WorldGuard claims, filter what you collect, and tune everything from an admin GUI or `config.yml`.

**Website:** [itemmagnet.theryn.org](https://itemmagnet.theryn.org) · **Hangar:** [ItemMagnets](https://hangar.papermc.io/Alcerious/ItemMagnets)

Listing copy for Hangar, Spigot, and Modrinth lives in [`marketing/`](marketing/).

---

## Why ItemMagnet?

Most magnet plugins teleport items through blocks or ignore land claims. ItemMagnet is built for **survival SMPs** that care about fairness and polish:

- **Visible physics** — step-based pull with line-of-sight; no phasing through walls
- **Claim-aware** — Lands, WorldGuard, Towny, GriefPrevention, Residence, PlotSquared, SuperiorSkyblock2
- **Smart filters** — server + personal blacklists, Minecraft tag rules, tier whitelists, presets with preview/confirm
- **Redstone fuel loop** — dust and blocks recharge the magnet; blocks trigger a radius boost
- **Admin-friendly** — `/itemmagnet config` GUI, presets, hot reload, LuckPerms-ready permissions, audit log
- **Production storage** — player filters in YAML, SQLite, or MySQL

---

## Features

### Gameplay

| Feature | Description |
|---------|-------------|
| **Three tiers** | Fragment, Survey, Anchor — custom materials, names, radius, drain, recipes |
| **Hold modes** | `MAIN_HAND`, `HOTBAR`, or `INVENTORY` (passive magnet in your pack) |
| **XP orbs** | Optional experience orb pulling at the same radius |
| **Fuel system** | Sneak + right-click (either hand) or auto-absorb redstone drops |
| **Power surge** | Redstone blocks add charge + temporary radius boost |
| **Live lore** | Charge, boost, and **current pull radius** on the item tooltip |
| **Sounds** | Per-fuel recharge audio; pull, depleted, and denied cues |

### Filters

| Feature | Description |
|---------|-------------|
| **Server blacklist** | Materials + tag rules in config and admin GUI |
| **Personal GUI** | `/itemmagnet filter` — server rules read-only, personal materials/tags editable |
| **Presets** | `mining`, `farming`, `mob-drops`, `keep-valuables` with merge preview |
| **Clear & import** | `/itemmagnet filter clear`, `/itemmagnet import …` |
| **Storage** | YAML (default), SQLite, or MySQL for player filter data |

### Server admin

| Feature | Description |
|---------|-------------|
| **Config GUI** | `/itemmagnet config` — settings, tiers, fuel, integrations, item filter |
| **Rename items** | Change tier display names from the GUI (chat input, `&` colors) |
| **Presets** | `theryn`, `skyblock`, `vanilla-survival`, `hub-spawn`, `testing`, or custom |
| **World filter** | Disable magnets in hub/spawn worlds |
| **Anti-AFK** | Optional movement check; one-time notify (no chat spam) |
| **Unlock gates** | Permission, advancement, CMI stat/rank, LuckPerms group, mcMMO skill, Quests, admin command |
| **Proximity lore** | Optional coordinate zones — ambient messages when holding a magnet (default off) |
| **Audit log** | `plugins/ItemMagnet/config-audit.log` for config and filter changes |

### Integrations (all optional)

- **Protection** — Lands, WorldGuard, Towny, GriefPrevention, Residence, PlotSquared, SuperiorSkyblock2
- **Progression** — CMI stats/ranks, LuckPerms groups, mcMMO skills, Quests quest-complete unlocks
- **Bridges** — Quests/CMI progress on `ItemMagnetPullBlockedEvent` (config-driven, default off)
- **PlaceholderAPI** — charge, radius, tier, boost, filter counts
- **Developer API** — cancellable pull/fuel/XP events, `ItemMagnetPullBlockedEvent`, `grantUnlock`, `giveMagnet`

---

## Compatibility

| | Requirement |
|---|-------------|
| **Server** | [Paper](https://papermc.io/) **1.21.1 or newer** |
| **Java** | **21 or newer** (run `java -version` on your host) |
| **Tested on** | Paper 1.21.1, 1.21.4, 26.1 |
| **Not supported** | Spigot, CraftBukkit, Folia |

Paper is required — the plugin targets the Paper API and modern event handling.

Full matrix: [docs/compatibility.md](docs/compatibility.md)

---

## Download

Get the latest JAR from [GitHub Releases](https://github.com/RMHavelaar101/item-magent/releases) or [Hangar](https://hangar.papermc.io/Alcerious/ItemMagnets).

1. Place `ItemMagnet-1.6.1.jar` in your server's `plugins/` folder
2. Restart the server
3. Give yourself a magnet:

   ```
   /itemmagnet give YourName fragment 500
   ```

4. Hold the magnet; put **redstone dust** in your other hand; **sneak + right-click** to fuel
5. Drop items nearby — they pull toward you with visible motion

Optional: `/itemmagnet filter` for personal filters, `/itemmagnet config` for the admin editor.

More detail: [docs/quick-start.md](docs/quick-start.md)

---

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/itemmagnet` | — | Show help (filtered by permission) |
| `/itemmagnet reload` | `itemmagnet.reload` | Hot-reload config and messages |
| `/itemmagnet config` | `itemmagnet.config` | In-game config editor |
| `/itemmagnet filter` | `itemmagnet.filter` | Personal item filter GUI |
| `/itemmagnet filter clear` | `itemmagnet.filter` | Clear personal filter rules |
| `/itemmagnet import …` | varies | Bulk merge blacklist/filter/preset |
| `/itemmagnet give <player> <tier\|all> [charge]` | `itemmagnet.give` | Give a resonator |
| `/itemmagnet unlock <player> <tier\|all>` | `itemmagnet.unlock` | Unlock a recipe |
| `/itemmagnet debug` | `itemmagnet.debug` | Stats for your active magnet |
| `/itemmagnet version` | `itemmagnet.admin` | Version and hook status |

Aliases: `/im`, `/magnet`

Full reference: [docs/commands.md](docs/commands.md)

---

## Permissions (essentials)

| Node | Default | Purpose |
|------|---------|---------|
| `itemmagnet.use` | `true` | Use magnets |
| `itemmagnet.filter` | `true` | Personal filter GUI and import |
| `itemmagnet.use.<tier>` | `true` | Per-tier use (fragment, survey, anchor) |
| `itemmagnet.wilderness` | `op` | Magnet use in unclaimed Lands/Towny wilderness |
| `itemmagnet.admin` | `op` | Admin commands (reload, give, config, …) |
| `itemmagnet.bypass.lands` | `false` | Skip Lands checks |
| `itemmagnet.bypass.regions` | `false` | Skip WorldGuard region lists |

Full list: [docs/permissions.md](docs/permissions.md)

---

## Configuration

On first run the plugin creates `plugins/ItemMagnet/config.yml` and `messages.yml`.

Key sections:

- **tiers** — materials, display names, lore, radius, recipes, unlocks, whitelists/blacklists/tags
- **fuel** — redstone dust vs block charge, boost, per-fuel sounds
- **integrations** — protection plugins, Quests/CMI bridges
- **player-filter** — default preset, storage backend (YAML/SQLite/MySQL)
- **settings** — hold-mode, item blacklist/tags, inventory-full behavior, XP pull
- **presets** — `preset: none | theryn | skyblock | …`

Edit via file or `/itemmagnet config`. Most changes hot-reload with `/itemmagnet reload`.

Reference: [docs/configuration.md](docs/configuration.md) · [Config GUI guide](docs/config-gui.md)

---

## Documentation

| Topic | Link |
|-------|------|
| Install & requirements | [installation.md](docs/installation.md) |
| Quick start | [quick-start.md](docs/quick-start.md) |
| Version compatibility | [compatibility.md](docs/compatibility.md) |
| Configuration | [configuration.md](docs/configuration.md) |
| Config GUI | [config-gui.md](docs/config-gui.md) |
| Commands | [commands.md](docs/commands.md) |
| Permissions | [permissions.md](docs/permissions.md) |
| Developer API | [api.md](docs/api.md) |
| Pull-blocked bridges | [pull-blocked-bridges.md](docs/integrations/pull-blocked-bridges.md) |
| FAQ | [faq.md](docs/faq.md) |
| Release notes | [CHANGELOG.md](CHANGELOG.md) |
| Listing copy | [marketing/](marketing/) |

Integration guides: [Lands](docs/integrations/lands.md) · [WorldGuard](docs/integrations/worldguard.md) · [Quests](docs/integrations/quests.md) · [PlaceholderAPI](docs/integrations/placeholderapi.md)

---

## Building from source

```bash
git clone https://github.com/RMHavelaar101/item-magent.git
cd item-magnet
./gradlew build
```

Output: `build/libs/ItemMagnet-1.6.1.jar` (~250 KB; SQLite/MySQL drivers loaded by Paper at runtime when using SQL storage)

Requires **Java 21+**. See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup and tests.

---

## Support

- [Report a bug](https://github.com/RMHavelaar101/item-magent/issues/new?template=bug_report.yml)
- [Request a feature](https://github.com/RMHavelaar101/item-magent/issues/new?template=feature_request.yml)
- [Product page](https://itemmagnet.theryn.org)

Include Paper version, Java version, and steps to reproduce.

---

## License

MIT — see [LICENSE](LICENSE).
