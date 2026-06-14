package com.rmh.itemmagnet.gui;

import java.util.UUID;

public final class ConfigGuiChatSession {

    private final UUID playerId;
    private final String configPath;
    private final ConfigGuiType returnMenu;
    private final String returnContext;

    public ConfigGuiChatSession(UUID playerId, String configPath, ConfigGuiType returnMenu, String returnContext) {
        this.playerId = playerId;
        this.configPath = configPath;
        this.returnMenu = returnMenu;
        this.returnContext = returnContext;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getConfigPath() {
        return configPath;
    }

    public ConfigGuiType getReturnMenu() {
        return returnMenu;
    }

    public String getReturnContext() {
        return returnContext;
    }
}
