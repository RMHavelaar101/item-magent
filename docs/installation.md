# Installation

## Requirements

| Component | Version |
|-----------|---------|
| Server software | **Paper 1.21.1+** (tested through 26.1) |
| Java | **21+** |
| Lands | Optional — claim protection |
| WorldGuard | Optional — region protection |
| Towny | Optional — town protection |
| GriefPrevention | Optional — claim protection |
| PlaceholderAPI | Optional — placeholders |
| CMI | Optional — stat/rank unlock gates |

## Steps

1. Download `ItemMagnet-1.2.1.jar` from [GitHub Releases](https://github.com/RMHavelaar101/item-magent/releases) or [Hangar](https://hangar.papermc.io/RMHavelaar101/ItemMagnet).
2. Place the JAR in your server's `plugins/` directory.
3. Start or restart the server.
4. On first run, ItemMagnet creates:
   - `plugins/ItemMagnet/config.yml`
   - `plugins/ItemMagnet/messages.yml`
   - `plugins/ItemMagnet/unlocks.yml` (when COMMAND unlocks are granted)
5. Edit `config.yml` to tune tiers, fuel, and integrations.
6. Reload with `/itemmagnet reload` or restart.

## Applying the Theryn preset

Copy sections from `plugins/ItemMagnet/presets/theryn.yml` into your live `config.yml`, or set:

```yaml
preset: theryn
```

Then run `/itemmagnet reload`.

## bStats

Plugin ID `31998` is pre-configured. To disable:

```yaml
metrics:
  bstats-enabled: false
```

## Java 21+

Verify with:

```bash
java -version
```

Your start script must use Java 21 or newer.
