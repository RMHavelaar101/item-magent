# Changelog

All notable changes to ItemMagnet are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/).

## [1.6.1] - 2026-06-15

### Changed

- **Release JAR size** — SQLite JDBC, MySQL connector, and HikariCP are loaded via Paper's `plugin.yml` `libraries` at runtime instead of being bundled in the shadow JAR (~250 KB vs ~17 MB). SQLite/MySQL filter storage behavior is unchanged on Paper 1.21.1+.

## [1.6.0] - 2026-06-15

### Added

- **`/itemmagnet filter clear`** — removes all personal filter materials and tags (preserves first-hint metadata)
- **Admin tag blacklist GUI** — edit `settings.item-blacklist-tags` from the Item Filter config menu (Materials | Tags toggle, chat add)
- **Preset preview + confirm** — preset picker shows merge preview (new materials/tags, server-skipped count) before applying
- **Player filter storage backends** — `player-filter.storage`: `YAML` (default), `SQLITE`, or `MYSQL`; one-time YAML → SQL import on first enable
- **bStats session charts** — top block reason, per-reason activity flags, storage backend, inventory-full behavior, default preset, hold mode, pull experience
- **Pull-blocked bridges** — config-driven Quests and CMI progress rules on `ItemMagnetPullBlockedEvent` (`integrations.quests.progress-on-blocked`, `integrations.cmi.progress-on-blocked`)

### Changed

- Personal filter data can be stored in SQLite (`player-filters.db`) or MySQL (HikariCP pool) instead of `player-filters.yml`
- Shadow JAR bundles and relocates SQLite JDBC, MySQL connector, and HikariCP alongside bStats

## [1.5.0] - 2026-06-15

### Added

- **Shared filter core** — `MaterialFilterResolver`, `PullEligibilityService`, tag-based rules (`settings.item-blacklist-tags`, tier `blacklist-tags` / `whitelist-tags`)
- **Player filter GUI** — read-only server rules section, personal materials + tags, preset picker (merge)
- **Filter presets** — jar presets (`mining`, `farming`, `mob-drops`, `keep-valuables`) + `plugins/ItemMagnet/filter-presets/` overrides; `player-filter.default-preset`
- **PlaceholderAPI** — `%itemmagnet_filter_count%`, `%itemmagnet_server_blacklist_count%`, `%itemmagnet_boost_active%`
- **API event** — `ItemMagnetPullBlockedEvent` (informational, deduped per tick)
- **Import commands** — `/itemmagnet import blacklist|filter|filter-preset` (`itemmagnet.import` for server blacklist)
- **Server presets** — `skyblock`, `vanilla-survival`, `hub-spawn`
- **Config audit log** — `plugins/ItemMagnet/config-audit.log` on config/filter changes
- **Inventory-full behavior** — `settings.inventory-full-behavior`: `CONTINUE`, `PAUSE`, `NOTIFY_ONCE`
- **Tier custom model data** — optional `custom-model-data` per tier (applied when > 0)
- **Debug** — filter state section; `/itemmagnet debug nearby` (5 nearest items + block reason)
- **Help & hints** — `/itemmagnet help filter`, `/itemmagnet filter help`, first-magnet filter hint

### Changed

- Config GUI shows gray locked controls (instead of empty slots) when view-only access is granted
- Pull eligibility checks consolidated through `PullEligibilityService` (server → tier → player → protection)

## [1.4.0] - 2026-06-15

### Added

- **Player item filter** — `/itemmagnet filter` opens a personal GUI to blacklist materials your magnet will ignore (`itemmagnet.filter`, default `true`)
- **Server item blacklist** — `settings.item-blacklist` in `config.yml` plus **Item Filter** section in the admin config GUI
- **Granular config GUI permissions** — per-section and per-field nodes under `itemmagnet.config.*` (save, reset, section, field)

### Changed

- Config GUI controls are hidden unless the player has the matching section or field permission
- Save & Reload and Reset require `itemmagnet.config.save` and `itemmagnet.config.reset` respectively

## [1.3.0] - 2026-06-15

### Added

- **Public API** — `ItemMagnetApi.grantUnlock`, `giveMagnet`, `isTierUnlocked`, `getHookStatus`
- **`MagnetTierUnlockedEvent`** — fires when a tier is granted via command or API
- **IntegrationStatusService** — unified hook detection for `/itemmagnet version` and startup log
- **New protection hooks** — Residence 6.0.0.1, PlotSquared 6.11.1, SuperiorSkyblock2 (reflection)
- **New unlock types** — `LP_GROUP` (LuckPerms), `MCMMO_SKILL` (mcMMO skill level)
- **Quests bridge** — optional `integrations.quests.unlock-on-complete` mapping (PikaMug Quests 5.2.9)
- **Config GUI** — toggles for Residence, PlotSquared, SuperiorSkyblock, Quests bridge
- Integration docs for all soft dependencies with tested version numbers

### Changed

- PlaceholderAPI compile dependency bumped to **2.12.2**
- `/itemmagnet version` prints two lines: version + full hook status map

### Fixed

- **`config.yml` YAML indentation** under `integrations` (GriefPrevention, Residence, PlotSquared, SuperiorSkyblock, Quests) — fixes reload/parse failures on strict YAML loaders

## [1.2.5] - 2026-06-15

### Added

- **Startup thank-you message** — logs website and review links to the console when the plugin enables (toggle via `/itemmagnet startup-message` or config GUI)
- **Config GUI: Reset all to defaults** — shift-click to restore `config.yml` from plugin defaults
- **`/im` command** — explicit shortcut registered alongside `/itemmagnet`

