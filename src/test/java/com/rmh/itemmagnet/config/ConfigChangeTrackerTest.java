package com.rmh.itemmagnet.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigChangeTrackerTest {

    @Test
    void restartRequiredOnlyForMetricsKeys() {
        ConfigChangeTracker tracker = new ConfigChangeTracker();
        tracker.record("settings.sounds.enabled");
        tracker.record("metrics.bstats-enabled");
        tracker.record("metrics.update-check");

        assertEquals(1, tracker.getRestartRequiredKeys().size());
        assertTrue(tracker.requiresRestart("metrics.bstats-enabled"));
        assertFalse(tracker.requiresRestart("metrics.update-check"));
        assertFalse(tracker.requiresRestart("settings.sounds.enabled"));
    }
}
