package com.rmh.itemmagnet.listener;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class MagnetListener implements Listener {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final UnlockService unlockService;

    public MagnetListener(ItemMagnetPlugin plugin, MagnetItemService itemService, UnlockService unlockService) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.unlockService = unlockService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        unlockService.discoverRecipesOnJoin(event.getPlayer());
        plugin.getUpdateChecker().notifyPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMagnetService().getAfkTracker().clear(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        plugin.getMagnetService().getAfkTracker().recordMovement(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (!itemService.isMagnet(mainHand)) {
            return;
        }

        if (itemService.transferFuel(player, mainHand, offHand)) {
            event.setCancelled(true);
            MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
            Map<String, String> placeholders = new HashMap<>();
            itemService.read(mainHand).ifPresent(data -> {
                placeholders.put("charge", String.valueOf(data.getCharge()));
                placeholders.put("max_charge", String.valueOf(data.getTier().getMaxCharge()));
                player.sendMessage(TextUtil.component(TextUtil.color(messages.format("magnet.fuel-transferred", placeholders))));
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        if (result == null || !itemService.isMagnet(result)) {
            return;
        }

        itemService.read(result).ifPresent(data -> {
            TierConfig tier = data.getTier();
            if (!unlockService.isUnlocked(player, tier)) {
                inventory.setResult(null);
            }
        });
    }
}
