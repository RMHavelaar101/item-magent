# Changelog

## [1.6.6] - 2026-06-15

### Added

- **Update checker improvements** — prominent console banner on boot when a newer GitHub release exists; notifies online admins when the async check completes (no rejoin required)
- **Clickable update link** — in-game `[Download]` opens configured URL (default GitHub releases)
- **`/itemmagnet version`** — shows pending update and download link for admins
- **`metrics.update-download-url`** — configurable download link (default GitHub releases latest)

### Changed

- **Semver comparison** — update checks compare version numbers correctly (e.g. `1.6.10` > `1.6.9`; no false positives when remote is older)
- **Hot reload** — `metrics.update-check` and download URL apply on `/itemmagnet reload` without server restart
- **URLs** — GitHub repo moved to [MCTheryn/item-magent](https://github.com/MCTheryn/item-magent); product page [theryn.org/itemmagnet](https://www.theryn.org/itemmagnet)

### Fixed (since 1.6.0)

- **Stuck items near ledges** — horizontal-first pull tries alternate axes when a step is blocked
- **Items stopping short of the player** — magnet collects items within pickup distance using vertical reach
- **Vertical Y-level pull** — `vertical-reach-blocks` and `vertical-pull-mode` settings
- **WorldGuard region blacklist** — fixed region iteration crash
- **`/itemmagnet reload` crash** — schedulers restart cleanly
- **WorldGuard hook** — fixed reflection errors on ITEM_PICKUP checks
- **Smaller release JAR** — JDBC drivers loaded via Paper libraries (~330 KB)

## [1.6.0] - 2026-06-15

### Added

- `/itemmagnet filter clear`, admin tag blacklist GUI, preset preview/confirm
- Player filter storage backends — YAML, SQLite, or MySQL
- bStats session charts and Quests/CMI pull-blocked bridges

Full history: [GitHub CHANGELOG](https://github.com/MCTheryn/item-magent/blob/main/CHANGELOG.md)
