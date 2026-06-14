package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class LandsConfig {

    private final boolean enabled;
    private final CheckLocation checkAt;
    private final WildernessPolicy wilderness;
    private final String wildernessPermission;
    private final ClaimedLandPolicy claimedLand;
    private final boolean requirePlayerInAllowedLand;

    public LandsConfig(
            boolean enabled,
            CheckLocation checkAt,
            WildernessPolicy wilderness,
            String wildernessPermission,
            ClaimedLandPolicy claimedLand,
            boolean requirePlayerInAllowedLand
    ) {
        this.enabled = enabled;
        this.checkAt = checkAt;
        this.wilderness = wilderness;
        this.wildernessPermission = wildernessPermission;
        this.claimedLand = claimedLand;
        this.requirePlayerInAllowedLand = requirePlayerInAllowedLand;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CheckLocation getCheckAt() {
        return checkAt;
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

    public boolean isRequirePlayerInAllowedLand() {
        return requirePlayerInAllowedLand;
    }
}
