package com.rmh.itemmagnet.config;

public final class MetricsConfig {

    private final boolean bstatsEnabled;
    private final int bstatsPluginId;
    private final UpdateCheckMode updateCheck;
    private final boolean bstatsBlockReasons;

    public MetricsConfig(
            boolean bstatsEnabled,
            int bstatsPluginId,
            UpdateCheckMode updateCheck,
            boolean bstatsBlockReasons
    ) {
        this.bstatsEnabled = bstatsEnabled;
        this.bstatsPluginId = bstatsPluginId;
        this.updateCheck = updateCheck;
        this.bstatsBlockReasons = bstatsBlockReasons;
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

    public boolean isBstatsBlockReasons() {
        return bstatsBlockReasons;
    }
}
