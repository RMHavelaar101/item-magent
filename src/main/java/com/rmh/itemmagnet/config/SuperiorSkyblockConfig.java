package com.rmh.itemmagnet.config;

public final class SuperiorSkyblockConfig {

    private final boolean enabled;
    private final WildernessPolicy wilderness;
    private final String wildernessPermission;
    private final ClaimedLandPolicy claimedLand;
    private final String islandPermission;

    public SuperiorSkyblockConfig(
            boolean enabled,
            WildernessPolicy wilderness,
            String wildernessPermission,
            ClaimedLandPolicy claimedLand,
            String islandPermission
    ) {
        this.enabled = enabled;
        this.wilderness = wilderness;
        this.wildernessPermission = wildernessPermission;
        this.claimedLand = claimedLand;
        this.islandPermission = islandPermission;
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

    public String getIslandPermission() {
        return islandPermission;
    }
}
