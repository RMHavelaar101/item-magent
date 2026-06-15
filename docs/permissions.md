# Permissions

| Node | Default | Description |
|------|---------|-------------|
| `itemmagnet.use` | `true` | Use item magnets (parent) |
| `itemmagnet.use.fragment` | `true` | Use fragment tier |
| `itemmagnet.use.survey` | `true` | Use survey tier |
| `itemmagnet.use.anchor` | `true` | Use anchor tier |
| `itemmagnet.filter` | `true` | Open personal item filter GUI (`/itemmagnet filter`) |
| `itemmagnet.craft.fragment` | `true` | Craft fragment tier |
| `itemmagnet.craft.survey` | `true` | Craft survey tier |
| `itemmagnet.craft.anchor` | `true` | Craft anchor tier |
| `itemmagnet.wilderness` | `op` | Use magnet in wilderness (Lands/Towny) |
| `itemmagnet.bypass.regions` | `false` | Skip WorldGuard region lists |
| `itemmagnet.bypass.lands` | `false` | Skip Lands checks |
| `itemmagnet.bypass.towny` | `false` | Skip Towny checks |
| `itemmagnet.bypass.griefprevention` | `false` | Skip GriefPrevention checks |
| `itemmagnet.admin` | `op` | Parent admin permission |
| `itemmagnet.reload` | `op` | Reload configuration |
| `itemmagnet.give` | `op` | Give magnet items |
| `itemmagnet.unlock` | `op` | Unlock recipes for players |
| `itemmagnet.import` | `op` | Import materials into server blacklist |
| `itemmagnet.updates` | `op` | Update notifications |
| `itemmagnet.config` | `op` | Open admin config GUI |
| `itemmagnet.config.save` | `op` | Save & reload from config GUI |
| `itemmagnet.config.reset` | `op` | Reset config to defaults from GUI |
| `itemmagnet.config.section.<id>` | `op` | Access an entire config GUI section |
| `itemmagnet.config.field.<path>` | — | Edit a single config GUI control (`settings_scan-interval-ticks`, etc.) |

### Config GUI section IDs

`settings`, `sounds`, `anti-afk`, `height`, `fuel`, `tiers`, `integrations`, `proximity-lore`, `item-filter`, `commands`, `startup-message`

Grant `itemmagnet.config` for full access (legacy behavior). Use section or field nodes to give junior staff partial access. Explicit `false` on a section or field denies that control even when `itemmagnet.config` is granted.

Custom tiers should define matching `itemmagnet.use.<tier>` nodes.

## LuckPerms examples

```bash
# VIP gets survey tier only
lp group vip permission set itemmagnet.use.survey true
lp group default permission set itemmagnet.use.survey false

# Allow wilderness use for VIP
lp group vip permission set itemmagnet.wilderness true

# Junior admin: settings only, no save
lp user helper permission set itemmagnet.config true
lp user helper permission set itemmagnet.config.section.settings true
lp user helper permission unset itemmagnet.config.save

# Players manage their own magnet item blacklist
lp group default permission set itemmagnet.filter true
```

## Unlock permissions

For `unlock.type: COMMAND`, grant via command or permission:

```
itemmagnet.unlock.<tier>
```

Example: `itemmagnet.unlock.fragment`
