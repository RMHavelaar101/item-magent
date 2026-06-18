# Configuration Reference

All settings live in `plugins/ItemMagnet/config.yml`.

## Top-level

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `preset` | string | `none` | Merge a preset from `presets/` (`theryn`, `skyblock`, `vanilla-survival`, `hub-spawn`, …) |

## settings

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `scan-interval-ticks` | int | `2` | How often the magnet scans for items |
| `max-items-per-tick` | int | `10` | Performance cap per player per scan |
| `pull-step-blocks` | double | `0.4` | Distance items move per step |
| `pickup-distance` | double | `1.5` | Distance at which items are collected (uses separate vertical limit when `vertical-reach-blocks` > 0) |
| `vertical-reach-blocks` | double | `2` | Max blocks above/below player for pull range (horizontal radius still applies on X/Z). `0` = legacy 3D sphere |
| `vertical-pull-mode` | enum | `HORIZONTAL_FIRST` | `DIRECT` (straight LOS) or `HORIZONTAL_FIRST` (slide horizontally, then vertically) |
| `sneak-to-disable` | bool | `true` | Sneaking pauses the magnet |
| `fuel-radius` | double | `3` | Auto-absorb radius for redstone fuel |
| `show-charge-bar` | bool | `true` | Show durability-style charge bar |
| `particle-type` | string | `REVERSE_PORTAL` | Bukkit `Particle` enum name |
| `deny-message-cooldown-ticks` | int | `40` | Cooldown for protection deny messages |
| `pull-experience` | bool | `true` | Pull experience orbs (XP bubbles) |
| `hold-mode` | enum | `MAIN_HAND` | `MAIN_HAND`, `HOTBAR`, `INVENTORY` |
| `multi-magnet-policy` | enum | `BEST_TIER` | `BEST_TIER`, `FIRST_FOUND` |
| `disable-in-creative` | bool | `true` | Disable for creative players |
| `disable-in-spectator` | bool | `true` | Disable for spectators |
| `item-blacklist` | list | `[]` | Materials never pulled for any player/tier (server-wide) |
| `item-blacklist-tags` | list | `[]` | Bukkit material tags (e.g. `minecraft:shulker_boxes`) |
| `inventory-full-behavior` | enum | `CONTINUE` | `CONTINUE`, `PAUSE`, or `NOTIFY_ONCE` when inventory cannot fit an item |
| `world-filter.mode` | enum | `NONE` | `NONE`, `WHITELIST`, `BLACKLIST` |
| `world-filter.worlds` | list | `[]` | World names for filter |
| `sounds.enabled` | bool | `false` | Play magnet sound effects |
| `sounds.pull` / `fuel` / `depleted` / `denied` | string | — | Bukkit `Sound` enum names |

## metrics

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `bstats-enabled` | bool | `true` | Enable bStats |
| `bstats-plugin-id` | int | `31998` | Your bStats plugin ID |
| `bstats-block-reasons` | bool | `true` | Session charts for blocked-pull reasons |
| `update-check` | enum | `ON_STARTUP` | `ALWAYS`, `ON_STARTUP`, `DISABLED` — checks GitHub releases |
| `update-download-url` | string | GitHub releases latest | Link in console banner and in-game update notifications |

## anti-afk

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | bool | `false` | Enable movement check |
| `required-blocks-moved` | double | `2` | Min horizontal travel in window |
| `window-seconds` | int | `60` | AFK detection window |
| `disable-auto-fuel-when-afk` | bool | `true` | Block fuel absorb when AFK |

## height

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `use-y-range` | bool | `false` | Hard-disable outside Y range |
| `min-y` | int | `-64` | Minimum Y for magnet use |
| `max-y` | int | `320` | Maximum Y for magnet use |
| `underground.enabled` | bool | `false` | Enable underground modifiers |
| `underground.threshold-y` | int | `50` | Below this Y = underground |
| `underground.radius-modifier` | double | `-2` | Radius adjustment underground |
| `underground.drain-multiplier` | double | `1.25` | Extra drain underground |
| `surface.radius-modifier` | double | `0` | Radius adjustment on surface |

## fuel

Per-material fuel entries (`REDSTONE`, `REDSTONE_BLOCK`):

| Key | Type | Description |
|-----|------|-------------|
| `charge-per-item` | int | Charge added per item |
| `radius-bonus` | int | Radius bonus per boost level |
| `boost-level-add` | int | Boost stacks added |
| `boost-duration-seconds` | int | Boost duration |

