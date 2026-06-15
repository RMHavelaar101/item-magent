# Version compatibility

Official support matrix for ItemMagnet.

## Requirements

| Component | Supported | Notes |
|-----------|-----------|-------|
| **Paper** | **1.21.1+** | Required. Uses Paper API and modern interact events. |
| **Java** | **21+** | Plugin is built with Java 21 toolchain. |
| **Minecraft** | **1.21.1+** | Any Paper build for 1.21.1 and newer game versions. |

## Tested Paper builds

| Paper version | Minecraft | Status |
|---------------|-----------|--------|
| 1.21.1 | 1.21.1 | Minimum supported |
| 1.21.4 | 1.21.4 | Tested |
| 26.1 | 1.21.x (Paper internal) | Tested |

## Optional plugins (soft dependencies)

All integrations are optional — ItemMagnet runs standalone.

| Plugin | Tested version | Integration |
|--------|----------------|-------------|
| **Lands** | 7.26.3 | Claim / wilderness / `ITEM_PICKUP` flag |
| **WorldGuard** | 7.0.16 | Region lists + `item-pickup` flag |
| **Towny** | 0.103.0.0 | Town plot protection |
| **GriefPrevention** | 16.18.7 (Legacy v16) | Claim respect |
| **PlaceholderAPI** | 2.12.2 | `%itemmagnet_*%` placeholders |
| **CMI** + **CMILib** | 9.8.7.x + 1.5.9.3 | `CMI_STAT` / `CMI_RANK` unlock gates |
| **LuckPerms** | 5.5.x | `LP_GROUP` unlock type |
| **mcMMO** | 2.2.x (Paper 26.1 line) | `MCMMO_SKILL` unlock gates |
| **Residence** | 6.0.0.1 | Claim flag hook |
| **PlotSquared** | 6.11.1 | Plot guest/member checks |
| **SuperiorSkyblock2** | Latest stable on Hangar | Island permission hook |
| **Quests** (PikaMug) | 5.2.9 | Optional quest-complete → tier unlock mapping |

## Checking your server

```
/itemmagnet version
```

Example output:

```
ItemMagnet v1.3.0 | Paper git-Paper-26.1-...
Hooks: Lands: true | Worldguard: true | Towny: false | ...
```

Protection hooks report `true` when the plugin is installed **and** reflection initialized successfully. Unlock providers (CMI, LuckPerms, mcMMO, Quests) report `true` when the plugin JAR is present.

## Not supported

| Platform | Reason |
|----------|--------|
| **Spigot / CraftBukkit** | Paper API required for fuel transfer. |
| **Folia** | Standard Bukkit scheduler; no region-thread migration. |
| **Proxy (Velocity, Bungee)** | Server-side only. |
| **Bedrock** | Java Edition only. |

## api-version

`plugin.yml` sets `api-version: '1.21'`. Do not run on pre-1.21 Paper builds.
