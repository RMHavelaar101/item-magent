# Publishing checklist

Everything you need to list ItemMagnet on Hangar and SpigotMC. **You only need to capture images** — copy and JAR are ready.

## Before you publish

- [ ] `./gradlew build` passes
- [ ] JAR: `build/libs/ItemMagnet-1.2.1.jar`
- [ ] GitHub repo public: `github.com/RMHavelaar101/item-magent`
- [ ] Tag release: `git tag v1.2.1 && git push origin v1.2.1`
- [ ] GitHub Release attached JAR (CI or manual upload)

## Images to capture

See [media/README.md](../../media/README.md) for capture tips.

| # | File | Use on |
|---|------|--------|
| 1 | `media/screenshots/01-tiers-inventory.png` | Spigot gallery, Hangar, README |
| 2 | `media/screenshots/02-items-pulling.gif` | Hero / first gallery slot |
| 3 | `media/screenshots/03-fuel-transfer.png` | Gallery |
| 4 | `media/screenshots/04-claim-boundary.png` | Gallery (Lands/WG selling point) |
| 5 | `media/screenshots/05-config-gui.png` | Gallery (admin GUI) |
| 6 | `media/demo/item-magnet-demo.gif` | Hangar header, Spigot optional embed |

**Already done:** `branding/icon-128.png`, `icon-256.png`, `banner-1280x640.png`

Upload screenshots to GitHub (`media/` folder) for stable raw URLs, or use SpigotMC's image uploader for the listing body.

## Hangar (recommended primary)

1. https://hangar.papermc.io — create project `Alcerious/ItemMagnets`
2. **Avatar:** `branding/icon-256.png`
3. **Description:** paste from [hangar-page.md](hangar-page.md)
4. **Platforms:** Paper — `1.21.1`, `1.21.4`, `26.1`
5. **Dependencies:** Lands, WorldGuard, Towny, GriefPrevention, PlaceholderAPI, CMI (all optional)
6. **Upload JAR** from GitHub Release
7. **Changelog:** link to [CHANGELOG.md](../../CHANGELOG.md) or paste 1.2.1 highlights

## SpigotMC (secondary)

1. https://www.spigotmc.org/resources/authors/ — **New resource**
2. **Category:** Minecraft → Mechanics (or Fun)
3. **Title:** `ItemMagnet — Tiered Item Magnets, Redstone Fuel, Claim Protection`
4. **Icon:** `branding/icon-128.png`
5. **Description:** paste BBCode from [spigot-description.bbcode](spigot-description.bbcode)
6. **Tested Minecraft versions:** `1.21.1`, `1.21.4` (add others as you verify)
7. **Tested server:** Paper (not Spigot)
8. **Java version:** 21
9. **Gallery:** upload 4–6 screenshots + GIF
10. **JAR:** same file as Hangar
11. **External link:** GitHub or Hangar for updates

## Listing copy locations

| Platform | File |
|----------|------|
| SpigotMC body | [spigot-description.bbcode](spigot-description.bbcode) |
| Hangar body | [hangar-page.md](hangar-page.md) |
| GitHub README | [README.md](../../README.md) |
| Version matrix | [compatibility.md](compatibility.md) |

## Suggested tagline (short)

> Tiered item magnets with redstone fuel, visible pull physics, and Lands/WorldGuard support.

## Support links (use in all listings)

- **Product site:** https://itemmagnet.theryn.org
- **Docs:** https://github.com/RMHavelaar101/item-magent/tree/main/docs
- **Issues:** https://github.com/RMHavelaar101/item-magent/issues
- **Discord:** (add if you create one)

## Post-publish

- [ ] Announce on your server / Discord
- [ ] Monitor GitHub Issues
- [ ] Respond to Hangar/Spigot reviews
- [ ] Tag `v1.2.2` for bugfixes only
