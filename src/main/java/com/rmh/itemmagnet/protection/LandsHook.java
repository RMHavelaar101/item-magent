package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ClaimedLandPolicy;
import com.rmh.itemmagnet.config.LandsConfig;
import com.rmh.itemmagnet.config.WildernessPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class LandsHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object landsIntegration;
    private Object itemPickupFlag;
    private Method getAreaMethod;
    private Method getLandPlayerMethod;
    private Method hasRoleFlagMethod;
    private Method getOwnerUidMethod;
    private Method isTrustedMethod;

    public LandsHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("Lands") == null) {
            return;
        }
        try {
            Class<?> integrationClass = Class.forName("me.angeschossen.lands.api.LandsIntegration");
            landsIntegration = integrationClass.getMethod("of", org.bukkit.plugin.Plugin.class).invoke(null, plugin);

            Class<?> flagsClass = Class.forName("me.angeschossen.lands.api.flags.type.Flags");
            itemPickupFlag = flagsClass.getField("ITEM_PICKUP").get(null);

            getAreaMethod = integrationClass.getMethod("getArea", Location.class);
            getLandPlayerMethod = integrationClass.getMethod("getLandPlayer", UUID.class);

            Class<?> areaClass = Class.forName("me.angeschossen.lands.api.land.Area");
            hasRoleFlagMethod = areaClass.getMethod("hasRoleFlag", UUID.class, itemPickupFlag.getClass());
            getOwnerUidMethod = areaClass.getMethod("getOwnerUID");
            isTrustedMethod = areaClass.getMethod("isTrusted", UUID.class);
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize Lands hook: " + exception.getMessage());
            landsIntegration = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return landsIntegration != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        LandsConfig config = plugin.getConfigManager().getMagnetConfig().getLands();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.lands")) {
            return true;
        }

        return evaluate(player, location, config);
    }

    private boolean evaluate(Player player, Location location, LandsConfig config) {
        try {
            Object area = getAreaMethod.invoke(landsIntegration, location);
            if (area == null) {
                return handleWilderness(player, config);
            }

            return switch (config.getClaimedLand()) {
                case DENY -> false;
                case OWNER_ONLY -> player.getUniqueId().equals(getOwnerUidMethod.invoke(area));
                case MEMBER_ONLY -> (boolean) isTrustedMethod.invoke(area, player.getUniqueId());
                case RESPECT_FLAGS -> (boolean) hasRoleFlagMethod.invoke(area, player.getUniqueId(), itemPickupFlag);
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Lands evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean handleWilderness(Player player, LandsConfig config) {
        return switch (config.getWilderness()) {
            case ALLOW -> true;
            case DENY -> false;
            case PERMISSION -> player.hasPermission(config.getWildernessPermission());
        };
    }

    public void reload() {
        initialize();
    }
}
