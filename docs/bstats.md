# bStats Setup

ItemMagnet follows the official [bStats Gradle Shadow guide](https://bstats.org/getting-started).

## How it is bundled

In `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

tasks.shadowJar {
    configurations.set(listOf(project.configurations.runtimeClasspath.get()))
    dependencies {
        exclude { dependency -> dependency.moduleGroup != "org.bstats" }
    }
    relocate("org.bstats", project.group.toString()) // -> com.rmh
}
```

This:

1. Merges **only** bStats into the release JAR (Paper API stays `compileOnly`)
2. Relocates `org.bstats` → `com.rmh` to avoid conflicts with other plugins

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
