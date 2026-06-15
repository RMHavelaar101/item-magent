package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.filter.storage.PlayerFilterBackend;

public final class PlayerFilterConfig {

    private final String defaultPreset;
    private final boolean showPresetHint;
    private final PlayerFilterBackend storage;
    private final String sqliteFile;
    private final PlayerFilterMysqlConfig mysql;

    public PlayerFilterConfig(
            String defaultPreset,
            boolean showPresetHint,
            PlayerFilterBackend storage,
            String sqliteFile,
            PlayerFilterMysqlConfig mysql
    ) {
        this.defaultPreset = defaultPreset == null || defaultPreset.isBlank() ? "none" : defaultPreset;
        this.showPresetHint = showPresetHint;
        this.storage = storage == null ? PlayerFilterBackend.YAML : storage;
        this.sqliteFile = sqliteFile == null || sqliteFile.isBlank() ? "player-filters.db" : sqliteFile;
        this.mysql = mysql == null ? new PlayerFilterMysqlConfig(null, 3306, null, null, null, 5, null) : mysql;
    }

    public String getDefaultPreset() {
        return defaultPreset;
    }

    public boolean isShowPresetHint() {
        return showPresetHint;
    }

    public PlayerFilterBackend getStorage() {
        return storage;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }

    public PlayerFilterMysqlConfig getMysql() {
        return mysql;
    }
}
