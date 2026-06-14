package com.rmh.itemmagnet.gui;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public final class ConfigGuiListener implements Listener {

    private final ItemMagnetPlugin plugin;
    private final ConfigGuiService guiService;

    public ConfigGuiListener(ItemMagnetPlugin plugin, ConfigGuiService guiService) {
        this.plugin = plugin;
        this.guiService = guiService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ConfigGuiHolder configHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!player.hasPermission("itemmagnet.config")) {
            player.closeInventory();
            return;
        }
        guiService.handleClick(player, configHolder, event.getSlot(), event.isRightClick(), event.isShiftClick());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!guiService.hasChatSession(player)) {
            return;
        }
        event.setCancelled(true);
        plugin.getServer().getScheduler().runTask(plugin, () -> guiService.handleChatInput(player, event.getMessage()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        guiService.clearChatSession(event.getPlayer());
    }
}
