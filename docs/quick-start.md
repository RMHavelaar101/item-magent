# Quick Start

Get a working magnet in five minutes.

## 1. Install the plugin

See [installation.md](installation.md).

## 2. Give yourself a magnet

```
/itemmagnet give YourName fragment 500
```

## 3. Add fuel

Hold the **Fragment Resonator** in your **main hand**.

Hold **redstone dust** in your **off hand**.

**Shift + right-click** to transfer fuel into the magnet.

## 4. Test item pull

Drop items on the ground nearby. With a charged magnet active (see `hold-mode` in config), items should visibly slide toward you.

### Passive mode

Set `hold-mode: INVENTORY` in config to pull items while the magnet sits anywhere in your inventory — no need to hold it in your main hand.

## 5. Try a power surge

Drop a **redstone block** nearby (or shift+click with one in off-hand). Redstone blocks add more charge and temporarily boost pull radius.

## Sneak to disable

Hold sneak while using the magnet to pause pulling (useful when sorting inventory).

## Next steps

- Configure [Lands](integrations/lands.md) or [WorldGuard](integrations/worldguard.md) protection
- Set up [recipe unlocks](recipes-and-unlocks.md)
- Tune tiers in [configuration.md](configuration.md)
