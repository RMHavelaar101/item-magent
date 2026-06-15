package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ConfigAuditLog;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.filter.storage.PlayerFilterBackend;
import com.rmh.itemmagnet.filter.storage.PlayerFilterMigrationService;
import com.rmh.itemmagnet.filter.storage.PlayerFilterRecord;
import com.rmh.itemmagnet.filter.storage.PlayerFilterRepository;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class PlayerFilterStorage {

    private final ItemMagnetPlugin plugin;
    private final ConfigAuditLog auditLog;
    private final PlayerFilterRepository repository;
    private final Set<UUID> hintShown = new HashSet<>();
    private final Set<String> materialKeys = new HashSet<>();
    private final Set<String> tagKeys = new HashSet<>();

    public PlayerFilterStorage(ItemMagnetPlugin plugin, ConfigAuditLog auditLog, PlayerFilterRepository repository) {
        this.plugin = plugin;
        this.auditLog = auditLog;
        this.repository = repository;
    }

    public PlayerFilterBackend getStorageBackend() {
        return repository.getBackend();
    }

    public void load() {
        repository.initialize();
        hintShown.clear();
        materialKeys.clear();
        tagKeys.clear();

        for (PlayerFilterRecord record : repository.loadAll()) {
            if (record.isHintShown()) {
                hintShown.add(record.getUuid());
            }
            for (Material material : record.getMaterials()) {
                materialKeys.add(materialKey(record.getUuid(), material));
            }
            for (String tag : record.getTags()) {
                tagKeys.add(tagKey(record.getUuid(), tag));
            }
        }
    }

    public void shutdown() {
        saveSync();
        repository.shutdown();
    }

    public boolean isBlacklisted(UUID playerId, Material material) {
        if (materialKeys.contains(materialKey(playerId, material))) {
            return true;
        }
        for (String tagKey : tagKeys) {
            if (!tagKey.startsWith(playerId + ":t:")) {
                continue;
            }
            String tagName = tagKey.substring(tagKey.indexOf(":t:") + 3);
            if (MaterialFilterResolver.isBlockedByTags(List.of(tagName), material)) {
                return true;
            }
        }
        return false;
    }

    public boolean isServerBlocked(Material material) {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        return config.getServerItemFilter().blocks(material);
    }

    public Set<Material> getBlacklistedMaterials(UUID playerId) {
        String prefix = playerId + ":m:";
        Set<Material> materials = new HashSet<>();
        for (String key : materialKeys) {
            if (!key.startsWith(prefix)) {
                continue;
            }
            Material material = Material.matchMaterial(key.substring(prefix.length()));
            if (material != null) {
                materials.add(material);
            }
        }
        return Collections.unmodifiableSet(materials);
    }

    public Set<String> getBlacklistedTags(UUID playerId) {
        String prefix = playerId + ":t:";
        Set<String> tags = new HashSet<>();
        for (String key : tagKeys) {
            if (key.startsWith(prefix)) {
                tags.add(key.substring(prefix.length()));
            }
        }
        return Collections.unmodifiableSet(tags);
    }

    public int getFilterRuleCount(UUID playerId) {
        return getBlacklistedMaterials(playerId).size() + getBlacklistedTags(playerId).size();
    }

    public boolean addMaterial(UUID playerId, Material material, Player actor) {
        if (isServerBlocked(material)) {
            return false;
        }
        if (!materialKeys.add(materialKey(playerId, material))) {
            return false;
        }
        saveAsync(playerId);
        if (actor != null) {
            auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-add-material", material.name());
        }
        return true;
    }

    public boolean removeMaterial(UUID playerId, Material material, Player actor) {
        if (!materialKeys.remove(materialKey(playerId, material))) {
            return false;
        }
        saveAsync(playerId);
        if (actor != null) {
            auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-remove-material", material.name());
        }
        return true;
    }

    public boolean removeTag(UUID playerId, String tag, Player actor) {
        if (!tagKeys.remove(tagKey(playerId, tag))) {
            return false;
        }
        saveAsync(playerId);
        if (actor != null) {
            auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-remove-tag", tag);
        }
        return true;
    }

    public void applyPreset(UUID playerId, MaterialFilterRule preset, Player actor) {
        for (Material material : preset.getMaterials()) {
            if (!isServerBlocked(material)) {
                materialKeys.add(materialKey(playerId, material));
            }
        }
        for (String tag : preset.getTags()) {
            tagKeys.add(tagKey(playerId, tag));
        }
        saveAsync(playerId);
        if (actor != null) {
            auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-apply-preset", preset.getRuleCount() + " rules");
        }
    }

    public void mergeMaterials(UUID playerId, List<Material> materials, Player actor) {
        int added = 0;
        for (Material material : materials) {
            if (isServerBlocked(material)) {
                continue;
            }
            if (materialKeys.add(materialKey(playerId, material))) {
                added++;
            }
        }
        if (added > 0) {
            saveAsync(playerId);
            if (actor != null) {
                auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-import", added + " materials");
            }
        }
    }

    public boolean hasSeenHint(UUID playerId) {
        return hintShown.contains(playerId);
    }

    public void markHintShown(UUID playerId) {
        if (!hintShown.add(playerId)) {
            return;
        }
        saveAsync(playerId);
    }

    public void applyDefaultPresetIfNeeded(UUID playerId) {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        String presetName = config.getPlayerFilterConfig().getDefaultPreset();
        if ("none".equalsIgnoreCase(presetName) || getFilterRuleCount(playerId) > 0) {
            return;
        }
        FilterPresetService presetService = plugin.getFilterPresetService();
        MaterialFilterRule preset = presetService.getPreset(presetName);
        if (preset != null) {
            applyPreset(playerId, preset, null);
        }
    }

    public int clear(UUID playerId, Player actor) {
        String materialPrefix = playerId + ":m:";
        String tagPrefix = playerId + ":t:";
        int removed = 0;
        removed += (int) materialKeys.stream().filter(key -> key.startsWith(materialPrefix)).count();
        materialKeys.removeIf(key -> key.startsWith(materialPrefix));
        removed += (int) tagKeys.stream().filter(key -> key.startsWith(tagPrefix)).count();
        tagKeys.removeIf(key -> key.startsWith(tagPrefix));
        if (removed > 0) {
            saveAsync(playerId);
            if (actor != null) {
                auditLog.logFilterChange(actor.getName(), actor.getUniqueId(), "filter-clear", removed + " rules");
            }
        }
        return removed;
    }

    private void saveAsync(UUID playerId) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> savePlayer(playerId));
    }

    private synchronized void savePlayer(UUID playerId) {
        repository.saveRecord(PlayerFilterMigrationService.buildRecord(
                playerId,
                getBlacklistedMaterials(playerId),
                getBlacklistedTags(playerId),
                hintShown.contains(playerId)
        ));
    }

    private synchronized void saveSync() {
        for (UUID playerId : PlayerFilterMigrationService.collectPlayerIds(materialKeys, tagKeys, hintShown)) {
            savePlayer(playerId);
        }
    }

    private String materialKey(UUID playerId, Material material) {
        return playerId + ":m:" + material.name().toUpperCase(Locale.ROOT);
    }

    private String tagKey(UUID playerId, String tag) {
        if (tag == null || tag.isBlank()) {
            return playerId + ":t:";
        }
        String normalized = tag.contains(":")
                ? tag.toLowerCase(Locale.ROOT)
                : "minecraft:" + tag.toLowerCase(Locale.ROOT);
        return playerId + ":t:" + normalized;
    }
}
