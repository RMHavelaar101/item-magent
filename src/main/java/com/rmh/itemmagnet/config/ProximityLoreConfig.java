package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProximityLoreConfig {

    private static final ProximityLoreConfig DISABLED = new ProximityLoreConfig(
            false,
            40,
            true,
            90,
            List.of(),
            Map.of()
    );

    private final boolean enabled;
    private final int scanIntervalTicks;
    private final boolean requireActiveMagnet;
    private final int cooldownSeconds;
    private final List<ProximityLoreZone> zones;
    private final Map<String, ProximityLoreZone> zonesById;

    public ProximityLoreConfig(
            boolean enabled,
            int scanIntervalTicks,
            boolean requireActiveMagnet,
            int cooldownSeconds,
            List<ProximityLoreZone> zones,
            Map<String, ProximityLoreZone> zonesById
    ) {
        this.enabled = enabled;
        this.scanIntervalTicks = scanIntervalTicks;
        this.requireActiveMagnet = requireActiveMagnet;
        this.cooldownSeconds = cooldownSeconds;
        this.zones = List.copyOf(zones);
        this.zonesById = Collections.unmodifiableMap(new LinkedHashMap<>(zonesById));
    }

    public static ProximityLoreConfig disabled() {
        return DISABLED;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getScanIntervalTicks() {
        return scanIntervalTicks;
    }

    public boolean isRequireActiveMagnet() {
        return requireActiveMagnet;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public List<ProximityLoreZone> getZones() {
        return zones;
    }

    public ProximityLoreZone getZone(String id) {
        return zonesById.get(id);
    }
}
