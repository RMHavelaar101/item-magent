package com.rmh.itemmagnet.config;

import java.util.Locale;

public enum VerticalPullMode {
    DIRECT,
    HORIZONTAL_FIRST;

    public static VerticalPullMode parse(String value) {
        if (value == null || value.isBlank()) {
            return HORIZONTAL_FIRST;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return HORIZONTAL_FIRST;
        }
    }
}
