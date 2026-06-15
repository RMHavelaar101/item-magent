package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.api.event.ItemMagnetPullBlockedEvent;
import com.rmh.itemmagnet.filter.PullBlockReason;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class MetricsCollectorListener implements Listener {

    private final MetricsCollector metricsCollector;

    public MetricsCollectorListener(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPullBlocked(ItemMagnetPullBlockedEvent event) {
        metricsCollector.recordBlock(event.getReason());
    }
}
