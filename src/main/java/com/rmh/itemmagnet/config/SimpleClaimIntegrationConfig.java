package com.rmh.itemmagnet.config;

public final class SimpleClaimIntegrationConfig {

    private final boolean enabled;
    private final WildernessPolicy wilderness;
    private final String wildernessPermission;
    private final ClaimedLandPolicy claimedLand;

    public SimpleClaimIntegrationConfig(
            boolean enabled,
            WildernessPolicy wilderness,
            String wildernessPermission,
            ClaimedLandPolicy claimedLand
    ) {
        this.enabled = enabled;
        this.wilderness = wilderness;
        this.wildernessPermission = wildernessPermission;
        this.claimedLand = claimedLand;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public WildernessPolicy getWilderness() {
        return wilderness;
    }

    public String getWildernessPermission() {
        return wildernessPermission;
    }

    public ClaimedLandPolicy getClaimedLand() {
        return claimedLand;
    }
}
