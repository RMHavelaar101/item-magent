# Permissions

| Node | Default | Description |
|------|---------|-------------|
| `itemmagnet.use` | `true` | Use item magnets (parent) |
| `itemmagnet.use.fragment` | `true` | Use fragment tier |
| `itemmagnet.use.survey` | `true` | Use survey tier |
| `itemmagnet.use.anchor` | `true` | Use anchor tier |
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
| `itemmagnet.debug` | `op` | Debug command |
| `itemmagnet.updates` | `op` | Update notifications |

Custom tiers should define matching `itemmagnet.use.<tier>` nodes.

## LuckPerms examples

```bash
# VIP gets survey tier only
lp group vip permission set itemmagnet.use.survey true
lp group default permission set itemmagnet.use.survey false

# Allow wilderness use for VIP
lp group vip permission set itemmagnet.wilderness true
```

## Unlock permissions

For `unlock.type: COMMAND`, grant via command or permission:

```
itemmagnet.unlock.<tier>
```

Example: `itemmagnet.unlock.fragment`
