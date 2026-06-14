# Troubleshooting

## Plugin does not enable

- Confirm **Paper 26.1+** (not Spigot-only builds)
- Confirm **Java 25**
- Check console for stack traces on startup

## Magnet does not pull items

1. Hold magnet in **main hand** (not off-hand)
2. Confirm charge > 0 (`/itemmagnet debug`)
3. Confirm not sneaking (if `sneak-to-disable: true`)
4. Check AFK settings — move around if anti-AFK is enabled
5. Check Y-range settings
6. Verify `itemmagnet.use` permission

## "You cannot pull items here"

Protection denied the pull:

1. Run `/itemmagnet debug` — check `Can pull at feet`
2. Review Lands `wilderness` / `claimed-land` settings
3. Review WorldGuard region mode and blacklist
4. Test with `itemmagnet.bypass.lands` / `itemmagnet.bypass.regions` (admin only)

## Recipe not visible

1. Check `unlock.type` for the tier
2. Grant manually: `/itemmagnet unlock <player> <tier>`
3. For advancements, confirm the advancement exists and is completed

## Performance concerns

- Lower `max-items-per-tick` (default 10)
- Increase `scan-interval-ticks` (default 2)
- Use Spark (`/spark profiler`) while magnets are active
- Reduce tier radii

## Lands / WorldGuard not detected

Integration logs a warning if enabled in config but plugin is missing. Install the protection plugin or set `enabled: false`.

## bStats not reporting

Set a valid `metrics.bstats-plugin-id` from [bstats.org](https://bstats.org).

## Debug checklist

```
/itemmagnet version
/itemmagnet debug
/itemmagnet reload
```

Share console output and relevant config sections when reporting bugs.
