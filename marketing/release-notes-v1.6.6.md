## What's new in 1.6.6

### Added

- **Update checker improvements** — prominent console banner on boot when a newer GitHub release exists; notifies online admins when the async check completes (no rejoin required)
- **Clickable update link** — in-game `[Download]` opens configured URL (default GitHub releases)
- **`/itemmagnet version`** — shows pending update and download link for admins
- **`metrics.update-download-url`** — configurable download link (default GitHub releases latest)

### Changed

- **Semver comparison** — update checks compare version numbers correctly (e.g. `1.6.10` > `1.6.9`)
- **Hot reload** — `metrics.update-check` and download URL apply on `/itemmagnet reload` without server restart
- **URLs** — GitHub repo: [MCTheryn/item-magent](https://github.com/MCTheryn/item-magent) · Product page: [theryn.org/itemmagnet](https://www.theryn.org/itemmagnet)

### Also in the 1.6.x line

- Vertical reach pull, ledge/stall fixes, WorldGuard and reload stability fixes, smaller ~330 KB JAR

**Download:** `ItemMagnet-1.6.6.jar`  
**Requires:** Paper 1.21.1+, Java 21+
