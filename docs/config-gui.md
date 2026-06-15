# Config GUI

Open the in-game editor with `/itemmagnet config` or `/im config` (permission: `itemmagnet.config`, default `op`).

## Main menu extras

| Control | What it does |
|---------|----------------|
| Startup thank-you message | Toggle the console message logged on plugin enable |
| Reset all to defaults | **Shift-click** to restore `config.yml` from plugin defaults (cannot be undone) |

## Sections

| Menu | What you can edit |
|------|-------------------|
| Settings | Scan interval, pull physics, hold-mode, fuel radius, arm swing, game mode disables |
| Sounds | Enable sounds and view sound names |
| Anti-AFK | Enable AFK, movement window, notify-once, auto-fuel block |
| Height | Y range and underground modifiers |
| Fuel | Per-material charge, boost, radius bonus |
| Tiers | **Display names**, radius, drain, max charge, pull-experience per tier |
| Integrations | Enable Lands, WorldGuard, Towny, GriefPrevention |
| Proximity Lore | Enable feature, scan interval, cooldown, require magnet; edit zone coords |

Each button includes a short description of what the setting does.

## Proximity lore

**Proximity Lore** menu (main hub → sculk sensor icon):

- Toggle `enabled` and `require-active-magnet`
- Adjust scan interval and per-zone cooldown
- **Zones** — list configured zone IDs; click to edit world (chat), X/Y/Z, radius, and Y tolerance
- Message text and per-tier overrides remain in `config.yml` (see [proximity-lore.md](proximity-lore.md))

## Renaming items

In **Tiers → select a tier → Display name**, click the name tag button and type the new name in chat. Use `&` color codes (e.g. `&6Gold Magnet`). Type `cancel` to abort without saving.

Re-give magnets after renaming so existing items pick up the new name.

## Save & Reload

Click **Save & Reload** (lime concrete) to write `config.yml` and run `/itemmagnet reload` logic. Most changes apply immediately.

## Presets

If `preset: testing` (or another preset) is set, preset values merge on reload and may override GUI edits. Set `preset: none` from the main hub indicator or in `config.yml` when doing long-term edits.

## Tier materials

Changing a tier's `material` only affects **new** magnets. Re-give items after material changes.

## LuckPerms

Use existing permission nodes; enable `commands.filter-by-permission: true` so players only see commands they can use in help and tab completion.
