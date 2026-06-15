# bStats Setup

ItemMagnet follows the official [bStats Gradle Shadow guide](https://bstats.org/getting-started).

## How it is bundled

In `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.bstats:bstats-bukkit:3.2.1")
    // SQLite/MySQL/Hikari: compileOnly + plugin.yml libraries (Paper downloads at runtime)
}

tasks.shadowJar {
    // Only bStats is shaded — keeps the Hangar/upload JAR under ~300 KB
    relocate("org.bstats", "com.rmh.lib.bstats")
}
```

SQLite JDBC, MySQL connector, and HikariCP are declared under `libraries:` in `plugin.yml`. Paper downloads them on first load (cached locally). This avoids bundling multi-platform native SQLite binaries in the release JAR.

Build the release JAR:

```bash
./gradlew shadowJar
```

## Register your plugin

1. Create a free account at [bstats.org](https://bstats.org)
2. Add a new plugin (Bukkit / Spigot / Paper)
3. Copy your **plugin ID** (a number like `12345`)

## Configure on the server

After first run, edit `plugins/ItemMagnet/config.yml`:

```yaml
metrics:
  bstats-enabled: true
  bstats-plugin-id: YOUR_ID_HERE
  bstats-block-reasons: true
```

Then `/itemmagnet reload` or restart.

Until you set a valid ID, the plugin logs a reminder and skips metrics — everything else works normally.

## Custom charts

ItemMagnet reports session aggregates (reset on restart):

| Chart | Description |
|-------|-------------|
| `lands_enabled` | Lands integration active |
| `worldguard_enabled` | WorldGuard integration active |
| `anti_afk_enabled` | Anti-AFK enabled |
| `underground_modifier_enabled` | Underground radius modifier enabled |
| `tier_count` | Number of configured tiers |
| `player_filter_storage` | `YAML`, `SQLITE`, or `MYSQL` |
| `inventory_full_behavior` | Config value |
| `default_filter_preset` | `player-filter.default-preset` |
| `hold_mode` | Config value |
| `pull_experience` | Global XP pull toggle |
| `top_block_reason` | Most common `PullBlockReason` this session |
| `block_reason_*` | `active` / `inactive` per major reason (when `bstats-block-reasons` is true) |

Blocked-pull charts use an in-memory collector hooked to `ItemMagnetPullBlockedEvent` — no per-tick or per-item overhead.

## Disable bStats

Server owners can disable globally in `plugins/bStats/config.yml`, or per-plugin:

```yaml
metrics:
  bstats-enabled: false
```

Disable only block-reason charts:

```yaml
metrics:
  bstats-block-reasons: false
```
