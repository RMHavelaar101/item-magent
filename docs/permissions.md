# Permissions

| Node | Default | Description |
|------|---------|-------------|
| `itemmagnet.use` | `true` | Use item magnets |
| `itemmagnet.craft.fragment` | `true` | Craft fragment tier |
| `itemmagnet.craft.survey` | `true` | Craft survey tier |
| `itemmagnet.craft.anchor` | `true` | Craft anchor tier |
| `itemmagnet.wilderness` | `op` | Use magnet in Lands wilderness |
| `itemmagnet.bypass.regions` | `false` | Skip WorldGuard region lists |
| `itemmagnet.bypass.lands` | `false` | Skip Lands checks |
| `itemmagnet.admin` | `op` | Parent admin permission |
| `itemmagnet.reload` | `op` | Reload configuration |
| `itemmagnet.give` | `op` | Give magnet items |
| `itemmagnet.unlock` | `op` | Unlock recipes for players |
| `itemmagnet.debug` | `op` | Debug command |
| `itemmagnet.updates` | `op` | Update notifications |

## LuckPerms examples

```bash
# Allow magnet use for default group
lp group default permission set itemmagnet.use true

# Restrict survey crafting to a rank
lp group seeker permission set itemmagnet.craft.survey true

# Allow wilderness use for VIP
lp group vip permission set itemmagnet.wilderness true
```

## Unlock permissions

For `unlock.type: COMMAND`, you can also grant:

```
itemmagnet.unlock.<tier>
```

Example: `itemmagnet.unlock.fragment`
