package com.rmh.itemmagnet.config;

public final class GriefPreventionConfig {

    private final boolean enabled;
    private final ClaimedLandPolicy claimedLand;

    public GriefPreventionConfig(boolean enabled, ClaimedLandPolicy claimedLand) {
        this.enabled = enabled;
        this.claimedLand = claimedLand;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ClaimedLandPolicy getClaimedLand() {
        return claimedLand;
    }
}
