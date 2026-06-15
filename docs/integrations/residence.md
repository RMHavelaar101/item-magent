# Residence Integration

ItemMagnet integrates with [Residence](https://www.spigotmc.org/resources/11480/) via reflection.

## Enable

```yaml
integrations:
  residence:
    enabled: true
    wilderness: ALLOW
    claimed-land: RESPECT_FLAGS
```

## claimed-land

| Value | Behavior |
|-------|----------|
| `RESPECT_FLAGS` | Uses Residence `itempickup` flag |
| `OWNER_ONLY` | Owner UUID must match player |
| `MEMBER_ONLY` | Same as `RESPECT_FLAGS` for Residence |
| `DENY` | Never pull in claimed residences |

## Bypass

`itemmagnet.bypass.residence`

## Tested version

Residence **6.0.0.1** (requires CMILib 5.x+).
