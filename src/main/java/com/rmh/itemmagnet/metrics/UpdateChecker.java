package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.UpdateCheckMode;
import com.rmh.itemmagnet.util.PluginCompat;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class UpdateChecker implements Listener {

    private static final String RELEASES_URL = "https://api.github.com/repos/RMHavelaar101/item-magent/releases/latest";

    private final ItemMagnetPlugin plugin;
    private final Set<UUID> notifiedPlayers = new HashSet<>();
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        UpdateCheckMode mode = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateCheck();
        if (mode == UpdateCheckMode.DISABLED) {
            return;
        }
        checkAsync();
        if (mode == UpdateCheckMode.ALWAYS) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkAsync, 20L * 60 * 60 * 6, 20L * 60 * 60 * 6);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void checkAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) URI.create(RELEASES_URL).toURL().openConnection();
                connection.setRequestProperty("User-Agent", "ItemMagnet/" + PluginCompat.getVersion(plugin));
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                if (connection.getResponseCode() != 200) {
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder body = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                    String tag = extractJsonField(body.toString(), "tag_name");
                    if (tag != null && PluginCompat.isNewerVersion(tag, PluginCompat.getVersion(plugin))) {
                        latestVersion = tag;
                        updateAvailable = true;
                        plugin.getLogger().info("Update available: " + tag);
                    } else {
                        updateAvailable = false;
                    }
                }
            } catch (Exception exception) {
                plugin.getLogger().log(Level.FINE, "Update check failed", exception);
            }
        });
    }

    public void notifyPlayer(Player player) {
        if (!updateAvailable || !player.hasPermission("itemmagnet.updates")) {
            return;
        }
        if (notifiedPlayers.contains(player.getUniqueId())) {
            return;
        }
        notifiedPlayers.add(player.getUniqueId());
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("version", latestVersion == null ? "unknown" : latestVersion);
        placeholders.put("url", "https://hangar.papermc.io/RMHavelaar101/ItemMagnets");
        player.sendMessage(TextUtil.component(TextUtil.color(messages.format("command.update-available", placeholders))));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        notifyPlayer(event.getPlayer());
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    private String extractJsonField(String json, String field) {
        String token = "\"" + field + "\":";
        int index = json.indexOf(token);
        if (index < 0) {
            return null;
        }
        int start = json.indexOf('"', index + token.length()) + 1;
        int end = json.indexOf('"', start);
        if (start <= 0 || end < 0) {
            return null;
        }
        return json.substring(start, end);
    }
}