## [1.2.4] - 2026-06-15

### Fixed

- **Lands & WorldGuard reflection** — updated hook method lookups for current Lands RoleFlag typing and WorldGuard varargs StateFlag queries

## [1.2.3] - 2026-06-15

### Fixed

- **Paper 26.x recipe compatibility** — skip durability-style charge bar on stackable tier materials (e.g. recovery compass) so crafted recipe results stay valid and no longer break other plugins that scan recipes at startup
- **Lands 7.26 hook** — use `getArea(Location)` API instead of removed `getLand(Location)`
- **WorldGuard 7.0.17 hook** — use `RegionQuery.testState(Location, …)` instead of removed `ApplicableRegionSet.testState(LocalPlayer, …)`
- **AFK notify-once spam on login** — `markAfkNotified` now persists when no movement record exists; fresh logins are seeded and not treated as idle before tracking starts
- **Invalid fuel sound** — default fuel sound updated; legacy `BLOCK_REDSTONE_BLOCK_CLICK` aliases to `BLOCK_NOTE_BLOCK_BIT`

## [1.2.2] - 2026-06-14

### Added

- **Proximity lore** — optional `proximity-lore` config section: ambient messages when players with an active magnet enter coordinate zones
- Per-zone cooldown, Y tolerance, and optional per-tier message overrides
- Lightweight scan interval separate from item pull ticks
- Config GUI: **Proximity Lore** menu — toggle, timings, zone list, coordinate editor
- Unit tests for zone matching logic
- Documentation: [docs/proximity-lore.md](docs/proximity-lore.md)

### Notes

- **Backwards compatible** — feature is off by default; absent or `enabled: false` = no behavior change
- No dependencies on lore plugins, WorldGuard, or custom APIs — any SMP can use coordinate zones

## [1.2.1] - 2026-06-14

### Added

- Config GUI: rename tier display names via chat input (name tag button in tier editor)

### Changed

- Removed bStats/metrics section from config GUI (still configurable in config.yml if needed)

## [1.2.0] - 2026-06-14

### Added

- Live `{radius}` and `{base_radius}` placeholders on magnet item lore
- `settings.fuel-use-effective-radius` — absorb fuel within pull radius
- Per-fuel recharge sounds (`fuel.<MATERIAL>.sound`)
- AFK `notify-once` — single message per idle session
- Optional `settings.pull-arm-swing` hand animation on pull
- Permission-filtered help and tab completion (`commands.filter-by-permission`)
- Specific usage errors for give/unlock commands
- `/itemmagnet config` — full in-game config GUI with descriptions
- Reload restart warnings for metrics keys (bStats, update-check)

### Changed

- Manual and ground fuel transfer play per-material sounds (throttled)

## [1.1.1] - 2026-06-14

### Added

- Experience orb (XP bubble) pulling when `pull-experience` is enabled
- `/itemmagnet giveall` and `/itemmagnet unlockall` commands
- `give <player> all` and `unlock <player> all` aliases
- Multi-line `/itemmagnet help` (also shown when command has no args)
- `presets/testing.yml` for QA on live servers

### Fixed

- Shift+right-click fuel transfer no longer places redstone blocks (full interact deny + either-hand fuel)
- Fuel transfer writes to correct inventory slots (no main-hand overwrite)
- Manual fuel transfer now sends boost-active message
- `/itemmagnet debug` uses MagnetLocator and shows slot, hold-mode, boost timer, world/gamemode state

## [1.1.0] - 2026-06-14

### Added

- Paper **1.21.1+** / Java **21+** support (tested up to Paper 26.1)
- Passive magnet modes: `hold-mode` MAIN_HAND, HOTBAR, or INVENTORY
- Per-tier use permissions (`itemmagnet.use.<tier>`)
- Per-tier item whitelist (`whitelist-enabled` + `whitelist`)
- World blacklist/whitelist filter
- Configurable pull/fuel/depleted/denied sounds
- Towny and GriefPrevention protection hooks
- PlaceholderAPI placeholders (`%itemmagnet_charge%`, `%itemmagnet_radius%`, etc.)
- Public API events: `ItemMagnetPullEvent`, `ItemMagnetFuelAbsorbEvent`, `ItemMagnetDepleteEvent`
- Persistent COMMAND unlock storage (`unlocks.yml`)
- Nearby-entity item scanning for better performance on busy servers

### Fixed

- Update checker no longer reports false positives (`v1.0.0` vs `1.0.0`)
- `/itemmagnet version` now requires `itemmagnet.admin`
- `/itemmagnet give` charge argument validates input
- Reload reschedules magnet tick interval
- AFK, fuel-full, and craft-locked messages now sent when appropriate

### Changed

- Default tier 1 material is `FLINT_AND_STEEL` (avoids WorldEdit compass nav wand conflict)
- Build toolchain lowered to Java 21 for broader host compatibility

## [1.0.0] - 2026-06-14

### Added

- Three configurable magnet tiers (Fragment, Survey, Anchor Resonator) using vanilla items
- Visible line-of-sight item pull physics with particle trails
- Redstone dust and redstone block fuel system with shift+right-click recharge
- Redstone block power surge (temporary radius boost)
- Lands integration (wilderness, owner-only, member-only, respect flags)
- WorldGuard integration (item-pickup flag, region whitelist/blacklist)
- Open-ended recipe unlock gates (permission, advancement, CMI stat/rank, admin command)
- Anti-AFK movement check (configurable)
- Underground vs surface radius and drain modifiers
- `/itemmagnet` admin commands with tab completion
- bStats metrics and async update checker
- Theryn preset reference in `presets/theryn.yml`
