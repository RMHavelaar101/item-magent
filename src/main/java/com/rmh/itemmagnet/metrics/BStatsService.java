package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MagnetConfig;
import org.bukkit.Bukkit;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public final class BStatsService {

    private final ItemMagnetPlugin plugin;

    public BStatsService(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        if (!config.getMetrics().isBstatsEnabled()) {
            return;
        }
        int pluginId = config.getMetrics().getBstatsPluginId();
        if (pluginId <= 0) {
            plugin.getLogger().info("bStats plugin ID not configured. Register at https://bstats.org and set metrics.bstats-plugin-id.");
            return;
        }

        try {
            Metrics metrics = new Metrics(plugin, pluginId);
            metrics.addCustomChart(new SimplePie("lands_enabled", () ->
                    String.valueOf(config.getLands().isEnabled() && plugin.getProtectionService().getLandsHook().isAvailable())));
            metrics.addCustomChart(new SimplePie("worldguard_enabled", () ->
                    String.valueOf(config.getWorldGuard().isEnabled() && plugin.getProtectionService().getWorldGuardHook().isAvailable())));
            metrics.addCustomChart(new SimplePie("anti_afk_enabled", () ->
                    String.valueOf(config.getAntiAfk().isEnabled())));
            metrics.addCustomChart(new SimplePie("underground_modifier_enabled", () ->
                    String.valueOf(config.getHeight().getUnderground().isEnabled())));
            metrics.addCustomChart(new SimplePie("tier_count", () -> String.valueOf(config.getTiers().size())));
        } catch (IllegalStateException exception) {
            plugin.getLogger().fine("bStats skipped: " + exception.getMessage());
        }
    }
}
