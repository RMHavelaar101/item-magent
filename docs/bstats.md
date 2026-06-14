# bStats Setup

ItemMagnet uses the **Gradle Shadow** approach recommended by [bStats](https://bstats.org/getting-started).

## How it is bundled

In `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

shadowJar {
    // Relocation omitted on Java 25 — Shadow's ASM cannot remap Java 25 bytecode yet.
    // bStats is still bundled into the release JAR via `implementation`.
}
```

The Shadow plugin bundles bStats into `ItemMagnet-1.0.0.jar` and relocates the package so it does not conflict with other plugins that also use bStats.

We did **not** use copy-paste because Gradle Shadow is cleaner for releases and CI.

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
```

Then `/itemmagnet reload` or restart.

Until you set a valid ID, the plugin logs a reminder and skips metrics — everything else works normally.

## Custom charts

ItemMagnet reports:

- `lands_enabled`
- `worldguard_enabled`
- `anti_afk_enabled`
- `underground_modifier_enabled`
- `tier_count`

## Disable bStats

Server owners can disable globally in `plugins/bStats/config.yml`, or per-plugin:

```yaml
metrics:
  bstats-enabled: false
```
