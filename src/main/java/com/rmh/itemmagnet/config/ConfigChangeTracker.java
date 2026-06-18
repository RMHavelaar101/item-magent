package com.rmh.itemmagnet.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ConfigChangeTracker {

    private static final List<String> RESTART_REQUIRED_PREFIXES = List.of(
            "metrics.bstats-enabled",
            "metrics.bstats-plugin-id"
    );

    private final Set<String> changedPaths = new LinkedHashSet<>();

    public void record(String path) {
        if (path != null && !path.isBlank()) {
            changedPaths.add(path);
        }
    }

    public Set<String> getChangedPaths() {
        return Set.copyOf(changedPaths);
    }

    public List<String> getRestartRequiredKeys() {
        return changedPaths.stream()
                .filter(this::requiresRestart)
                .sorted()
                .toList();
    }

    public void clear() {
        changedPaths.clear();
    }

    public boolean requiresRestart(String path) {
        String normalized = path.toLowerCase(Locale.ROOT);
        return RESTART_REQUIRED_PREFIXES.stream().anyMatch(normalized::equals);
    }

    public static List<String> restartRequiredPrefixes() {
        return RESTART_REQUIRED_PREFIXES;
    }
}
