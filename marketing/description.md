# ItemMagnet

**Tiered item magnets with redstone fuel, smart filters, visible pull physics, and claim-aware protection.**

Pull dropped loot toward you with magnets that feel physical — items slide around corners and stop at walls. Charge them with redstone, respect Lands and WorldGuard claims, filter what you collect, and tune everything from an admin GUI or `config.yml`.

**Website:** [theryn.org/itemmagnet](https://www.theryn.org/itemmagnet) · **GitHub:** [MCTheryn/item-magent](https://github.com/MCTheryn/item-magent)

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

## Gameplay

- Three tiers — **Fragment**, **Survey**, **Anchor** (materials, names, radius, drain, recipes all configurable)
- Hold modes — main hand, hotbar, or anywhere in inventory
- XP orb pulling at the same radius (optional)
- Sneak + right-click fuel (either hand) or auto-absorb redstone drops
- Power surge from redstone blocks — charge + temporary radius boost
- Live item lore — charge, boost timer, and current pull radius
- Vertical reach — pull items above/below you within tier radius (`vertical-reach-blocks`, `vertical-pull-mode`)
- Per-fuel recharge sounds; pull, depleted, and denied cues

---

## Filters

- Server material + tag blacklist — config and admin GUI (Materials | Tags views)
- Personal filter GUI — `/itemmagnet filter` with server rules read-only at the top
- Built-in presets — mining, farming, mob-drops, keep-valuables with merge preview/confirm
- `/itemmagnet filter clear` — wipe personal rules; `/itemmagnet import` for bulk merge
- Player filter storage — YAML (default), SQLite, or MySQL with one-time YAML migration
- Tier whitelist/blacklist and tag rules per magnet tier

---

## For server owners

- `/itemmagnet config` — in-game editor for settings, tiers, fuel, integrations, item filter, and proximity lore
- Rename tier display names from the GUI (chat input, `&` color codes)
- Server presets — theryn, skyblock, vanilla-survival, hub-spawn, testing, or custom
- World blacklist/whitelist filter for hub and spawn worlds
- Anti-AFK movement check with one-time notify
- Unlock gates — permission, advancement, CMI stat/rank, LuckPerms group, mcMMO skill, Quests, or admin command
- Proximity lore — optional coordinate zones with ambient messages (default off)
- Config audit log — `plugins/ItemMagnet/config-audit.log`
- Update checker — console banner on boot, clickable in-game download link for admins

---

## Integrations (all optional)

| Plugin | Purpose |
|--------|---------|
| Lands | Wilderness, owner, member, and flag-based modes |
| WorldGuard | Region whitelist/blacklist and item-pickup flag |
| Towny | Town plot protection |
| GriefPrevention | Claim respect |
| Residence | Residence claim and itempickup flags |
| PlotSquared | Plot membership checks |
| SuperiorSkyblock2 | Island permission hook |
| CMI | Stat/rank unlock gates; optional progress on blocked pulls |
| LuckPerms | LP_GROUP unlock type for tier recipes |
| mcMMO | MCMMO_SKILL level unlock gates |
| Quests | Quest-complete tier unlocks; optional progress on blocked pulls |
| PlaceholderAPI | Charge, radius, tier, boost, and filter count placeholders |

Developer API — `grantUnlock`, `giveMagnet`, pull/blocked events, hook status.

---

## Compatibility

| | Requirement |
|---|-------------|
| **Server** | Paper **1.21.1 or newer** (required) |
| **Java** | **21 or newer** |
| **Tested on** | Paper 1.21.1, 1.21.4, 26.1 |
| **Not supported** | Spigot, CraftBukkit, Folia |

Paper is required — the plugin uses the Paper API and modern interact handling for fuel transfer.

---

## Quick start

1. Download `ItemMagnet-1.6.6.jar` from GitHub Releases
2. Place the JAR in your server's `plugins/` folder and restart
3. Give yourself a magnet: `/itemmagnet give YourName fragment 500`
4. Hold the magnet; put redstone dust in your other hand; sneak + right-click to fuel
5. Drop items nearby — they pull toward you with visible motion

Optional: `/itemmagnet filter` for personal filters, `/itemmagnet config` for admin tuning.

---

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/itemmagnet` | — | Show help (filtered by permission) |
| `/itemmagnet reload` | `itemmagnet.reload` | Hot-reload config and messages |
| `/itemmagnet config` | `itemmagnet.config` | Open in-game config editor |
| `/itemmagnet filter` | `itemmagnet.filter` | Personal item filter GUI |
| `/itemmagnet filter clear` | `itemmagnet.filter` | Clear personal filter rules |
| `/itemmagnet give <player> <tier\|all> [charge]` | `itemmagnet.give` | Give a resonator |
| `/itemmagnet unlock <player> <tier\|all>` | `itemmagnet.unlock` | Unlock a tier recipe |
| `/itemmagnet debug` | `itemmagnet.debug` | Stats for your active magnet |
| `/itemmagnet version` | `itemmagnet.admin` | Version, hook status, and update info |

Aliases: `/im`, `/magnet`

---

## Links

- [Product page](https://www.theryn.org/itemmagnet)
- [GitHub Releases](https://github.com/MCTheryn/item-magent/releases/latest)
- [Documentation](https://github.com/MCTheryn/item-magent/tree/main/docs)
- [Report a bug](https://github.com/MCTheryn/item-magent/issues)

MIT License.
