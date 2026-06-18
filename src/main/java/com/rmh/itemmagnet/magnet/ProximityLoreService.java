package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.ProximityLoreConfig;
import com.rmh.itemmagnet.config.ProximityLoreZone;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class ProximityLoreService {

    private final ItemMagnetPlugin plugin;
    private final MagnetLocator magnetLocator;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private BukkitTask task;

    public ProximityLoreService(ItemMagnetPlugin plugin, MagnetLocator magnetLocator) {
        this.plugin = plugin;
        this.magnetLocator = magnetLocator;
    }

    public void start() {
        stop();
        ProximityLoreConfig config = plugin.getConfigManager().getMagnetConfig().getProximityLore();
        if (!config.isEnabled()) {
            return;
        }
        int interval = Math.max(1, config.getScanIntervalTicks());
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, interval, interval);
    }

    public void restart() {
        stop();
        cooldowns.clear();
        start();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        ProximityLoreConfig config = plugin.getConfigManager().getMagnetConfig().getProximityLore();
        if (!config.isEnabled() || config.getZones().isEmpty()) {
            return;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            processPlayer(player, config);
        }
    }

    private void processPlayer(Player player, ProximityLoreConfig config) {
        if (!player.hasPermission("itemmagnet.use")) {
            return;
        }

        String tierId = null;
        if (config.isRequireActiveMagnet()) {
            MagnetConfig magnetConfig = plugin.getConfigManager().getMagnetConfig();
            Optional<MagnetSlot> slot = magnetLocator.locate(player, magnetConfig);
            if (slot.isEmpty()) {
                return;
            }
            tierId = slot.get().getData().getTier().getId();
        }

        long now = System.currentTimeMillis();
        for (ProximityLoreZone zone : config.getZones()) {
            if (!ProximityLoreMatcher.isInside(player.getLocation(), zone)) {
                continue;
            }
            String cooldownKey = player.getUniqueId() + ":" + zone.getId();
            Long last = cooldowns.get(cooldownKey);
            long cooldownMs = config.getCooldownSeconds() * 1000L;
            if (last != null && now - last < cooldownMs) {
                continue;
            }
            List<String> messages = zone.resolveMessages(tierId);
            if (messages.isEmpty()) {
                continue;
            }
            String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
            player.sendMessage(TextUtil.color(message));
            cooldowns.put(cooldownKey, now);
            return;
        }
    }

    public void clearCooldown(UUID playerId) {
        cooldowns.keySet().removeIf(key -> key.startsWith(playerId + ":"));
    }
}
