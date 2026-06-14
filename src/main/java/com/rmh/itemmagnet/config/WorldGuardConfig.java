package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class WorldGuardConfig {

    private final boolean enabled;
    private final CheckLocation checkAt;
    private final boolean respectItemPickupFlag;
    private final RegionMode regionMode;
    private final List<String> regions;
    private final Map<String, WorldGuardWorldConfig> worlds;

    public WorldGuardConfig(
            boolean enabled,
            CheckLocation checkAt,
            boolean respectItemPickupFlag,
            RegionMode regionMode,
            List<String> regions,
            Map<String, WorldGuardWorldConfig> worlds
    ) {
        this.enabled = enabled;
        this.checkAt = checkAt;
        this.respectItemPickupFlag = respectItemPickupFlag;
        this.regionMode = regionMode;
        this.regions = Collections.unmodifiableList(regions);
        this.worlds = Collections.unmodifiableMap(worlds);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CheckLocation getCheckAt() {
        return checkAt;
    }

    public boolean isRespectItemPickupFlag() {
        return respectItemPickupFlag;
    }

    public RegionMode getRegionMode() {
        return regionMode;
    }

    public List<String> getRegions() {
        return regions;
    }

    public Map<String, WorldGuardWorldConfig> getWorlds() {
        return worlds;
    }
}
