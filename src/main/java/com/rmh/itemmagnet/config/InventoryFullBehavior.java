package com.rmh.itemmagnet.config;

import java.util.Locale;

public enum InventoryFullBehavior {
    CONTINUE,
    PAUSE,
    NOTIFY_ONCE;

    public static InventoryFullBehavior parse(String value) {
        if (value == null || value.isBlank()) {
            return CONTINUE;
        }
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return CONTINUE;
        }
    }
}
