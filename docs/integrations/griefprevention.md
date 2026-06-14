# GriefPrevention integration

Optional reflection-based hook — GriefPrevention is not required at compile time.

## Enable

```yaml
integrations:
  griefprevention:
    enabled: true
    claimed-land: MEMBER_ONLY
```

## Options

| Key | Values | Description |
|-----|--------|-------------|
| `claimed-land` | `RESPECT_FLAGS`, `OWNER_ONLY`, `MEMBER_ONLY`, `DENY` | Behaviour inside claims |

Unclaimed wilderness is always allowed when GP integration is enabled.

## Bypass

Players with `itemmagnet.bypass.griefprevention` skip GriefPrevention checks.
