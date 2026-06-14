# Developer API

ItemMagnet exposes Bukkit events and a plugin accessor for addons.

## Access

```java
import com.rmh.itemmagnet.api.ItemMagnetApi;

ItemMagnetPlugin plugin = ItemMagnetApi.getPlugin();
```

## Events

| Event | When | Cancellable |
|-------|------|-------------|
| `ItemMagnetPullEvent` | Before an item entity is stepped toward a player | Yes |
| `ItemMagnetFuelAbsorbEvent` | Before fuel is absorbed from the ground | Yes |
| `ItemMagnetExperiencePullEvent` | Before an XP orb is pulled or collected | Yes |
| `ItemMagnetDepleteEvent` | When charge reaches zero | No |

### Example: block pulling diamonds

```java
@EventHandler
public void onPull(ItemMagnetPullEvent event) {
    if (event.getItemStack().getType() == Material.DIAMOND) {
        event.setCancelled(true);
    }
}
```

Register your listener in `plugin.yml` — events use the standard Bukkit event bus.

## PlaceholderAPI

When PlaceholderAPI is installed:

| Placeholder | Value |
|-------------|-------|
| `%itemmagnet_active%` | `true` / `false` |
| `%itemmagnet_tier%` | Active tier id |
| `%itemmagnet_charge%` | Current charge |
| `%itemmagnet_max_charge%` | Max charge |
| `%itemmagnet_radius%` | Effective pull radius |
| `%itemmagnet_boost%` | Boost level |
