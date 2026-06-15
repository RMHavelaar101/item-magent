package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class JdbcPlayerFilterRepository implements PlayerFilterRepository {

    private final ItemMagnetPlugin plugin;
    private final PlayerFilterBackend backend;
    private final String tablePrefix;
    private final Supplier<Connection> connectionSupplier;
    private final Runnable shutdownHook;

    private String materialsTable;
    private String tagsTable;
    private String metaTable;

    public JdbcPlayerFilterRepository(
            ItemMagnetPlugin plugin,
            PlayerFilterBackend backend,
            String tablePrefix,
            Supplier<Connection> connectionSupplier,
            Runnable shutdownHook
    ) {
        this.plugin = plugin;
        this.backend = backend;
        this.tablePrefix = tablePrefix == null || tablePrefix.isBlank() ? "im_" : tablePrefix;
        this.connectionSupplier = connectionSupplier;
        this.shutdownHook = shutdownHook == null ? () -> {} : shutdownHook;
    }

    @Override
    public PlayerFilterBackend getBackend() {
        return backend;
    }

    @Override
    public void initialize() {
        materialsTable = tablePrefix + "filter_materials";
        tagsTable = tablePrefix + "filter_tags";
        metaTable = tablePrefix + "filter_meta";
        try (Connection connection = connectionSupplier.get(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS %s (
                        uuid VARCHAR(36) NOT NULL,
                        material VARCHAR(64) NOT NULL,
                        PRIMARY KEY (uuid, material)
                    )
                    """.formatted(materialsTable));
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS %s (
                        uuid VARCHAR(36) NOT NULL,
                        tag VARCHAR(128) NOT NULL,
                        PRIMARY KEY (uuid, tag)
                    )
                    """.formatted(tagsTable));
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS %s (
                        uuid VARCHAR(36) NOT NULL PRIMARY KEY,
                        hint_shown BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """.formatted(metaTable));
        } catch (SQLException exception) {
            plugin.getLogger().severe("Failed to initialize player filter database: " + exception.getMessage());
        }
    }

    @Override
    public void shutdown() {
        shutdownHook.run();
    }

    @Override
    public List<PlayerFilterRecord> loadAll() {
        Map<UUID, Set<Material>> materialsByPlayer = new HashMap<>();
        Map<UUID, Set<String>> tagsByPlayer = new HashMap<>();
        Map<UUID, Boolean> hintsByPlayer = new HashMap<>();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid, material FROM " + materialsTable)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        Material material = Material.matchMaterial(resultSet.getString("material"));
                        if (material != null) {
                            materialsByPlayer.computeIfAbsent(uuid, ignored -> new HashSet<>()).add(material);
                        }
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid, tag FROM " + tagsTable)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        tagsByPlayer.computeIfAbsent(uuid, ignored -> new HashSet<>()).add(resultSet.getString("tag"));
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid, hint_shown FROM " + metaTable)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        hintsByPlayer.put(uuid, resultSet.getBoolean("hint_shown"));
                    }
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().warning("Failed to load player filters from database: " + exception.getMessage());
            return List.of();
        }

        Set<UUID> playerIds = new HashSet<>();
        playerIds.addAll(materialsByPlayer.keySet());
        playerIds.addAll(tagsByPlayer.keySet());
        playerIds.addAll(hintsByPlayer.keySet());

        List<PlayerFilterRecord> records = new ArrayList<>();
        for (UUID playerId : playerIds) {
            records.add(new PlayerFilterRecord(
                    playerId,
                    materialsByPlayer.getOrDefault(playerId, Set.of()),
                    tagsByPlayer.getOrDefault(playerId, Set.of()),
                    hintsByPlayer.getOrDefault(playerId, false)
            ));
        }
        return records;
    }

    @Override
    public void saveRecord(PlayerFilterRecord record) {
        String uuid = record.getUuid().toString();
        try (Connection connection = connectionSupplier.get()) {
            connection.setAutoCommit(false);
            try (PreparedStatement deleteMaterials = connection.prepareStatement(
                    "DELETE FROM " + materialsTable + " WHERE uuid = ?")) {
                deleteMaterials.setString(1, uuid);
                deleteMaterials.executeUpdate();
            }
            try (PreparedStatement deleteTags = connection.prepareStatement(
                    "DELETE FROM " + tagsTable + " WHERE uuid = ?")) {
                deleteTags.setString(1, uuid);
                deleteTags.executeUpdate();
            }

            try (PreparedStatement insertMaterial = connection.prepareStatement(
                    "INSERT INTO " + materialsTable + " (uuid, material) VALUES (?, ?)")) {
                for (Material material : record.getMaterials()) {
                    insertMaterial.setString(1, uuid);
                    insertMaterial.setString(2, material.name().toUpperCase(Locale.ROOT));
                    insertMaterial.addBatch();
                }
                insertMaterial.executeBatch();
            }

            try (PreparedStatement insertTag = connection.prepareStatement(
                    "INSERT INTO " + tagsTable + " (uuid, tag) VALUES (?, ?)")) {
                for (String tag : record.getTags()) {
                    insertTag.setString(1, uuid);
                    insertTag.setString(2, tag);
                    insertTag.addBatch();
                }
                insertTag.executeBatch();
            }

            String upsertMetaSql = backend == PlayerFilterBackend.MYSQL
                    ? "INSERT INTO " + metaTable + " (uuid, hint_shown) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE hint_shown = VALUES(hint_shown)"
                    : "INSERT INTO " + metaTable + " (uuid, hint_shown) VALUES (?, ?) "
                    + "ON CONFLICT(uuid) DO UPDATE SET hint_shown = excluded.hint_shown";
            try (PreparedStatement upsertMeta = connection.prepareStatement(upsertMetaSql)) {
                upsertMeta.setString(1, uuid);
                upsertMeta.setBoolean(2, record.isHintShown());
                upsertMeta.executeUpdate();
            }

            connection.commit();
        } catch (SQLException exception) {
            plugin.getLogger().warning("Failed to save player filter record: " + exception.getMessage());
        }
    }

    @Override
    public void deleteRules(UUID uuid) {
        String uuidString = uuid.toString();
        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement deleteMaterials = connection.prepareStatement(
                    "DELETE FROM " + materialsTable + " WHERE uuid = ?")) {
                deleteMaterials.setString(1, uuidString);
                deleteMaterials.executeUpdate();
            }
            try (PreparedStatement deleteTags = connection.prepareStatement(
                    "DELETE FROM " + tagsTable + " WHERE uuid = ?")) {
                deleteTags.setString(1, uuidString);
                deleteTags.executeUpdate();
            }
        } catch (SQLException exception) {
            plugin.getLogger().warning("Failed to delete player filter rules: " + exception.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        try (Connection connection = connectionSupplier.get(); Statement statement = connection.createStatement()) {
            long count = 0;
            count += countRows(statement, "SELECT COUNT(*) FROM " + materialsTable);
            count += countRows(statement, "SELECT COUNT(*) FROM " + tagsTable);
            count += countRows(statement, "SELECT COUNT(*) FROM " + metaTable);
            return count == 0;
        } catch (SQLException exception) {
            plugin.getLogger().warning("Failed to check player filter database: " + exception.getMessage());
            return true;
        }
    }

    private long countRows(Statement statement, String sql) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getLong(1) : 0;
        }
    }
}
