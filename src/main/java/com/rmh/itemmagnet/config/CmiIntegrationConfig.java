package com.rmh.itemmagnet.config;

public final class CmiIntegrationConfig {

    private final CmiProgressOnBlockedConfig progressOnBlocked;

    public CmiIntegrationConfig(CmiProgressOnBlockedConfig progressOnBlocked) {
        this.progressOnBlocked = progressOnBlocked;
    }

    public static CmiIntegrationConfig disabled() {
        return new CmiIntegrationConfig(CmiProgressOnBlockedConfig.disabled());
    }

    public CmiProgressOnBlockedConfig getProgressOnBlocked() {
        return progressOnBlocked;
    }
}
