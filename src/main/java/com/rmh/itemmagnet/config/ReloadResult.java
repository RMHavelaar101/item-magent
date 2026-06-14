package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;

public record ReloadResult(
        boolean success,
        List<String> appliedSections,
        List<String> restartRequiredKeys
) {
    public ReloadResult {
        appliedSections = List.copyOf(appliedSections);
        restartRequiredKeys = List.copyOf(restartRequiredKeys);
    }

    public static ReloadResult success(List<String> appliedSections, List<String> restartRequiredKeys) {
        return new ReloadResult(true, appliedSections, restartRequiredKeys);
    }

    public static ReloadResult failure() {
        return new ReloadResult(false, Collections.emptyList(), Collections.emptyList());
    }
}
