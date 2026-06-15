# Changelog

All notable changes to ItemMagnet are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/).

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
