package com.rmh.itemmagnet.config;

public final class CommandsConfig {

    private final boolean filterByPermission;

    public CommandsConfig(boolean filterByPermission) {
        this.filterByPermission = filterByPermission;
    }

    public boolean isFilterByPermission() {
        return filterByPermission;
    }
}
