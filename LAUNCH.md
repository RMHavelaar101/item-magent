# Marketplace Launch Guide

Step-by-step instructions to publish **ItemMagnet v1.2.1**.

## Pre-launch checklist

- [ ] `./gradlew build` passes locally and in CI
- [ ] Beta tested on Theryn (Lands, WG, fuel transfer, config GUI, AFK)
- [ ] Gameplay screenshots/GIF captured (see `media/README.md`) — **you handle this**
- [ ] GitHub repo public: `github.com/RMHavelaar101/item-magent`
- [ ] Review listing copy: `docs/marketplace/spigot-description.bbcode`, `hangar-page.md`

## 1. GitHub Release

```bash
git tag v1.2.1
git push origin v1.2.1
```

Attach `build/libs/ItemMagnet-1.2.1.jar` (CI or manual upload).

## 2. Hangar (recommended primary)

1. Create account at https://hangar.papermc.io
2. Create project: namespace `RMHavelaar101`, name `ItemMagnets`
3. **Avatar:** `branding/icon-256.png`
4. **Description:** paste from `docs/marketplace/hangar-page.md`
5. **Platforms:** Paper — `1.21.1`, `1.21.4`, `26.1`
6. **Dependencies:** Lands, WorldGuard, Towny, GriefPrevention, PlaceholderAPI, CMI (all optional)
7. Upload JAR from GitHub Release
8. Add demo GIF to description once captured (`media/demo/item-magnet-demo.gif`)
9. For auto-publish, add `HANGAR_API_TOKEN` to GitHub repo secrets

## 3. SpigotMC (secondary)

1. Go to https://www.spigotmc.org/resources/authors/
2. Create resource — category: **Mechanics** (or Fun)
3. **Title:** `ItemMagnet — Tiered Item Magnets, Redstone Fuel, Claim Protection`
4. **Icon:** `branding/icon-128.png`
5. **Description:** paste BBCode from `docs/marketplace/spigot-description.bbcode`
6. **Tested Minecraft versions:** `1.21.1`, `1.21.4` (add more as verified)
7. **Tested server:** Paper (not Spigot)
8. **Java version:** 21
9. Upload gallery images from `media/screenshots/` + demo GIF
10. Upload same JAR as GitHub Release
11. **External link:** GitHub or Hangar for updates

## 4. Production deployment (Theryn)

1. SFTP `ItemMagnet-1.2.1.jar` to `plugins/`
2. Set `preset: theryn` or merge `presets/theryn.yml`
3. Restart server (or hot-reload config if only YAML changed)
4. Verify: fuel transfer, Lands boundary, `/itemmagnet config`, `/itemmagnet debug`

## 5. Post-launch

- Monitor GitHub Issues
- Respond to Hangar/SpigotMC comments
- Tag `v1.2.2` for bugfix releases

## Copy & docs map

| Asset | Location |
|-------|----------|
| GitHub README | `README.md` |
| Spigot listing | `docs/marketplace/spigot-description.bbcode` |
| Hangar page | `docs/marketplace/hangar-page.md` |
| Version matrix | `docs/marketplace/compatibility.md` |
| Image checklist | `docs/marketplace/PUBLISHING.md` |
| Screenshot guide | `media/README.md` |

## Update notifier

Players with `itemmagnet.updates` see a one-line notice on join when a new GitHub release is available. Link points to Hangar.

## Version compatibility (summary)

| | |
|---|---|
| **Paper** | 1.21.1+ required |
| **Java** | 21+ required |
| **Tested** | 1.21.1, 1.21.4, 26.1 |
| **Not supported** | Spigot, Folia |

Full details: `docs/marketplace/compatibility.md`
