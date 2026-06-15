# ItemMagnet v1.6.0 — release notes

**Release date:** 2026-06-15

## Added

- **`/itemmagnet filter clear`** — removes all personal filter materials and tags (preserves first-hint metadata)
- **Admin tag blacklist GUI** — edit `settings.item-blacklist-tags` from the Item Filter config menu (Materials | Tags toggle, chat add)
- **Preset preview + confirm** — preset picker shows merge preview (new materials/tags, server-skipped count) before applying
- **Player filter storage backends** — `player-filter.storage`: `YAML` (default), `SQLITE`, or `MYSQL`; one-time YAML → SQL import on first enable
- **bStats session charts** — top block reason, per-reason activity flags, storage backend, inventory-full behavior, default preset, hold mode, pull experience
- **Pull-blocked bridges** — config-driven Quests and CMI progress rules on `ItemMagnetPullBlockedEvent`

## Changed

- Personal filter data can be stored in SQLite (`player-filters.db`) or MySQL (HikariCP pool) instead of `player-filters.yml`
- Shadow JAR bundles and relocates SQLite JDBC, MySQL connector, and HikariCP alongside bStats

## Upgrade notes

- Default `player-filter.storage: YAML` — no behavior change for existing servers
- Switching storage backend type is best done after a restart (flush memory → new repository)
- Quests/CMI `progress-on-blocked` rules default **off**
- Full changelog: [CHANGELOG.md](../CHANGELOG.md)
