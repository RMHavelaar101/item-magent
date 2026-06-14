# Recipes and Unlocks

## Default tiers

| Tier | Material | Default name | Base radius |
|------|----------|--------------|-------------|
| `fragment` | Compass | Fragment Resonator | 6 |
| `survey` | Recovery Compass | Survey Resonator | 9 |
| `anchor` | Clock | Anchor Resonator | 12 |

All names, materials, stats, and recipes are configurable.

## Default fragment recipe

```
  E | A | E
  C | L | C
  R | D | R

E = Echo Shard
A = Amethyst Shard
C = Compass
L = Lodestone
R = Redstone Block
D = Diamond
```

## Recipe configuration

```yaml
tiers:
  fragment:
    recipe:
      enabled: true
      hidden: true
      shape:
        - "EAE"
        - "CLC"
        - "RDR"
      ingredients:
        E: ECHO_SHARD
        A: AMETHYST_SHARD
        C: COMPASS
        L: LODESTONE
        R: REDSTONE_BLOCK
        D: DIAMOND
```

## Unlock gates

See [CMI unlocks](integrations/cmi-unlocks.md) for all unlock types.

Recipes are hidden until unlocked. Players who meet the condition receive recipe discovery on join.

## Customizing for your server

1. Change `display-name` and `lore` for your theme
2. Adjust `recipe.ingredients` and `shape`
3. Set `unlock.type` to gate crafting behind progression
4. Use `/itemmagnet unlock` from quest plugins or CMI event commands
