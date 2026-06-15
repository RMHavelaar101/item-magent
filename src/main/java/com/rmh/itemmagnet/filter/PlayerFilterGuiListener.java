package com.rmh.itemmagnet.filter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public final class PlayerFilterGuiListener implements Listener {

    private final PlayerFilterGuiService guiService;

    public PlayerFilterGuiListener(PlayerFilterGuiService guiService) {
        this.guiService = guiService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof PlayerFilterGuiHolder filterHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!player.hasPermission("itemmagnet.filter")) {
            player.closeInventory();
            return;
        }
        guiService.handleClick(player, filterHolder, event.getSlot());
    }
}