## integrations.lands

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | bool | `false` | Enable Lands hook |
| `check-at` | enum | `BOTH` | `ITEM`, `PLAYER`, `BOTH` |
| `wilderness` | enum | `ALLOW` | `ALLOW`, `DENY`, `PERMISSION` |
| `wilderness-permission` | string | `itemmagnet.wilderness` | Permission for wilderness |
| `claimed-land` | enum | `RESPECT_FLAGS` | See [lands.md](integrations/lands.md) |
| `require-player-in-allowed-land` | bool | `false` | Player feet must be in allowed land |

## integrations.worldguard

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | bool | `false` | Enable WorldGuard hook |
| `check-at` | enum | `BOTH` | `ITEM`, `PLAYER`, `BOTH` |
| `respect-item-pickup-flag` | bool | `true` | Honor `item-pickup` flag |
| `region-mode` | enum | `NONE` | `NONE`, `WHITELIST`, `BLACKLIST` |
| `regions` | list | `[]` | Region IDs |
| `worlds.<world>.region-mode` | enum | — | Per-world override |
| `worlds.<world>.regions` | list | — | Per-world region list |

## integrations.towny

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | bool | `false` | Enable Towny hook |
| `wilderness` | enum | `ALLOW` | `ALLOW`, `DENY`, `PERMISSION` |
| `wilderness-permission` | string | `itemmagnet.wilderness` | Wilderness permission |
| `claimed-town` | enum | `RESPECT_FLAGS` | See [towny.md](integrations/towny.md) |

## integrations.griefprevention

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enabled` | bool | `false` | Enable GriefPrevention hook |
| `claimed-land` | enum | `RESPECT_FLAGS` | See [griefprevention.md](integrations/griefprevention.md) |

## tiers.<id>

Each tier supports:

| Key | Type | Description |
|-----|------|-------------|
| `material` | string | Bukkit material for the held item model (e.g. `FLINT_AND_STEEL`) |
| `display-name` | string | Item name (`&` color codes) |
| `lore` | list | Lore lines; placeholders: `{charge}`, `{max_charge}`, `{boost}` |
| `enchant-glint` | bool | Fake enchant glint |
| `radius` | double | Base pull radius |
| `max-charge` | int | Maximum fuel |
| `base-drain-per-second` | double | Passive drain while active |
| `extra-drain-per-item` | double | Drain per item pulled per step |
| `boost-drain-multiplier` | double | Drain multiplier during boost |
| `min-radius` / `max-radius` | double | Radius clamps |
| `blacklist` | list | Materials never pulled |
| `blacklist-tags` | list | Tag-based tier blacklist |
| `whitelist-enabled` | bool | Enable whitelist filtering |
| `whitelist` | list | Allowed materials when whitelist enabled |
| `whitelist-tags` | list | Tag-based tier whitelist |
| `custom-model-data` | int | Optional CustomModelData (> 0 applies to new items) |
| `pull-experience` | bool | Pull XP orbs for this tier (requires global `pull-experience`) |
| `unlock` | section | See [recipes-and-unlocks.md](recipes-and-unlocks.md) |
| `recipe` | section | Shaped recipe definition |

## player-filter

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `storage` | enum | `YAML` | `YAML`, `SQLITE`, or `MYSQL` |
| `sqlite.file` | string | `player-filters.db` | SQLite database file under the plugin data folder |
| `mysql.*` | section | — | Host, port, database, credentials, pool size, table prefix |
| `default-preset` | string | `none` | Apply a built-in preset when a player has no personal rules (`mining`, `farming`, …) |
| `show-preset-hint` | bool | `true` | One-time `/itemmagnet filter` hint on first magnet received |

When `storage` is `SQLITE` or `MYSQL` and the SQL tables are empty but `player-filters.yml` exists, ItemMagnet imports all entries once and renames the YAML file to `player-filters.yml.bak`.

SQLite and MySQL drivers are **not** bundled in the plugin JAR. Paper loads them from Maven via `libraries:` in `plugin.yml` (requires outbound network on first run, then cached). Default `YAML` storage needs no extra downloads.

Changing the storage backend type requires a restart for a clean swap (flush memory → new repository).

Built-in presets ship in the jar under `filter-presets/`. Override or add presets in `plugins/ItemMagnet/filter-presets/*.yml`.

## integrations.quests / integrations.cmi (pull-blocked)

See [integrations/pull-blocked-bridges.md](integrations/pull-blocked-bridges.md) for `progress-on-blocked` rules on `ItemMagnetPullBlockedEvent`.

## proximity-lore (optional)

Ambient messages when players with an active magnet enter configured zones. **Default off** — see [proximity-lore.md](proximity-lore.md) for examples and zone keys.
