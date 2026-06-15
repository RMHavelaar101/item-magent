package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ClaimedLandPolicy;
import com.rmh.itemmagnet.config.SuperiorSkyblockConfig;
import com.rmh.itemmagnet.config.WildernessPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class SuperiorSkyblockHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Class<?> apiClass;
    private Method getIslandAtMethod;
    private Method getOwnerMethod;
    private Method isMemberMethod;
    private Method hasPermissionMethod;

    public SuperiorSkyblockHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") == null) {
            return;
        }
        try {
            apiClass = Class.forName("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI");
            getIslandAtMethod = apiClass.getMethod("getIslandAt", Location.class);

            Class<?> islandClass = Class.forName("com.bgsoftware.superiorskyblock.api.island.Island");
            Class<?> superiorPlayerClass = Class.forName("com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer");
            getOwnerMethod = islandClass.getMethod("getOwner");
            isMemberMethod = islandClass.getMethod("isMember", superiorPlayerClass);
            hasPermissionMethod = islandClass.getMethod("hasPermission", superiorPlayerClass, String.class);

            Method getPlayerMethod = apiClass.getMethod("getPlayer", UUID.class);
            this.getPlayerMethod = getPlayerMethod;
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize SuperiorSkyblock hook: " + exception.getMessage());
            apiClass = null;
        }
    }

    private Method getPlayerMethod;

    @Override
    public boolean isAvailable() {
        return apiClass != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        SuperiorSkyblockConfig config = plugin.getConfigManager().getMagnetConfig().getSuperiorSkyblock();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.superiorskyblock")) {
            return true;
        }

        try {
            Object island = getIslandAtMethod.invoke(null, location);
            if (island == null) {
                return handleWilderness(player, config);
            }

            Object superiorPlayer = getPlayerMethod.invoke(null, player.getUniqueId());
            if (superiorPlayer == null) {
                return false;
            }

            return switch (config.getClaimedLand()) {
                case DENY -> false;
                case OWNER_ONLY -> {
                    Object owner = getOwnerMethod.invoke(island);
                    UUID ownerId = (UUID) owner.getClass().getMethod("getUniqueId").invoke(owner);
                    yield player.getUniqueId().equals(ownerId);
                }
                case MEMBER_ONLY -> (boolean) isMemberMethod.invoke(island, superiorPlayer);
                case RESPECT_FLAGS -> (boolean) hasPermissionMethod.invoke(
                        island, superiorPlayer, config.getIslandPermission()
                );
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("SuperiorSkyblock evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean handleWilderness(Player player, SuperiorSkyblockConfig config) {
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
