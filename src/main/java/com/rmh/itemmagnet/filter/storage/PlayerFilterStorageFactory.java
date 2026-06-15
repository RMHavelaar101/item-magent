package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.PlayerFilterConfig;

public final class PlayerFilterStorageFactory {

    private PlayerFilterStorageFactory() {
    }

    public static PlayerFilterRepository create(ItemMagnetPlugin plugin, PlayerFilterConfig config) {
        return switch (config.getStorage()) {
            case YAML -> new YamlPlayerFilterRepository(plugin);
            case SQLITE -> SqlitePlayerFilterRepository.create(plugin, config);
            case MYSQL -> MysqlPlayerFilterRepository.create(plugin, config);
        };
    }
}
