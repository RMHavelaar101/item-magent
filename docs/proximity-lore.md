# Proximity lore (optional)

Send ambient chat messages when players holding an active magnet enter configured coordinate zones. Useful for quest hints, dungeon atmosphere, landmark discovery, or RPG flavor — **without** tying into TherynFragments, ExecutableBlocks, or WorldGuard.

**Default:** `enabled: false` — omit the section or leave disabled and ItemMagnet behaves exactly as before.

---

## Quick example

```yaml
proximity-lore:
  enabled: true
  scan-interval-ticks: 40
  require-active-magnet: true
  cooldown-seconds: 90
  zones:
    ancient_ruins:
      world: world
      x: 1200
      y: 64
      z: -800
      radius: 12
      y-tolerance: 10
      messages:
        - "&8The resonator hums: &7The silence here is older than the village."
      tier-messages:
        fragment:
          - "&8The Fragment Resonator trembles: &7something beneath listens."
        survey:
          - "&8The Survey Resonator aligns: &7echoes repeat in the stone."
```

Reload: `/itemmagnet reload`

---

## Options

| Key | Default | Description |
|-----|---------|-------------|
| `enabled` | `false` | Master switch |
| `scan-interval-ticks` | `40` | How often to check players (separate from pull tick) |
| `require-active-magnet` | `true` | Player must be holding/using a magnet tier |
| `cooldown-seconds` | `90` | Per-player, per-zone message cooldown |
| `zones.<id>.world` | — | Bukkit world name |
| `zones.<id>.x/y/z` | — | Zone center |
| `zones.<id>.radius` | — | Horizontal match distance |
| `zones.<id>.y-tolerance` | `8` | Vertical tolerance from center Y |
| `zones.<id>.messages` | — | Random message pool (supports `&` color codes) |
| `zones.<id>.tier-messages.<tierId>` | — | Optional overrides when player holds that tier |

---

## Design notes

- **Pure coordinates** — no plugin dependencies; portable across servers.
- **Tier messages** — optional flavor when a player holds `fragment`, `survey`, `anchor`, or any custom tier id.
- **Performance** — lightweight scan only when enabled; uses its own interval, not the pull loop.
- **Public servers** — ship with `enabled: false` in default `config.yml`; enable only on worlds where you want ambient lore.

Theryn preset includes a **commented** example in `presets/theryn.yml` — copy into live config when deploying on the Theryn server.

---

## Testing

1. `/itemmagnet give <you> fragment 500`
2. Hold the magnet and walk into a configured zone
3. Expect a whisper-style message; repeat within cooldown — silent until cooldown expires
