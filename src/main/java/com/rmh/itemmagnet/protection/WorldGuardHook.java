package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.RegionMode;
import com.rmh.itemmagnet.config.WorldGuardConfig;
import com.rmh.itemmagnet.config.WorldGuardWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public final class WorldGuardHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object regionQuery;
    private Object worldGuardPlugin;
    private Object itemPickupFlag;
    private Method getApplicableRegionsMethod;
    private Method adaptMethod;
    private Method wrapPlayerMethod;
    private Method testStateOnQueryMethod;
    private Method getIdMethod;

    public WorldGuardHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return;
        }
        try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object platform = worldGuardClass.getMethod("getInstance").invoke(null);
            platform = platform.getClass().getMethod("getPlatform").invoke(platform);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            regionQuery = container.getClass().getMethod("createQuery").invoke(container);

            Class<?> wgPluginClass = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            worldGuardPlugin = wgPluginClass.getMethod("inst").invoke(null);

            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            itemPickupFlag = flagsClass.getField("ITEM_PICKUP").get(null);

            Class<?> adapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            adaptMethod = adapterClass.getMethod("adapt", Location.class);
            wrapPlayerMethod = wgPluginClass.getMethod("wrapPlayer", Player.class);

            Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.protection.regions.RegionQuery");
            Class<?> weLocationClass = Class.forName("com.sk89q.worldedit.util.Location");
            getApplicableRegionsMethod = regionQueryClass.getMethod("getApplicableRegions", weLocationClass);

            Class<?> localPlayerClass = Class.forName("com.sk89q.worldguard.LocalPlayer");
            Class<?> stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
            Class<?> stateFlagArrayClass = java.lang.reflect.Array.newInstance(stateFlagClass, 0).getClass();
            testStateOnQueryMethod = regionQueryClass.getMethod(
                    "testState",
                    weLocationClass,
                    localPlayerClass,
                    stateFlagArrayClass
            );

            Class<?> protectedRegionClass = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
            getIdMethod = protectedRegionClass.getMethod("getId");
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize WorldGuard hook: " + exception.getMessage());
            regionQuery = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return regionQuery != null && worldGuardPlugin != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        WorldGuardConfig config = plugin.getConfigManager().getMagnetConfig().getWorldGuard();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.regions")) {
            return true;
        }

        try {
            Object localPlayer = wrapPlayerMethod.invoke(worldGuardPlugin, player);
            Object wgLocation = adaptMethod.invoke(null, location);

            if (config.isRespectItemPickupFlag()
                    && !(boolean) testStateOnQueryMethod.invoke(
                            regionQuery,
                            wgLocation,
                            localPlayer,
                            new Object[] { itemPickupFlag }
                    )) {
                return false;
            }

            Object regions = getApplicableRegionsMethod.invoke(regionQuery, wgLocation);

            RegionMode mode = config.getRegionMode();
            List<String> regionList = config.getRegions();
            WorldGuardWorldConfig worldOverride = config.getWorlds().get(location.getWorld().getName());
            if (worldOverride != null) {
                mode = worldOverride.getRegionMode();
                regionList = worldOverride.getRegions();
            }

            if (mode == RegionMode.NONE || regionList.isEmpty()) {
                return true;
            }

            boolean inListedRegion = isInListedRegion(regions, regionList);
            return switch (mode) {
                case WHITELIST -> inListedRegion;
                case BLACKLIST -> !inListedRegion;
                default -> true;
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("WorldGuard evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean isInListedRegion(Object regions, List<String> regionList) throws ReflectiveOperationException {
        Iterable<?> iterable = (Iterable<?>) regions.getClass().getMethod("iterator").invoke(regions);
        for (Object region : iterable) {
            String id = (String) getIdMethod.invoke(region);
            for (String configured : regionList) {
                if (id.equalsIgnoreCase(configured)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reload() {
        initialize();
    }
}
