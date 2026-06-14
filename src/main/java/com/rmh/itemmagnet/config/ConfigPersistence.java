package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigPersistence {

    private final ItemMagnetPlugin plugin;
    private final ConfigChangeTracker changeTracker;

    public ConfigPersistence(ItemMagnetPlugin plugin, ConfigChangeTracker changeTracker) {
        this.plugin = plugin;
        this.changeTracker = changeTracker;
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public void set(String path, Object value) {
        plugin.getConfig().set(path, value);
        changeTracker.record(path);
    }

    public void save() {
        plugin.saveConfig();
    }

    public ReloadResult saveAndReload() {
        save();
        return plugin.reloadPlugin();
    }
}
