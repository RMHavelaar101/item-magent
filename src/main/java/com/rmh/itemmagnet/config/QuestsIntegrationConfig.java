package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QuestsIntegrationConfig {

    private final boolean enabled;
    private final Map<String, String> unlockOnComplete;
    private final QuestsProgressOnBlockedConfig progressOnBlocked;

    public QuestsIntegrationConfig(
            boolean enabled,
            Map<String, String> unlockOnComplete,
            QuestsProgressOnBlockedConfig progressOnBlocked
    ) {
        this.enabled = enabled;
        this.unlockOnComplete = Collections.unmodifiableMap(new LinkedHashMap<>(unlockOnComplete));
        this.progressOnBlocked = progressOnBlocked == null
                ? QuestsProgressOnBlockedConfig.disabled()
                : progressOnBlocked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, String> getUnlockOnComplete() {
        return unlockOnComplete;
    }

    public QuestsProgressOnBlockedConfig getProgressOnBlocked() {
        return progressOnBlocked;
    }
}
