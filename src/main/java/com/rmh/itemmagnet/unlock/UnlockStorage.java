package com.rmh.itemmagnet.unlock;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class UnlockStorage {

    private final ItemMagnetPlugin plugin;
    private final File file;
    private final Set<String> unlockKeys = new HashSet<>();

    public UnlockStorage(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "unlocks.yml");
    }

    public void load() {
        unlockKeys.clear();
        if (!file.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String uuidString : config.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException exception) {
                continue;
            }
            List<String> tiers = config.getStringList(uuidString);
            for (String tier : tiers) {
                unlockKeys.add(key(uuid, tier.toLowerCase(Locale.ROOT)));
            }
        }
    }

    public boolean has(UUID playerId, String tierId) {
        return unlockKeys.contains(key(playerId, tierId.toLowerCase(Locale.ROOT)));
    }

    public void grant(UUID playerId, String tierId) {
        String normalizedTier = tierId.toLowerCase(Locale.ROOT);
        if (!unlockKeys.add(key(playerId, normalizedTier))) {
            return;
        }
        saveAsync();
    }

    public Set<String> getTiersFor(UUID playerId) {
        String prefix = playerId + ":";
        Set<String> tiers = new HashSet<>();
        for (String unlockKey : unlockKeys) {
            if (unlockKey.startsWith(prefix)) {
                tiers.add(unlockKey.substring(prefix.length()));
            }
        }
        return Collections.unmodifiableSet(tiers);
    }

    private void saveAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveSync);
    }

    private synchronized void saveSync() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder for unlock storage.");
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        for (String unlockKey : unlockKeys) {
            int separator = unlockKey.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String uuid = unlockKey.substring(0, separator);
            String tier = unlockKey.substring(separator + 1);
            List<String> tiers = config.getStringList(uuid);
            if (!tiers.contains(tier)) {
                tiers = new java.util.ArrayList<>(tiers);
                tiers.add(tier);
                config.set(uuid, tiers);
            }
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save unlocks.yml: " + exception.getMessage());
        }
    }

    private String key(UUID playerId, String tierId) {
        return playerId + ":" + tierId.toLowerCase(Locale.ROOT);
    }
}
