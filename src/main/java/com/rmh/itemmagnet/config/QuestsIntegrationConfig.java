package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QuestsIntegrationConfig {

    private final boolean enabled;
    private final Map<String, String> unlockOnComplete;

    public QuestsIntegrationConfig(boolean enabled, Map<String, String> unlockOnComplete) {
        this.enabled = enabled;
        this.unlockOnComplete = Collections.unmodifiableMap(new LinkedHashMap<>(unlockOnComplete));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, String> getUnlockOnComplete() {
        return unlockOnComplete;
    }
}
