package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;

public final class WorldFilterConfig {

    private final RegionMode mode;
    private final List<String> worlds;

    public WorldFilterConfig(RegionMode mode, List<String> worlds) {
        this.mode = mode;
        this.worlds = Collections.unmodifiableList(worlds);
    }

    public RegionMode getMode() {
        return mode;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public boolean isAllowed(String worldName) {
        if (mode == RegionMode.NONE || worlds.isEmpty()) {
            return true;
        }
        boolean listed = worlds.stream().anyMatch(world -> world.equalsIgnoreCase(worldName));
        return mode == RegionMode.WHITELIST ? listed : !listed;
    }
}
