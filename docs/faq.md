# FAQ

## Does the magnet pull items through walls?

No. ItemMagnet uses stepped movement with line-of-sight checks. Items stop when a solid block blocks the path.

## Does it work without Lands or WorldGuard?

Yes. Both are optional soft dependencies. With integrations disabled, only permission checks apply.

## Can I rename the items?

Yes. Set `display-name` and `lore` per tier in config. The default "Fragment Resonator" name is just a config default.

## How do I fuel the magnet?

1. **Shift + right-click** with redstone dust or block in your off-hand
2. **Auto-absorb** — drop redstone nearby while holding a charged magnet

## What do redstone blocks do vs dust?

- **Dust** — adds charge only
- **Block** — adds more charge plus a temporary radius boost (power surge)

## Can donors get a stronger magnet?

ItemMagnet does not include pay-to-win defaults. Server admins control tiers and unlock gates entirely via config.

## Does it affect mining stats / mcMMO?

No. The magnet only moves dropped item entities. It does not break blocks or alter mining XP.

## Does it work in wilderness?

Only if your Lands config allows it. Set `wilderness: DENY` to restrict to claims.

## Can I use it underground?

Yes. Optionally reduce radius underground via `height.underground` settings.

## How do I disable update notifications?

```yaml
metrics:
  update-check: DISABLED
```

Or remove `itemmagnet.updates` permission from staff.
