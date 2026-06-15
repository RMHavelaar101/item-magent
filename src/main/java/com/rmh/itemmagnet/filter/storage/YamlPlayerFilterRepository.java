package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class YamlPlayerFilterRepository implements PlayerFilterRepository {

    private final ItemMagnetPlugin plugin;
    private final File file;

    public YamlPlayerFilterRepository(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "player-filters.yml");
    }

    public File getFile() {
        return file;
    }

    @Override
    public PlayerFilterBackend getBackend() {
        return PlayerFilterBackend.YAML;
    }

    @Override
    public void initialize() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    @Override
    public void shutdown() {
        // No persistent connections.
    }

    @Override
    public List<PlayerFilterRecord> loadAll() {
        List<PlayerFilterRecord> records = new ArrayList<>();
        if (!file.exists()) {
            return records;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<UUID> hintShown = new HashSet<>();
        ConfigurationSection meta = config.getConfigurationSection("_meta.hints");
        if (meta != null) {
            for (String uuidString : meta.getKeys(false)) {
                try {
                    if (meta.getBoolean(uuidString, false)) {
                        hintShown.add(UUID.fromString(uuidString));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        Set<UUID> playerIds = new HashSet<>();
        for (String key : config.getKeys(false)) {
            if ("_meta".equals(key)) {
                continue;
            }
            try {
                playerIds.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
            }
        }
        playerIds.addAll(hintShown);

        for (UUID playerId : playerIds) {
            records.add(parsePlayer(config, playerId, hintShown.contains(playerId)));
        }
        return records;
    }

    @Override
    public void saveRecord(PlayerFilterRecord record) {
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();

        if (record.isHintShown()) {
            config.set("_meta.hints." + record.getUuid(), true);
        } else {
            config.set("_meta.hints." + record.getUuid(), null);
        }

        String base = record.getUuid().toString();
        List<String> materials = record.getMaterials().stream().map(Material::name).toList();
        List<String> tags = new ArrayList<>(record.getTags());
        if (materials.isEmpty() && tags.isEmpty()) {
            config.set(base, null);
        } else {
            config.set(base + ".materials", materials);
            config.set(base + ".tags", tags);
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save player-filters.yml: " + exception.getMessage());
        }
    }

    @Override
    public void deleteRules(UUID uuid) {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(uuid.toString(), null);
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save player-filters.yml: " + exception.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        if (!file.exists()) {
            return true;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            if (!"_meta".equals(key)) {
                return false;
            }
        }
        ConfigurationSection hints = config.getConfigurationSection("_meta.hints");
        return hints == null || hints.getKeys(false).isEmpty();
    }

    private PlayerFilterRecord parsePlayer(FileConfiguration config, UUID playerId, boolean hintShown) {
        Set<Material> materials = new HashSet<>();
        Set<String> tags = new HashSet<>();
        String key = playerId.toString();

        if (config.isList(key)) {
            for (String materialName : config.getStringList(key)) {
                Material material = Material.matchMaterial(materialName);
                if (material != null) {
                    materials.add(material);
                }
            }
        } else {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null) {
                for (String materialName : section.getStringList("materials")) {
                    Material material = Material.matchMaterial(materialName);
                    if (material != null) {
                        materials.add(material);
                    }
                }
                tags.addAll(section.getStringList("tags"));
            }
        }
        return new PlayerFilterRecord(playerId, materials, tags, hintShown);
    }
}
