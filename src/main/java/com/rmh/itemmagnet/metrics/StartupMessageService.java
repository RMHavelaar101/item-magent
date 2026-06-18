package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.util.PluginCompat;
import com.rmh.itemmagnet.util.PluginUrls;
import com.rmh.itemmagnet.util.TextUtil;

import java.util.List;
import java.util.Map;

public final class StartupMessageService {

    private static final String CONFIG_PATH = "startup-message.enabled";

    private final ItemMagnetPlugin plugin;

    public StartupMessageService(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean(CONFIG_PATH, true);
    }

    public void setEnabled(boolean enabled) {
        plugin.getConfig().set(CONFIG_PATH, enabled);
        plugin.saveConfig();
    }

    public void logIfEnabled() {
        if (!isEnabled()) {
            return;
        }

        Map<String, String> placeholders = Map.of(
                "version", PluginCompat.getVersion(plugin),
                "website", PluginUrls.WEBSITE
        );

        List<String> lines = plugin.getConfigManager().getMessagesConfig().getOrderedLines("startup.line-");
        if (lines.isEmpty()) {
            lines = List.of(
                    "Thanks for using ItemMagnet v{version}!",
                    "Website: {website}",
                    "Enjoying it? Star the project on GitHub or visit our website.",
                    "Disable this message: /itemmagnet startup-message off"
            );
        }

        for (String line : lines) {
            plugin.getLogger().info(TextUtil.stripColor(TextUtil.applyPlaceholders(line, placeholders)));
        }
    }
}
