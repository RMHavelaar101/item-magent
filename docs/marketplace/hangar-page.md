# ItemMagnet

**Tiered item magnets with redstone fuel, claim-aware protection, and visible pull physics.**

> **Paper 26.1+** · **Java 25** · Soft deps: Lands, WorldGuard, CMI (all optional)

![Demo](https://raw.githubusercontent.com/rmh/item-magnet/main/media/demo/item-magnet-demo.gif)

## Features

- Three configurable magnet tiers (vanilla compass, recovery compass, clock)
- Visible item pull with line-of-sight — no phasing through walls
- Redstone dust fuel + redstone block power surges
- Shift+right-click off-hand recharge
- Lands: wilderness, owner-only, member-only, respect flags
- WorldGuard: item-pickup flag, region whitelist/blacklist
- Recipe unlock gates: permission, advancement, CMI stat/rank, command
- Anti-AFK movement check
- Underground vs surface radius modifiers

## Quick start

1. Download and place in `plugins/`
2. Restart server
3. `/itemmagnet give <player> fragment 500`
4. Shift+right-click with redstone in off-hand to fuel
5. Hold magnet in main hand near dropped items

## Documentation

Full docs: https://github.com/RMHavelaar101/item-magent/tree/main/docs

## Commands

| Command | Description |
|---------|-------------|
| `/itemmagnet reload` | Reload config |
| `/itemmagnet version` | Version and hooks |
| `/itemmagnet give <player> <tier> [charge]` | Give magnet |
| `/itemmagnet unlock <player> <tier>` | Unlock recipe |
| `/itemmagnet debug` | Debug held magnet |

## Permissions

- `itemmagnet.use` — use magnets (default: true)
- `itemmagnet.admin` — admin commands (default: op)
- `itemmagnet.wilderness` — use in Lands wilderness (default: op)

See full permission list in docs.

## Support

Report issues: https://github.com/RMHavelaar101/item-magent/issues
