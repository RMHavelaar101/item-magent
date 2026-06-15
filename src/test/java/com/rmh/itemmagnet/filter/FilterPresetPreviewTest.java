package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ConfigManager;
import com.rmh.itemmagnet.config.MagnetConfig;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FilterPresetPreviewTest {

    private FilterPresetService presetService;
    private PlayerFilterStorage storage;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        ItemMagnetPlugin plugin = Mockito.mock(ItemMagnetPlugin.class);
        ConfigManager configManager = Mockito.mock(ConfigManager.class);
        MagnetConfig magnetConfig = Mockito.mock(MagnetConfig.class);
        when(plugin.getConfigManager()).thenReturn(configManager);
        when(configManager.getMagnetConfig()).thenReturn(magnetConfig);
        when(magnetConfig.getServerItemFilter()).thenReturn(new MaterialFilterRule(
                List.of(Material.IRON_ORE),
                List.of(),
                Set.of(Material.IRON_ORE)
        ));

        presetService = new FilterPresetService(plugin);
        storage = Mockito.mock(PlayerFilterStorage.class);
        playerId = UUID.randomUUID();
        when(storage.getBlacklistedMaterials(playerId)).thenReturn(java.util.Set.of(Material.DIRT));
        when(storage.getBlacklistedTags(playerId)).thenReturn(java.util.Set.of("minecraft:arrows"));
        when(storage.isServerBlocked(Material.IRON_ORE)).thenReturn(true);
        when(storage.isServerBlocked(Material.GOLD_ORE)).thenReturn(false);
    }

    @Test
    void previewMergeCountsNewRulesAndSkippedServerBlocked() {
        MaterialFilterRule preset = new MaterialFilterRule(
                List.of(Material.DIRT, Material.GOLD_ORE, Material.IRON_ORE),
                List.of("minecraft:logs", "minecraft:arrows"),
                Set.of(Material.DIRT, Material.GOLD_ORE, Material.IRON_ORE)
        );

        PresetMergePreview preview = presetService.previewMerge(storage, playerId, preset);

        assertEquals(1, preview.newMaterials());
        assertEquals(1, preview.newTags());
        assertEquals(1, preview.skippedServerBlocked());
    }
}
