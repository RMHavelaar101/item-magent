package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

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

    public ReloadResult resetToDefaults() {
        try (InputStream stream = plugin.getResource("config.yml")) {
            if (stream == null) {
                return ReloadResult.failure();
            }
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                return ReloadResult.failure();
            }
            File target = new File(plugin.getDataFolder(), "config.yml");
            try (InputStream in = stream; FileOutputStream out = new FileOutputStream(target)) {
                in.transferTo(out);
            }
            changeTracker.clear();
            return plugin.reloadPlugin();
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to reset config.yml to defaults", exception);
            return ReloadResult.failure();
        }
    }
}
