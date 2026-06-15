package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ConfigAuditLog;
import com.rmh.itemmagnet.config.ConfigManager;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.filter.storage.YamlPlayerFilterRepository;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class PlayerFilterStorageTest {

    @TempDir
    Path tempDir;

    private PlayerFilterStorage storage;

    @BeforeEach
    void setUp() {
        ItemMagnetPlugin plugin = Mockito.mock(ItemMagnetPlugin.class);
        ConfigAuditLog auditLog = Mockito.mock(ConfigAuditLog.class);
        File dataFolder = tempDir.toFile();
        when(plugin.getDataFolder()).thenReturn(dataFolder);

        Server server = Mockito.mock(Server.class);
        BukkitScheduler scheduler = Mockito.mock(BukkitScheduler.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(1);
            task.run();
            return null;
        }).when(scheduler).runTaskAsynchronously(any(), any(Runnable.class));

        ConfigManager configManager = Mockito.mock(ConfigManager.class);
        MagnetConfig magnetConfig = Mockito.mock(MagnetConfig.class);
        when(plugin.getConfigManager()).thenReturn(configManager);
        when(configManager.getMagnetConfig()).thenReturn(magnetConfig);
        when(magnetConfig.getServerItemFilter()).thenReturn(MaterialFilterRule.empty());

        storage = new PlayerFilterStorage(plugin, auditLog, new YamlPlayerFilterRepository(plugin));
        storage.load();
    }

    @Test
    void addMaterialAndRemoveMaterial() {
        UUID playerId = UUID.randomUUID();
        assertFalse(storage.isBlacklisted(playerId, Material.DIRT));
        assertTrue(storage.addMaterial(playerId, Material.DIRT, null));
        assertTrue(storage.isBlacklisted(playerId, Material.DIRT));
        assertTrue(storage.removeMaterial(playerId, Material.DIRT, null));
        assertFalse(storage.isBlacklisted(playerId, Material.DIRT));
    }

    @Test
    void loadsLegacyFlatListFormat() throws Exception {
        UUID playerId = UUID.randomUUID();
        Files.writeString(tempDir.resolve("player-filters.yml"), playerId + ":\n  - GOLD_INGOT\n");

        storage.load();
        assertTrue(storage.isBlacklisted(playerId, Material.GOLD_INGOT));
    }

    @Test
    void loadsTagsSchema() throws Exception {
        UUID playerId = UUID.randomUUID();
        Files.writeString(tempDir.resolve("player-filters.yml"),
                playerId + ":\n  materials:\n    - DIRT\n  tags:\n    - minecraft:arrows\n");

        storage.load();
        assertTrue(storage.isBlacklisted(playerId, Material.DIRT));
        assertTrue(storage.getBlacklistedTags(playerId).contains("minecraft:arrows"));
    }

    @Test
    void persistsAcrossReload() {
        UUID playerId = UUID.randomUUID();
        storage.addMaterial(playerId, Material.GOLD_INGOT, null);
        storage.load();
        assertTrue(storage.isBlacklisted(playerId, Material.GOLD_INGOT));
    }

    @Test
    void clearRemovesRulesButKeepsHints() throws Exception {
        UUID playerId = UUID.randomUUID();
        Files.writeString(tempDir.resolve("player-filters.yml"),
                "_meta:\n  hints:\n    " + playerId + ": true\n"
                        + playerId + ":\n  materials:\n    - DIRT\n  tags:\n    - minecraft:arrows\n");

        storage.load();
        assertTrue(storage.hasSeenHint(playerId));
        assertTrue(storage.isBlacklisted(playerId, Material.DIRT));

        int removed = storage.clear(playerId, null);
        assertEquals(2, removed);
        assertFalse(storage.isBlacklisted(playerId, Material.DIRT));
        assertTrue(storage.getBlacklistedTags(playerId).isEmpty());
        assertTrue(storage.hasSeenHint(playerId));

        storage.load();
        assertTrue(storage.hasSeenHint(playerId));
        assertFalse(storage.isBlacklisted(playerId, Material.DIRT));
    }
}
