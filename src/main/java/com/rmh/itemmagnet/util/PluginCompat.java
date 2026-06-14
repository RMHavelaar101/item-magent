package com.rmh.itemmagnet.util;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginCompat {

    private PluginCompat() {
    }

    public static String getVersion(JavaPlugin plugin) {
        return plugin.getPluginMeta().getVersion();
    }

    public static String normalizeVersion(String version) {
        if (version == null) {
            return "";
        }
        String trimmed = version.trim();
        if (trimmed.startsWith("v") || trimmed.startsWith("V")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }

    public static boolean isNewerVersion(String remoteVersion, String localVersion) {
        return !normalizeVersion(remoteVersion).equals(normalizeVersion(localVersion));
    }
}
