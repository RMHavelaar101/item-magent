package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QuestsProgressOnBlockedConfig {

    private final boolean enabled;
    private final Map<String, BlockedProgressRule> rules;

    public QuestsProgressOnBlockedConfig(boolean enabled, Map<String, BlockedProgressRule> rules) {
        this.enabled = enabled;
        this.rules = Collections.unmodifiableMap(new LinkedHashMap<>(rules));
    }

    public static QuestsProgressOnBlockedConfig disabled() {
        return new QuestsProgressOnBlockedConfig(false, Map.of());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, BlockedProgressRule> getRules() {
        return rules;
    }
}
