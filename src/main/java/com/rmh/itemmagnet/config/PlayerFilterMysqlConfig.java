package com.rmh.itemmagnet.config;

public final class PlayerFilterMysqlConfig {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int poolSize;
    private final String tablePrefix;

    public PlayerFilterMysqlConfig(
            String host,
            int port,
            String database,
            String username,
            String password,
            int poolSize,
            String tablePrefix
    ) {
        this.host = host == null || host.isBlank() ? "localhost" : host;
        this.port = port <= 0 ? 3306 : port;
        this.database = database == null || database.isBlank() ? "itemmagnet" : database;
        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;
        this.poolSize = poolSize <= 0 ? 5 : poolSize;
        this.tablePrefix = tablePrefix == null || tablePrefix.isBlank() ? "im_" : tablePrefix;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}
