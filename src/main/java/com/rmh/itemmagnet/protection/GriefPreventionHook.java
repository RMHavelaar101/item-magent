package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ClaimedLandPolicy;
import com.rmh.itemmagnet.config.GriefPreventionConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class GriefPreventionHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object dataStore;
    private Method getClaimAtMethod;
    private Method getOwnerIdMethod;
    private Method allowAccessMethod;

    public GriefPreventionHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") == null) {
            return;
        }
        try {
            Class<?> gpClass = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
            Object griefPrevention = gpClass.getField("instance").get(null);
            dataStore = gpClass.getField("dataStore").get(griefPrevention);

            Class<?> dataStoreClass = Class.forName("me.ryanhamshire.GriefPrevention.DataStore");
            Class<?> claimClass = Class.forName("me.ryanhamshire.GriefPrevention.Claim");
            getClaimAtMethod = dataStoreClass.getMethod("getClaimAt", Location.class, boolean.class, claimClass);
            getOwnerIdMethod = claimClass.getMethod("getOwnerID");
            allowAccessMethod = claimClass.getMethod("allowAccess", Player.class);
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize GriefPrevention hook: " + exception.getMessage());
            dataStore = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return dataStore != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        GriefPreventionConfig config = plugin.getConfigManager().getMagnetConfig().getGriefPrevention();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.griefprevention")) {
            return true;
        }

        try {
            Object claim = getClaimAtMethod.invoke(dataStore, location, false, null);
            if (claim == null) {
                return true;
            }

            return switch (config.getClaimedLand()) {
                case DENY -> false;
                case OWNER_ONLY -> {
                    UUID ownerId = (UUID) getOwnerIdMethod.invoke(claim);
                    yield player.getUniqueId().equals(ownerId);
                }
                case MEMBER_ONLY, RESPECT_FLAGS -> (boolean) allowAccessMethod.invoke(claim, player);
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("GriefPrevention evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    public void reload() {
        initialize();
    }
}
