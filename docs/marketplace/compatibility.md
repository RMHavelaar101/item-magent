# Version compatibility

Official support matrix for ItemMagnet **1.2.1**.

## Requirements

| Component | Supported | Notes |
|-----------|-----------|-------|
| **Paper** | **1.21.1+** | Required. Uses Paper API and modern interact events. |
| **Java** | **21+** | Plugin is built with Java 21 toolchain. |
| **Minecraft** | **1.21.1+** | Any Paper build for 1.21.1 and newer game versions. |

## Tested Paper builds

These versions are explicitly listed in `build.gradle.kts` Hangar publish config and have been used during development:

| Paper version | Minecraft | Status |
|---------------|-----------|--------|
| 1.21.1 | 1.21.1 | Minimum supported |
| 1.21.4 | 1.21.4 | Tested |
| 26.1 | 1.21.x (Paper internal) | Tested on Theryn SMP |

Newer Paper releases on the 1.21.x line are expected to work unless Mojang or Paper introduce breaking API changes. Report issues if a specific build fails.

## Not supported

| Platform | Reason |
|----------|--------|
| **Spigot / CraftBukkit** | Paper API and 1.21+ interact handling (`setUseItemInHand(DENY)`) are required for fuel transfer. |
| **Folia** | Plugin uses standard Bukkit scheduler; no region-thread migration. |
| **Proxy (Velocity, Bungee)** | Server-side only; no proxy component. |
| **Bedrock** | Java Edition only. |

## Optional plugins

All soft dependencies — ItemMagnet runs without them:

| Plugin | Purpose |
|--------|---------|
| Lands 7.x | Claim protection |
| WorldGuard 7.x | Region rules |
| Towny | Town plots |
| GriefPrevention | Claim protection |
| PlaceholderAPI | Scoreboard/tab placeholders |
| CMI | Stat/rank unlock gates |

## Java version by host

| Host type | Recommendation |
|-----------|----------------|
| Dedicated / VPS | Install Temurin 21 LTS |
| Pterodactyl / panel | Set Docker image or Java version to 21 |
| Local dev | `java -version` must show 21+ |

If the server starts on Java 17 or older, the plugin will not load.

## Checking your server

```
/version
```

Look for `Paper` in the output and a version ≥ 1.21.1.

```
/itemmagnet version
```

Shows ItemMagnet version and which optional hooks detected Lands, WorldGuard, Towny, GriefPrevention, CMI.

## api-version

`plugin.yml` sets `api-version: '1.21'`, which tells Paper to apply 1.21+ compatibility guards. Do not run on pre-1.21 Paper builds.
