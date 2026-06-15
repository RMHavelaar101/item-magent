package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.filter.PullBlockReason;
import com.rmh.itemmagnet.filter.storage.PlayerFilterBackend;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public final class BStatsService {

    private final ItemMagnetPlugin plugin;
    private final MetricsCollector metricsCollector;

    public BStatsService(ItemMagnetPlugin plugin, MetricsCollector metricsCollector) {
        this.plugin = plugin;
        this.metricsCollector = metricsCollector;
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

            metrics.addCustomChart(new SimplePie("player_filter_storage", () -> {
                PlayerFilterBackend backend = plugin.getPlayerFilterStorage().getStorageBackend();
                return backend.name();
            }));
            metrics.addCustomChart(new SimplePie("inventory_full_behavior", () ->
                    config.getInventoryFullBehavior().name()));
            metrics.addCustomChart(new SimplePie("default_filter_preset", () ->
                    config.getPlayerFilterConfig().getDefaultPreset()));
            metrics.addCustomChart(new SimplePie("hold_mode", () ->
                    config.getHoldMode().name()));
            metrics.addCustomChart(new SimplePie("pull_experience", () ->
                    String.valueOf(config.isPullExperience())));

            if (config.getMetrics().isBstatsBlockReasons()) {
                metrics.addCustomChart(new SimplePie("top_block_reason", metricsCollector::getTopBlockReasonName));
                for (PullBlockReason reason : majorBlockReasons()) {
                    metrics.addCustomChart(new SimplePie(
                            "block_reason_" + reason.name().toLowerCase(),
                            () -> metricsCollector.hasBlockReason(reason) ? "active" : "inactive"
                    ));
                }
            }
        } catch (IllegalStateException exception) {
            plugin.getLogger().fine("bStats skipped: " + exception.getMessage());
        }
    }

    private PullBlockReason[] majorBlockReasons() {
        return new PullBlockReason[] {
                PullBlockReason.PLAYER_BLACKLIST,
                PullBlockReason.SERVER_BLACKLIST,
                PullBlockReason.TIER_BLACKLIST,
                PullBlockReason.PROTECTION,
                PullBlockReason.INVENTORY_FULL,
                PullBlockReason.EVENT_CANCELLED
        };
    }
}
