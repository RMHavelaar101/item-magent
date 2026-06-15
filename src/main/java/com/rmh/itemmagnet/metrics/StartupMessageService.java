package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.util.PluginCompat;
import com.rmh.itemmagnet.util.TextUtil;

import java.util.List;
import java.util.Map;

public final class StartupMessageService {

    private static final String CONFIG_PATH = "startup-message.enabled";
    private static final String WEBSITE = "https://itemmagnet.theryn.org";

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
                "website", WEBSITE
        );

        List<String> lines = plugin.getConfigManager().getMessagesConfig().getOrderedLines("startup.line-");
        if (lines.isEmpty()) {
            lines = List.of(
                    "Thanks for using ItemMagnet v{version}!",
                    "Website: {website}",
                    "Enjoying it? Please leave a review on Hangar or SpigotMC.",
                    "Disable this message: /itemmagnet startup-message off"
            );
        }

        for (String line : lines) {
            plugin.getLogger().info(TextUtil.stripColor(TextUtil.applyPlaceholders(line, placeholders)));
        }
    }
}
