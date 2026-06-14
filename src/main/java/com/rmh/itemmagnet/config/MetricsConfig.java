package com.rmh.itemmagnet.config;

public final class MetricsConfig {

    private final boolean bstatsEnabled;
    private final int bstatsPluginId;
    private final UpdateCheckMode updateCheck;

    public MetricsConfig(boolean bstatsEnabled, int bstatsPluginId, UpdateCheckMode updateCheck) {
        this.bstatsEnabled = bstatsEnabled;
        this.bstatsPluginId = bstatsPluginId;
        this.updateCheck = updateCheck;
    }

    public boolean isBstatsEnabled() {
        return bstatsEnabled;
    }

    public int getBstatsPluginId() {
        return bstatsPluginId;
    }

    public UpdateCheckMode getUpdateCheck() {
        return updateCheck;
    }
}
