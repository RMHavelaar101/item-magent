# Marketplace Launch Guide

Step-by-step instructions to publish ItemMagnet v1.0.0.

## Pre-launch checklist

- [ ] `./gradlew build` passes locally and in CI
- [ ] Beta tested on Theryn (Lands member-only, WG blacklist, fuel, AFK)
- [ ] bStats plugin ID configured (optional)
- [ ] Gameplay screenshots/GIF captured (see `media/README.md`)
- [ ] GitHub repo is public at `github.com/RMHavelaar101/item-magent`

## 1. GitHub Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions will build and attach `ItemMagnet-1.0.0.jar`.

## 2. Hangar (primary)

1. Create account at https://hangar.papermc.io
2. Create project: namespace `RMHavelaar101`, name `ItemMagnet`
3. Upload avatar: `branding/icon-256.png`
4. Paste page body from `docs/marketplace/hangar-page.md`
5. Set platform: Paper `26.1`
6. Add soft dependencies: Lands, WorldGuard, CMI (all optional)
7. Upload JAR from GitHub Release
8. For auto-publish, add `HANGAR_API_TOKEN` to GitHub repo secrets

## 3. SpigotMC (secondary)

1. Go to https://www.spigotmc.org/resources/authors/
2. Create resource in **Spigot** category
3. Title: `ItemMagnet — Claim-Aware Tiered Item Magnets with Redstone Fuel`
4. Upload icon: `branding/icon-128.png`
5. Paste description from `docs/marketplace/spigot-description.bbcode`
6. Upload screenshots/GIF via SpigotMC image uploader
7. Set tested versions: `26.1.2`
8. Upload same JAR as GitHub Release
9. Link dev builds to Hangar if desired

## 4. Theryn beta deployment

1. SFTP `ItemMagnet-1.0.0.jar` to Bisect `plugins/`
2. Set `preset: theryn` or copy `presets/theryn.yml` into config
3. Restart server
4. Verify checklist from plan (Lands, WG, fuel, AFK, underground radius)
5. Run Spark profiler with 5+ players using magnets

## 5. Post-launch

- Monitor GitHub Issues
- Respond to Hangar/SpigotMC comments
- Tag `v1.0.1` for bugfix releases

## Update notifier

Players with `itemmagnet.updates` see a one-line notice on join when a new GitHub release is available. Link points to Hangar.
