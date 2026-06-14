# Media assets

## Included (ready for publish)

| File | Use |
|------|-----|
| `branding/icon-128.png` | SpigotMC resource icon |
| `branding/icon-256.png` | Hangar avatar |
| `branding/banner-1280x640.png` | GitHub README hero |
| `branding/icon.svg` | Source icon |
| `branding/palette.md` | Brand colors |

## Capture on your Paper server (you provide these)

Use Paper **1.21.1+** on a clean survival world. Vanilla shaders off, F1 HUD hidden for beauty shots.

| File | Content | Priority |
|------|---------|----------|
| `screenshots/01-tiers-inventory.png` | Three tiers in inventory showing lore (charge, range, boost) | High |
| `screenshots/02-items-pulling.gif` | Items sliding around a corner — hero shot | **Required** |
| `screenshots/03-fuel-transfer.png` | Sneak + right-click fuel or boost-active chat message | High |
| `screenshots/04-claim-boundary.png` | Pull stopping at Lands or WorldGuard border | High |
| `screenshots/05-config-gui.png` | `/itemmagnet config` main menu or tier editor | High |
| `demo/item-magnet-demo.gif` | 15–30s loop: hold magnet → items pull → fuel → surge | **Required** |

### Capture tips

1. **Tiers shot** — `/itemmagnet giveall YourName 750` then open inventory
2. **Pull GIF** — drop items behind a wall corner; show them sliding around
3. **Fuel shot** — redstone in off-hand, sneak+RClick; capture action bar or chat boost message
4. **Claim shot** — stand at Lands/WG edge; drop item on far side of border
5. **GUI shot** — `/itemmagnet config` as op; show tier list with display names
6. **Demo GIF** — combine pull + fuel + tier swap in one short loop

### Upload

- Commit to `media/` on GitHub for stable raw URLs
- Or use SpigotMC image uploader for listing body
- Do **not** use Discord CDN links (they expire)

### Suggested Spigot gallery order

1. `02-items-pulling.gif` (first — catches attention)
2. `01-tiers-inventory.png`
3. `05-config-gui.png`
4. `03-fuel-transfer.png`
5. `04-claim-boundary.png`
