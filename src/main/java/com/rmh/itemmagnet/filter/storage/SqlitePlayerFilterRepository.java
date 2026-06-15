package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.PlayerFilterConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SqlitePlayerFilterRepository {

    private SqlitePlayerFilterRepository() {
    }

    public static PlayerFilterRepository create(ItemMagnetPlugin plugin, PlayerFilterConfig config) {
        File databaseFile = new File(plugin.getDataFolder(), config.getSqliteFile());
        String jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        return new JdbcPlayerFilterRepository(
                plugin,
                PlayerFilterBackend.SQLITE,
                "im_",
                () -> {
                    try {
                        return openConnection(jdbcUrl);
                    } catch (SQLException exception) {
                        throw new IllegalStateException(exception);
                    }
                },
                () -> {}
        );
    }

    private static Connection openConnection(String jdbcUrl) throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }
}
