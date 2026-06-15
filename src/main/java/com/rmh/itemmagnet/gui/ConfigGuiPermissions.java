package com.rmh.itemmagnet.gui;

import org.bukkit.entity.Player;

public final class ConfigGuiPermissions {

    public static final String BASE = "itemmagnet.config";
    public static final String SAVE = "itemmagnet.config.save";
    public static final String RESET = "itemmagnet.config.reset";

    private ConfigGuiPermissions() {
    }

    public static String fieldPermission(String configPath) {
        return "itemmagnet.config.field." + configPath.replace('.', '_');
    }

    public static String sectionPermission(String sectionId) {
        return "itemmagnet.config.section." + sectionId;
    }

    public static boolean canAccessSection(Player player, String sectionId) {
        if (!player.hasPermission(BASE)) {
            return false;
        }
        String sectionPerm = sectionPermission(sectionId);
        if (player.isPermissionSet(sectionPerm)) {
            return player.hasPermission(sectionPerm);
        }
        return true;
    }

    public static boolean canEdit(Player player, String configPath, String sectionId) {
        if (!player.hasPermission(BASE)) {
            return false;
        }
        String fieldPerm = fieldPermission(configPath);
        if (player.isPermissionSet(fieldPerm)) {
            return player.hasPermission(fieldPerm);
        }
        String sectionPerm = sectionPermission(sectionId);
        if (player.isPermissionSet(sectionPerm)) {
            return player.hasPermission(sectionPerm);
        }
        return true;
    }

    public static boolean canSave(Player player) {
        return player.hasPermission(BASE) && player.hasPermission(SAVE);
    }

    public static boolean canReset(Player player) {
        return player.hasPermission(BASE) && player.hasPermission(RESET);
    }
}
