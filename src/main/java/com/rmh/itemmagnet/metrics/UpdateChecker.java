package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.UpdateCheckMode;
import com.rmh.itemmagnet.util.PluginCompat;
import com.rmh.itemmagnet.util.PluginUrls;
import com.rmh.itemmagnet.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

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
import java.util.logging.Logger;

public final class UpdateChecker implements Listener {

    private final ItemMagnetPlugin plugin;
    private final Set<UUID> notifiedPlayers = new HashSet<>();
    private final Logger logger;
    private String latestVersion;
    private boolean updateAvailable;
    private BukkitTask periodicTask;
    private boolean listenerRegistered;

    public UpdateChecker(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void start() {
        UpdateCheckMode mode = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateCheck();
        if (mode == UpdateCheckMode.DISABLED) {
            return;
        }
        if (!listenerRegistered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            listenerRegistered = true;
        }
        checkAsync();
        if (mode == UpdateCheckMode.ALWAYS && periodicTask == null) {
            periodicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    this::performCheck,
                    20L * 60 * 60 * 6,
                    20L * 60 * 60 * 6
            );
        }
    }

    public void restart() {
        if (periodicTask != null) {
            periodicTask.cancel();
            periodicTask = null;
        }
        latestVersion = null;
        updateAvailable = false;
        notifiedPlayers.clear();
        UpdateCheckMode mode = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateCheck();
        if (mode == UpdateCheckMode.DISABLED) {
            return;
        }
        start();
    }

    public void checkAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::performCheck);
    }

    private void performCheck() {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(PluginUrls.GITHUB_RELEASES_API).toURL().openConnection();
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
                String localVersion = PluginCompat.getVersion(plugin);
                if (tag != null && PluginCompat.isNewerVersion(tag, localVersion)) {
                    boolean newlyAvailable = !updateAvailable || !tag.equals(latestVersion);
                    latestVersion = tag;
                    updateAvailable = true;
                    if (newlyAvailable) {
                        Bukkit.getScheduler().runTask(plugin, this::onUpdateDetected);
                    }
                } else {
                    updateAvailable = false;
                    latestVersion = tag;
                }
            }
        } catch (Exception exception) {
            logger.log(Level.FINE, "Update check failed", exception);
        }
    }

    private void onUpdateDetected() {
        logBootUpdateBanner();
        for (Player player : Bukkit.getOnlinePlayers()) {
            notifyPlayer(player);
        }
    }

    private void logBootUpdateBanner() {
        String currentVersion = PluginCompat.getVersion(plugin);
        String latest = latestVersion == null ? "unknown" : latestVersion;
        String downloadUrl = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateDownloadUrl();

        logger.info("==================================================");
        logger.info(" UPDATE AVAILABLE");
        logger.info(" Running: " + currentVersion + "  ->  Latest: " + latest);
        logger.info(" Download: " + downloadUrl);
        logger.info(" Website:  " + PluginUrls.WEBSITE);
        logger.info("==================================================");
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
        String downloadUrl = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateDownloadUrl();

        Component message = TextUtil.component(messages.format("command.update-available", placeholders));
        Component link = TextUtil.clickableLink(
                messages.format("command.update-available-link", placeholders),
                downloadUrl
        );
        player.sendMessage(message.append(link));
    }

    public Component buildUpdateAvailableMessage(Map<String, String> placeholders) {
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        String downloadUrl = plugin.getConfigManager().getMagnetConfig().getMetrics().getUpdateDownloadUrl();
        Component message = TextUtil.component(messages.format("command.version-update-available", placeholders));
        Component link = TextUtil.clickableLink(
                messages.format("command.update-available-link", placeholders),
                downloadUrl
        );
        return message.append(link);
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
