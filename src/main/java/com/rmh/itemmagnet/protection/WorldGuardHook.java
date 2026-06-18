package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.RegionMode;
import com.rmh.itemmagnet.config.WorldGuardConfig;
import com.rmh.itemmagnet.config.WorldGuardWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
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
    private Class<?> stateFlagClass;

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
            stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
            Class<?> stateFlagArrayClass = Array.newInstance(stateFlagClass, 0).getClass();
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
                            createStateFlagArray(itemPickupFlag)
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
        } catch (RuntimeException exception) {
            plugin.getLogger().warning("WorldGuard evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private Object createStateFlagArray(Object flag) {
        Object array = Array.newInstance(stateFlagClass, 1);
        Array.set(array, 0, flag);
        return array;
    }

    private boolean isInListedRegion(Object regions, List<String> regionList) throws ReflectiveOperationException {
        if (regions instanceof Iterable<?> iterable) {
            for (Object region : iterable) {
                if (regionMatches(region, regionList)) {
                    return true;
                }
            }
            return false;
        }

        Method getRegionsMethod = regions.getClass().getMethod("getRegions");
        Object regionCollection = getRegionsMethod.invoke(regions);
        if (regionCollection instanceof Iterable<?> iterable) {
            for (Object region : iterable) {
                if (regionMatches(region, regionList)) {
                    return true;
                }
            }
            return false;
        }

        Iterator<?> iterator = (Iterator<?>) regions.getClass().getMethod("iterator").invoke(regions);
        while (iterator.hasNext()) {
            if (regionMatches(iterator.next(), regionList)) {
                return true;
            }
        }
        return false;
    }

    private boolean regionMatches(Object region, List<String> regionList) throws ReflectiveOperationException {
        String id = (String) getIdMethod.invoke(region);
        for (String configured : regionList) {
            if (id.equalsIgnoreCase(configured)) {
                return true;
            }
        }
        return false;
    }

    public void reload() {
        initialize();
    }
}
