# Developer API

ItemMagnet exposes Bukkit events, a static API accessor, and integration status for addons.

## Access

```java
import com.rmh.itemmagnet.api.ItemMagnetApi;

if (ItemMagnetApi.isEnabled()) {
    ItemMagnetApi.grantUnlock(player, "fragment");
    ItemMagnetApi.giveMagnet(player, "fragment", 500);
}
```

| Method | Description |
|--------|-------------|
| `isEnabled()` | Whether ItemMagnet is loaded and enabled |
| `getPlugin()` | Raw `ItemMagnetPlugin` instance |
| `grantUnlock(player, tierId)` | Unlock a tier recipe for a player |
| `giveMagnet(player, tierId, charge)` | Give a magnet item (`charge < 0` uses half max charge) |
| `isTierUnlocked(player, tierId)` | Check unlock state |
| `getHookStatus()` | Map of integration name → detected |

Declare `softdepend: [ItemMagnet]` in your `plugin.yml`. Use `compileOnly` against `ItemMagnet-*-plain.jar` — do not shade ItemMagnet into your plugin.

## Events

| Event | When | Cancellable |
|-------|------|-------------|
| `ItemMagnetPullEvent` | Before an item entity is stepped toward a player | Yes |
| `ItemMagnetFuelAbsorbEvent` | Before fuel is absorbed from the ground | Yes |
| `ItemMagnetExperiencePullEvent` | Before an XP orb is pulled or collected | Yes |
| `ItemMagnetDepleteEvent` | When charge reaches zero | No |
| `MagnetTierUnlockedEvent` | When a tier is granted via unlock command/API | No |

### Example: react to tier unlocks

```java
@EventHandler
public void onTierUnlock(MagnetTierUnlockedEvent event) {
    getLogger().info(event.getPlayer().getName() + " unlocked " + event.getTierId());
}
```

## PlaceholderAPI

When PlaceholderAPI 2.12.2+ is installed:

| Placeholder | Value |
|-------------|-------|
| `%itemmagnet_active%` | `true` / `false` |
| `%itemmagnet_tier%` | Active tier id |
| `%itemmagnet_charge%` | Current charge |
| `%itemmagnet_max_charge%` | Max charge |
| `%itemmagnet_radius%` | Effective pull radius |
| `%itemmagnet_boost%` | Boost level |
