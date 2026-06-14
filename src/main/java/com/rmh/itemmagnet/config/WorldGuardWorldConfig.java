package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class WorldGuardWorldConfig {

    private final RegionMode regionMode;
    private final List<String> regions;

    public WorldGuardWorldConfig(RegionMode regionMode, List<String> regions) {
        this.regionMode = regionMode;
        this.regions = Collections.unmodifiableList(regions);
    }

    public RegionMode getRegionMode() {
        return regionMode;
    }

    public List<String> getRegions() {
        return regions;
    }
}
