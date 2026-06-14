# Towny integration

Optional reflection-based hook — Towny is not required at compile time.

## Enable

```yaml
integrations:
  towny:
    enabled: true
    wilderness: DENY
    wilderness-permission: itemmagnet.wilderness
    claimed-town: MEMBER_ONLY
```

## Options

| Key | Values | Description |
|-----|--------|-------------|
| `wilderness` | `ALLOW`, `DENY`, `PERMISSION` | Unclaimed wilderness behaviour |
| `wilderness-permission` | permission node | Used when `wilderness: PERMISSION` |
| `claimed-town` | `RESPECT_FLAGS`, `OWNER_ONLY`, `MEMBER_ONLY`, `DENY` | Inside-town behaviour |

## Bypass

Players with `itemmagnet.bypass.towny` skip Towny checks.
