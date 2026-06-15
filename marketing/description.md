# ItemMagnet — product description (source)

Use this file as the canonical homepage / listing copy. Derived formats live alongside it:

- `description.html` — Hangar, Spigot resource HTML
- `description.bbcode` — Spigot BBCode
- `modrinth.md` — Modrinth project page
- `changelog-v1.6.0.md` — release notes for v1.6.0

---

## Tagline

Tiered item magnets with redstone fuel, smart filters, visible pull physics, and claim-aware protection.

## Short (one paragraph)

ItemMagnet is a Paper plugin built for survival SMPs. Pull dropped loot with line-of-sight physics — items slide around corners and stop at walls instead of phasing through blocks. Fuel resonators with redstone dust and blocks, respect Lands and WorldGuard claims, and tune everything from an in-game config GUI or YAML. Personal and server-wide item filters (including Minecraft tag rules), tier progression with recipe unlock gates, and optional hooks for PlaceholderAPI, Quests, CMI, and more.

## Why ItemMagnet?

Most magnet plugins teleport items through walls or ignore land claims. ItemMagnet focuses on **fairness, polish, and operator control**:

- **Visible physics** — step-based pull with line-of-sight; no wall clipping
- **Claim-aware** — Lands, WorldGuard, Towny, GriefPrevention, Residence, PlotSquared, SuperiorSkyblock2
- **Smart filters** — server blacklist, per-player GUI, tag rules (`minecraft:logs`), tier whitelists/blacklists, presets with preview/confirm
- **Redstone fuel loop** — dust and blocks recharge the magnet; blocks trigger a radius boost
- **Admin-friendly** — `/itemmagnet config` GUI, hot reload, presets, LuckPerms-ready permissions, audit log
- **Production-ready storage** — player filters in YAML, SQLite, or MySQL (with one-time YAML migration)

## Core gameplay

- Three default tiers (Fragment, Survey, Anchor) — fully configurable materials, lore, radius, drain, recipes
- Hold modes: main hand, hotbar, or anywhere in inventory
- XP orb pulling (optional)
- Sneak + right-click fuel transfer or auto-absorb redstone drops
- Power surge from redstone blocks — extra charge + temporary radius boost
- Live item lore: charge, boost timer, current pull radius

## Filters (v1.4–v1.6)

- **Server rules** — material + tag blacklist in config and admin GUI
- **Personal GUI** — `/itemmagnet filter` with server rules read-only at the top
- **Presets** — mining, farming, mob-drops, keep-valuables; merge preview before apply
- **Clear command** — `/itemmagnet filter clear`
- **Import** — bulk merge via `/itemmagnet import`
- **Storage backends** — YAML (default), SQLite, or MySQL for large networks

## Integrations (all optional)

Lands · WorldGuard · Towny · GriefPrevention · Residence · PlotSquared · SuperiorSkyblock2 · CMI · LuckPerms · mcMMO · Quests · PlaceholderAPI · Developer API (`ItemMagnetPullBlockedEvent`, pull/fuel/deplete events)

## Requirements

- **Paper** 1.21.1 or newer (not Spigot/CraftBukkit/Folia)
- **Java** 21+

## Links

- Website: https://itemmagnet.theryn.org
- GitHub: https://github.com/RMHavelaar101/item-magent
- Hangar: https://hangar.papermc.io/Alcerious/ItemMagnets
- Docs: https://github.com/RMHavelaar101/item-magent/tree/main/docs
