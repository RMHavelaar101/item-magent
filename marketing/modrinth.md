# ItemMagnet

**Tiered item magnets with redstone fuel, smart filters, visible pull physics, and claim-aware protection.**

ItemMagnet is a Paper plugin for survival SMPs. Pull dropped loot with **line-of-sight physics** — items slide around corners and stop at walls instead of phasing through blocks. Fuel resonators with redstone, respect land claims, and tune everything from an in-game config GUI or YAML.

---

## Highlights

- **Visible physics** — step-based pull with line-of-sight; no wall clipping
- **Claim-aware** — Lands, WorldGuard, Towny, GriefPrevention, Residence, PlotSquared, SuperiorSkyblock2
- **Smart filters** — server + personal blacklists, Minecraft tag rules, tier whitelists/blacklists, presets with merge preview
- **Redstone fuel loop** — dust and blocks recharge the magnet; redstone blocks trigger a radius boost
- **Admin-friendly** — `/itemmagnet config` GUI, hot reload, server presets, LuckPerms-ready permissions, config audit log
- **Production storage** — player filter data in YAML, SQLite, or MySQL (one-time YAML migration)

---

## Gameplay

- Three configurable tiers (Fragment, Survey, Anchor) — materials, lore, radius, recipes, unlock gates
- Hold modes: main hand, hotbar, or anywhere in inventory
- Optional XP orb pulling at the same radius
- Sneak + right-click fuel transfer or auto-absorb redstone drops
- Power surge from redstone blocks — extra charge + temporary radius boost
- Live item lore: charge, boost timer, current pull radius

---

## Filters

- Server material + tag blacklist (`settings.item-blacklist`, `settings.item-blacklist-tags`)
- Personal filter GUI — `/itemmagnet filter` (server rules shown read-only)
- Built-in presets: `mining`, `farming`, `mob-drops`, `keep-valuables`
- Preset merge **preview + confirm** before applying
- `/itemmagnet filter clear` — wipe personal rules (keeps first-hint metadata)
- `/itemmagnet import` — bulk merge materials or presets
- Storage backends: **YAML** (default), **SQLite**, **MySQL**

---

## Integrations (all optional)

| Plugin | Purpose |
|--------|---------|
| Lands | Wilderness, owner, member, flag modes |
| WorldGuard | Region lists, item-pickup flag |
| Towny | Town plot protection |
| GriefPrevention | Claim respect |
| Residence / PlotSquared / SuperiorSkyblock2 | Additional claim hooks |
| CMI | Stat/rank unlock gates; optional progress on blocked pulls |
| LuckPerms | LP_GROUP recipe unlocks |
| mcMMO | Skill level unlock gates |
| Quests | Quest-complete tier unlocks; optional progress on blocked pulls |
| PlaceholderAPI | Charge, radius, tier, boost, filter counts |
| Developer API | Events, `grantUnlock`, `giveMagnet`, hook status |

---

## Commands (essentials)

| Command | Description |
|---------|-------------|
| `/itemmagnet give <player> <tier> [charge]` | Give a magnet |
| `/itemmagnet filter` | Personal filter GUI |
| `/itemmagnet filter clear` | Clear personal filter rules |
| `/itemmagnet config` | In-game config editor |
| `/itemmagnet reload` | Hot-reload config |

Aliases: `/im`, `/magnet`

Full reference: [docs/commands.md](https://github.com/RMHavelaar101/item-magent/blob/main/docs/commands.md)

---

## Requirements

- **Paper** 1.21.1 or newer
- **Java** 21+
- Tested on Paper 1.21.1, 1.21.4, 26.1
- **Not supported:** Spigot, CraftBukkit, Folia

---

## Links

- [Website](https://itemmagnet.theryn.org)
- [GitHub & documentation](https://github.com/RMHavelaar101/item-magent)
- [Hangar](https://hangar.papermc.io/Alcerious/ItemMagnets)

License: **MIT**
