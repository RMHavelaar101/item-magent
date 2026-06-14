package com.rmh.itemmagnet.listener;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class CraftListener implements Listener {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final UnlockService unlockService;

    public CraftListener(ItemMagnetPlugin plugin, MagnetItemService itemService, UnlockService unlockService) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.unlockService = unlockService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        if (!(event.getInventory() instanceof CraftingInventory craftingInventory)) {
            return;
        }

        ItemStack result = craftingInventory.getResult();
        if (result == null || !itemService.isMagnet(result)) {
            return;
        }

        itemService.read(result).ifPresent(data -> {
            TierConfig tier = data.getTier();
            if (!unlockService.isUnlocked(player, tier)) {
                event.setCancelled(true);
                MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
                player.sendMessage(TextUtil.component(TextUtil.color(messages.format("craft.locked", Map.of()))));
            }
        });
    }
}
