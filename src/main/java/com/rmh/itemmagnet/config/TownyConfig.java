package com.rmh.itemmagnet.config;

public final class TownyConfig {

    private final boolean enabled;
    private final WildernessPolicy wilderness;
    private final String wildernessPermission;
    private final ClaimedLandPolicy claimedTown;

    public TownyConfig(
            boolean enabled,
            WildernessPolicy wilderness,
            String wildernessPermission,
            ClaimedLandPolicy claimedTown
    ) {
        this.enabled = enabled;
        this.wilderness = wilderness;
        this.wildernessPermission = wildernessPermission;
        this.claimedTown = claimedTown;
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

    public ClaimedLandPolicy getClaimedTown() {
        return claimedTown;
    }
}
