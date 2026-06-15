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

public final class PlotSquaredHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object plotApi;
    private Method getPlotAbsMethod;
    private Method isAddedMethod;
    private Method getOwnerMethod;

    public PlotSquaredHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            return;
        }
        try {
            Class<?> plotApiClass = Class.forName("com.plotsquared.core.PlotAPI");
            plotApi = plotApiClass.getConstructor().newInstance();

            Class<?> locationClass = Class.forName("com.plotsquared.core.location.Location");
            Class<?> blockLocClass = Class.forName("com.plotsquared.core.location.BlockLoc");
            Method atMethod = blockLocClass.getMethod("at", int.class, int.class, int.class);
            Method withWorldMethod = locationClass.getMethod("withWorld", String.class);

            Class<?> plotClass = Class.forName("com.plotsquared.core.plot.Plot");
            getPlotAbsMethod = plotApiClass.getMethod("getPlotAbs", locationClass);
            isAddedMethod = plotClass.getMethod("isAdded", UUID.class);
            getOwnerMethod = plotClass.getMethod("getOwner");

            this.atMethod = atMethod;
            this.withWorldMethod = withWorldMethod;
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize PlotSquared hook: " + exception.getMessage());
            plotApi = null;
        }
    }

    private Method atMethod;
    private Method withWorldMethod;

    @Override
    public boolean isAvailable() {
        return plotApi != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        SimpleClaimIntegrationConfig config = plugin.getConfigManager().getMagnetConfig().getPlotSquared();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.plotsquared")) {
            return true;
        }

        try {
            Object blockLoc = atMethod.invoke(null, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object plotLocation = withWorldMethod.invoke(blockLoc, location.getWorld().getName());
            Object plot = getPlotAbsMethod.invoke(plotApi, plotLocation);
            if (plot == null) {
                return handleWilderness(player, config);
            }

            return switch (config.getClaimedLand()) {
                case DENY -> false;
                case OWNER_ONLY -> {
                    UUID ownerId = (UUID) getOwnerMethod.invoke(plot);
                    yield player.getUniqueId().equals(ownerId);
                }
                case MEMBER_ONLY, RESPECT_FLAGS -> (boolean) isAddedMethod.invoke(plot, player.getUniqueId());
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("PlotSquared evaluation failed: " + exception.getMessage());
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
