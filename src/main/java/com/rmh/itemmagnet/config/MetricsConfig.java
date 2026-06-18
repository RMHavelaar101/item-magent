package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.util.PluginUrls;

public final class MetricsConfig {

    private final boolean bstatsEnabled;
    private final int bstatsPluginId;
    private final UpdateCheckMode updateCheck;
    private final boolean bstatsBlockReasons;
    private final String updateDownloadUrl;

    public MetricsConfig(
            boolean bstatsEnabled,
            int bstatsPluginId,
            UpdateCheckMode updateCheck,
            boolean bstatsBlockReasons,
            String updateDownloadUrl
    ) {
        this.bstatsEnabled = bstatsEnabled;
        this.bstatsPluginId = bstatsPluginId;
        this.updateCheck = updateCheck;
        this.bstatsBlockReasons = bstatsBlockReasons;
        this.updateDownloadUrl = updateDownloadUrl == null || updateDownloadUrl.isBlank()
                ? PluginUrls.GITHUB_RELEASES_LATEST
                : updateDownloadUrl.trim();
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

    public String getUpdateDownloadUrl() {
        return updateDownloadUrl;
    }
}
