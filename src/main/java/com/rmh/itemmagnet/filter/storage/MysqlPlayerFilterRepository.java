package com.rmh.itemmagnet.filter.storage;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.PlayerFilterConfig;
import com.rmh.itemmagnet.config.PlayerFilterMysqlConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class MysqlPlayerFilterRepository {

    private MysqlPlayerFilterRepository() {
    }

    public static PlayerFilterRepository create(ItemMagnetPlugin plugin, PlayerFilterConfig config) {
        PlayerFilterMysqlConfig mysql = config.getMysql();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + mysql.getHost() + ":" + mysql.getPort() + "/" + mysql.getDatabase()
                + "?useSSL=false&allowPublicKeyRetrieval=true");
        hikariConfig.setUsername(mysql.getUsername());
        hikariConfig.setPassword(mysql.getPassword());
        hikariConfig.setMaximumPoolSize(mysql.getPoolSize());
        hikariConfig.setPoolName("ItemMagnet-PlayerFilters");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        return new JdbcPlayerFilterRepository(
                plugin,
                PlayerFilterBackend.MYSQL,
                mysql.getTablePrefix(),
                () -> {
                    try {
                        return openConnection(dataSource);
                    } catch (SQLException exception) {
                        throw new IllegalStateException(exception);
                    }
                },
                dataSource::close
        );
    }

    private static Connection openConnection(HikariDataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }
}
