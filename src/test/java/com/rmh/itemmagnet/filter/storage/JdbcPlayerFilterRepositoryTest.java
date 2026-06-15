package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcPlayerFilterRepositoryTest {

    @TempDir
    Path tempDir;

    private PlayerFilterRepository repository;
    private String jdbcUrl;

    @BeforeEach
    void setUp() {
        File databaseFile = tempDir.resolve("player-filters.db").toFile();
        jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        ItemMagnetPlugin plugin = Mockito.mock(ItemMagnetPlugin.class);
        Mockito.when(plugin.getLogger()).thenReturn(Logger.getLogger("ItemMagnetTest"));
        repository = new JdbcPlayerFilterRepository(
                plugin,
                PlayerFilterBackend.SQLITE,
                "im_",
                () -> {
                    try {
                        return DriverManager.getConnection(jdbcUrl);
                    } catch (SQLException exception) {
                        throw new IllegalStateException(exception);
                    }
                },
                () -> {}
        );
        repository.initialize();
    }

    @Test
    void savesAndLoadsRecords() {
        UUID playerId = UUID.randomUUID();
        repository.saveRecord(new PlayerFilterRecord(
                playerId,
                Set.of(Material.DIRT, Material.STONE),
                Set.of("minecraft:logs"),
                true
        ));

        List<PlayerFilterRecord> records = repository.loadAll();
        assertEquals(1, records.size());
        PlayerFilterRecord record = records.get(0);
        assertEquals(playerId, record.getUuid());
        assertTrue(record.getMaterials().containsAll(Set.of(Material.DIRT, Material.STONE)));
        assertTrue(record.getTags().contains("minecraft:logs"));
        assertTrue(record.isHintShown());
    }

    @Test
    void deleteRulesRemovesMaterialsAndTags() {
        UUID playerId = UUID.randomUUID();
        repository.saveRecord(new PlayerFilterRecord(
                playerId,
                Set.of(Material.DIRT),
                Set.of("minecraft:logs"),
                true
        ));

        repository.deleteRules(playerId);
        List<PlayerFilterRecord> records = repository.loadAll();
        assertEquals(1, records.size());
        assertTrue(records.get(0).getMaterials().isEmpty());
        assertTrue(records.get(0).getTags().isEmpty());
        assertTrue(records.get(0).isHintShown());
    }

    @Test
    void migrationFromYamlPopulatesSqlite() throws Exception {
        ItemMagnetPlugin plugin = Mockito.mock(ItemMagnetPlugin.class);
        Mockito.when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        Mockito.when(plugin.getLogger()).thenReturn(Logger.getLogger("ItemMagnetTest"));

        UUID playerId = UUID.randomUUID();
        java.nio.file.Files.writeString(
                tempDir.resolve("player-filters.yml"),
                playerId + ":\n  materials:\n    - EMERALD\n  tags:\n    - minecraft:gems\n"
        );

        YamlPlayerFilterRepository yamlRepository = new YamlPlayerFilterRepository(plugin);
        for (PlayerFilterRecord record : yamlRepository.loadAll()) {
            repository.saveRecord(record);
        }

        assertFalse(repository.isEmpty());
        PlayerFilterRecord loaded = repository.loadAll().get(0);
        assertTrue(loaded.getMaterials().contains(Material.EMERALD));
        assertTrue(loaded.getTags().contains("minecraft:gems"));
    }
}
