# PlotSquared Integration

ItemMagnet integrates with [PlotSquared](https://www.spigotmc.org/resources/77506/) via reflection.

## Enable

```yaml
integrations:
  plotsquared:
    enabled: true
    wilderness: ALLOW
    claimed-land: MEMBER_ONLY
```

## Behaviour

- **Wilderness** (no plot at location): `wilderness` policy (`ALLOW`, `DENY`, `PERMISSION`)
- **Claimed plot**: `claimed-land` policy — `MEMBER_ONLY` checks `plot.isAdded(player)`

## Bypass

`itemmagnet.bypass.plotsquared`

## Tested version

PlotSquared **6.11.1** on Paper 1.21.x.
