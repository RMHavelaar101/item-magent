package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CmiProgressOnBlockedConfig {

    private final boolean enabled;
    private final Map<String, BlockedProgressRule> rules;

    public CmiProgressOnBlockedConfig(boolean enabled, Map<String, BlockedProgressRule> rules) {
        this.enabled = enabled;
        this.rules = Collections.unmodifiableMap(new LinkedHashMap<>(rules));
    }

    public static CmiProgressOnBlockedConfig disabled() {
        return new CmiProgressOnBlockedConfig(false, Map.of());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, BlockedProgressRule> getRules() {
        return rules;
    }
}
