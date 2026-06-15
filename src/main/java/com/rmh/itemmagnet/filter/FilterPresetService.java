package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class FilterPresetService {

    private final Map<String, MaterialFilterRule> presets = new LinkedHashMap<>();

    public FilterPresetService(ItemMagnetPlugin plugin) {
        loadBuiltInPresets(plugin.getLogger());
        loadDataFolderPresets(plugin);
    }

    public Map<String, MaterialFilterRule> getPresets() {
        return Collections.unmodifiableMap(presets);
    }

    public MaterialFilterRule getPreset(String name) {
        if (name == null) {
            return null;
        }
        return presets.get(name.toLowerCase(Locale.ROOT));
    }

    public List<String> getPresetNames() {
        return List.copyOf(presets.keySet());
    }

    private void loadBuiltInPresets(Logger logger) {
        loadPresetResource("mining", logger);
        loadPresetResource("farming", logger);
        loadPresetResource("mob-drops", logger);
        loadPresetResource("keep-valuables", logger);
    }

    private void loadDataFolderPresets(ItemMagnetPlugin plugin) {
        java.io.File folder = new java.io.File(plugin.getDataFolder(), "filter-presets");
        if (!folder.exists()) {
            return;
        }
        java.io.File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (java.io.File file : files) {
            String id = file.getName().substring(0, file.getName().length() - 4).toLowerCase(Locale.ROOT);
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            presets.put(id, parsePreset(yaml, plugin.getLogger(), id));
        }
    }

    private void loadPresetResource(String id, Logger logger) {
        String path = "filter-presets/" + id + ".yml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return;
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
            presets.put(id, parsePreset(yaml, logger, id));
        } catch (Exception exception) {
            logger.warning("Failed to load filter preset " + id + ": " + exception.getMessage());
        }
    }

    private MaterialFilterRule parsePreset(YamlConfiguration yaml, Logger logger, String id) {
        List<String> materials = yaml.getStringList("materials");
        List<String> tags = yaml.getStringList("tags");
        return MaterialFilterResolver.resolve(materials, tags, logger);
    }

    public MaterialFilterRule mergePresets(List<String> names, Logger logger) {
        MaterialFilterRule merged = MaterialFilterRule.empty();
        for (String name : names) {
            MaterialFilterRule preset = getPreset(name);
            if (preset == null) {
                logger.warning("Unknown filter preset: " + name);
                continue;
            }
            merged = merged.merged(preset);
        }
        return merged;
    }

    public PresetMergePreview previewMerge(
            PlayerFilterStorage storage,
            java.util.UUID playerId,
            MaterialFilterRule preset
    ) {
        int newMaterials = 0;
        int newTags = 0;
        int skipped = 0;
        for (Material material : preset.getMaterials()) {
            if (storage.isServerBlocked(material)) {
                skipped++;
                continue;
            }
            if (!storage.getBlacklistedMaterials(playerId).contains(material)) {
                newMaterials++;
            }
        }
        java.util.Set<String> existingTags = storage.getBlacklistedTags(playerId);
        for (String tag : preset.getTags()) {
            String normalized = normalizeTag(tag);
            if (!existingTags.contains(normalized)) {
                newTags++;
            }
        }
        return new PresetMergePreview(newMaterials, newTags, skipped);
    }

    private String normalizeTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return tag;
        }
        return tag.contains(":")
                ? tag.toLowerCase(java.util.Locale.ROOT)
                : "minecraft:" + tag.toLowerCase(java.util.Locale.ROOT);
    }
}
