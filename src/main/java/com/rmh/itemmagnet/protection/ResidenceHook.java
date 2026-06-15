package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ClaimedLandPolicy;
import com.rmh.itemmagnet.config.SimpleClaimIntegrationConfig;
import com.rmh.itemmagnet.config.WildernessPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class ResidenceHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object residenceApi;
    private Method getByLocMethod;
    private Method getOwnerUuidMethod;
    private Method playerHasPermissionMethod;

    public ResidenceHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("Residence") == null) {
            return;
        }
        try {
            Class<?> residenceClass = Class.forName("com.bekvon.bukkit.residence.Residence");
            Method getInstance = residenceClass.getMethod("getInstance");
            Object residence = getInstance.invoke(null);
            residenceApi = residenceClass.getMethod("getAPI").invoke(residence);

            Class<?> apiClass = Class.forName("com.bekvon.bukkit.residence.api.ResidenceApi");
            Class<?> claimedResidenceClass = Class.forName("com.bekvon.bukkit.residence.protection.ClaimedResidence");
            getByLocMethod = apiClass.getMethod("getByLoc", Location.class);
            getOwnerUuidMethod = claimedResidenceClass.getMethod("getOwnerUUID");
            playerHasPermissionMethod = claimedResidenceClass.getMethod("playerHas", Player.class, String.class, boolean.class);
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize Residence hook: " + exception.getMessage());
            residenceApi = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return residenceApi != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        SimpleClaimIntegrationConfig config = plugin.getConfigManager().getMagnetConfig().getResidence();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.residence")) {
            return true;
        }

        try {
            Object residence = getByLocMethod.invoke(residenceApi, location);
            if (residence == null) {
                return handleWilderness(player, config);
            }

            return switch (config.getClaimedLand()) {
                case DENY -> false;
                case OWNER_ONLY -> {
                    UUID ownerId = (UUID) getOwnerUuidMethod.invoke(residence);
                    yield player.getUniqueId().equals(ownerId);
                }
                case MEMBER_ONLY, RESPECT_FLAGS -> (boolean) playerHasPermissionMethod.invoke(
                        residence, player, "itempickup", true
                );
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Residence evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean handleWilderness(Player player, SimpleClaimIntegrationConfig config) {
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
