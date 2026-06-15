package com.rmh.itemmagnet.integration;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.filter.PlayerFilterStorage;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.magnet.MagnetLocator;
import com.rmh.itemmagnet.magnet.MagnetSlot;
import com.rmh.itemmagnet.magnet.RadiusCalculator;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public final class ItemMagnetPlaceholderExpansion extends PlaceholderExpansion {

    private final ItemMagnetPlugin plugin;
    private final MagnetLocator magnetLocator;
    private final PlayerFilterStorage playerFilterStorage;

    public ItemMagnetPlaceholderExpansion(
            ItemMagnetPlugin plugin,
            MagnetLocator magnetLocator,
            PlayerFilterStorage playerFilterStorage
    ) {
        this.plugin = plugin;
        this.magnetLocator = magnetLocator;
        this.playerFilterStorage = playerFilterStorage;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "itemmagnet";
    }

    @Override
    public @NotNull String getAuthor() {
        return "rmh";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        String key = params.toLowerCase(Locale.ROOT);

        if ("filter_count".equals(key)) {
            return String.valueOf(playerFilterStorage.getFilterRuleCount(player.getUniqueId()));
        }
        if ("server_blacklist_count".equals(key)) {
            return String.valueOf(config.getServerItemFilter().getRuleCount());
        }

        Optional<MagnetSlot> slotOptional = magnetLocator.locate(player, config);
        if (slotOptional.isEmpty()) {
            return switch (key) {
                case "active" -> "false";
                case "boost_active" -> "false";
                default -> "";
            };
        }

        MagnetSlot slot = slotOptional.get();
        MagnetData data = slot.getData();
        TierConfig tier = data.getTier();
        long tick = plugin.getServer().getCurrentTick();
        double radius = RadiusCalculator.calculateEffectiveRadius(config, tier, data, player, tick);
        boolean boostActive = data.isBoostActive(tick);

        return switch (key) {
            case "active" -> "true";
            case "tier" -> tier.getId();
            case "charge" -> String.valueOf(data.getCharge());
            case "max_charge" -> String.valueOf(tier.getMaxCharge());
            case "radius" -> String.format(Locale.ROOT, "%.2f", radius);
            case "boost" -> String.valueOf(data.getBoostLevel());
            case "boost_active" -> String.valueOf(boostActive);
            default -> null;
        };
    }

    public static void tryRegister(
            ItemMagnetPlugin plugin,
            MagnetLocator magnetLocator,
            PlayerFilterStorage playerFilterStorage
    ) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }
        new ItemMagnetPlaceholderExpansion(plugin, magnetLocator, playerFilterStorage).register();
    }
}
