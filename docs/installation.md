# Installation

## Requirements

| Component | Version |
|-----------|---------|
| Server software | **Paper 26.1.x** (build 69+ recommended) |
| Java | **25** |
| Lands | Optional — claim protection |
| WorldGuard | Optional — region protection |
| CMI | Optional — stat/rank unlock gates |

## Steps

1. Download `ItemMagnet-1.0.0.jar` from GitHub Releases or Hangar.
2. Place the JAR in your server's `plugins/` directory.
3. Start or restart the server.
4. On first run, ItemMagnet creates:
   - `plugins/ItemMagnet/config.yml`
   - `plugins/ItemMagnet/messages.yml`
5. Edit `config.yml` to tune tiers, fuel, and integrations.
6. Reload with `/itemmagnet reload` or restart.

## Applying the Theryn preset

Copy sections from `plugins/ItemMagnet/presets/theryn.yml` into your live `config.yml`, or set:

```yaml
preset: theryn
```

Then run `/itemmagnet reload`.

## bStats

Register at [bstats.org](https://bstats.org) and set your plugin ID:

```yaml
metrics:
  bstats-enabled: true
  bstats-plugin-id: YOUR_ID
```

## Java 25

Paper 26.1 requires Java 25. Verify with:

```bash
java -version
```

Your start script must use Java 25, for example:

```bash
java -Xms4G -Xmx4G -jar paper.jar --nogui
```
