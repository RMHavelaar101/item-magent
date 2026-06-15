package com.rmh.itemmagnet.api;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.integration.IntegrationStatusService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public final class ItemMagnetApi {

    private static ItemMagnetPlugin plugin;

    private ItemMagnetApi() {
    }

    public static void init(ItemMagnetPlugin itemMagnetPlugin) {
        plugin = itemMagnetPlugin;
    }

    public static ItemMagnetPlugin getPlugin() {
        return plugin;
    }

    public static boolean isEnabled() {
        return plugin != null && plugin.isEnabled();
    }

    public static void grantUnlock(Player player, String tierId) {
        requireEnabled();
        TierConfig tier = requireTier(tierId);
        plugin.getUnlockService().grantUnlock(player, tier);
    }

    public static void giveMagnet(Player player, String tierId, int charge) {
        requireEnabled();
        TierConfig tier = requireTier(tierId);
        int effectiveCharge = charge >= 0 ? charge : tier.getMaxCharge() / 2;
        ItemStack item = plugin.getItemService().create(tier, effectiveCharge);
        player.getInventory().addItem(item);
        plugin.onPlayerReceivedMagnet(player);
    }

    public static boolean isTierUnlocked(Player player, String tierId) {
        if (!isEnabled()) {
            return false;
        }
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(tierId);
        if (tier == null) {
            return false;
        }
        return plugin.getUnlockService().isUnlocked(player, tier);
    }

    public static Map<String, Boolean> getHookStatus() {
        if (!isEnabled()) {
            return Collections.emptyMap();
        }
        IntegrationStatusService statusService = plugin.getIntegrationStatusService();
        if (statusService == null) {
            return Collections.emptyMap();
        }
        return statusService.getHookStatus();
    }

    private static void requireEnabled() {
        if (!isEnabled()) {
            throw new IllegalStateException("ItemMagnet is not enabled");
        }
    }

    private static TierConfig requireTier(String tierId) {
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(tierId);
        if (tier == null) {
            throw new IllegalArgumentException("Unknown magnet tier: " + tierId);
        }
        return tier;
    }
}
