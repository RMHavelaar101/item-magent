package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.ItemMagnetPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;

public final class ConfigAuditLog {

    private final ItemMagnetPlugin plugin;
    private final File file;

    public ConfigAuditLog(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config-audit.log");
    }

    public void logConfigChange(String playerName, UUID playerId, String path, Object oldValue, Object newValue) {
        append(String.format(
                "[%s] player=%s uuid=%s action=set path=%s old=%s new=%s",
                Instant.now(),
                playerName,
                playerId,
                path,
                stringify(oldValue),
                stringify(newValue)
        ));
    }

    public void logFilterChange(String playerName, UUID playerId, String action, String detail) {
        append(String.format(
                "[%s] player=%s uuid=%s action=%s detail=%s",
                Instant.now(),
                playerName,
                playerId,
                action,
                detail
        ));
    }

    public void logSystemChange(String action, String detail) {
        append(String.format("[%s] player=console uuid=system action=%s detail=%s", Instant.now(), action, detail));
    }

    private void append(String line) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                return;
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println(line);
            } catch (IOException exception) {
                plugin.getLogger().log(Level.WARNING, "Failed to write config audit log", exception);
            }
        });
    }

    private String stringify(Object value) {
        return value == null ? "null" : String.valueOf(value);
    }
}
