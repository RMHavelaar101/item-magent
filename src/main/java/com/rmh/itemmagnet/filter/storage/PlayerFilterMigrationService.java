package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.PlayerFilterConfig;
import org.bukkit.Material;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class PlayerFilterMigrationService {

    private PlayerFilterMigrationService() {
    }

    public static void migrateIfNeeded(ItemMagnetPlugin plugin, PlayerFilterRepository repository) {
        PlayerFilterConfig config = plugin.getConfigManager().getMagnetConfig().getPlayerFilterConfig();
        if (config.getStorage() == PlayerFilterBackend.YAML) {
            return;
        }
        if (!repository.isEmpty()) {
            return;
        }

        File yamlFile = new File(plugin.getDataFolder(), "player-filters.yml");
        if (!yamlFile.exists()) {
            return;
        }

        YamlPlayerFilterRepository yamlRepository = new YamlPlayerFilterRepository(plugin);
        List<PlayerFilterRecord> records = yamlRepository.loadAll();
        if (records.isEmpty()) {
            return;
        }

        for (PlayerFilterRecord record : records) {
            repository.saveRecord(record);
        }

        File backupFile = new File(plugin.getDataFolder(), "player-filters.yml.bak");
        if (yamlFile.renameTo(backupFile)) {
            plugin.getLogger().info("Imported " + records.size() + " player filter records from YAML to "
                    + config.getStorage().name() + " (backup: player-filters.yml.bak).");
        } else {
            plugin.getLogger().info("Imported " + records.size() + " player filter records from YAML to "
                    + config.getStorage().name() + ".");
        }
    }

    public static PlayerFilterRecord buildRecord(
            UUID playerId,
            Set<Material> materials,
            Set<String> tags,
            boolean hintShown
    ) {
        return new PlayerFilterRecord(playerId, materials, tags, hintShown);
    }

    public static Set<UUID> collectPlayerIds(Set<String> materialKeys, Set<String> tagKeys, Set<UUID> hintShown) {
        Set<UUID> players = new HashSet<>(hintShown);
        for (String key : materialKeys) {
            players.add(UUID.fromString(key.substring(0, key.indexOf(':'))));
        }
        for (String key : tagKeys) {
            players.add(UUID.fromString(key.substring(0, key.indexOf(':'))));
        }
        return players;
    }
}
