package com.rmh.itemmagnet.listener;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.FuelTransferResult;
import com.rmh.itemmagnet.item.FuelTransferStatus;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.magnet.FuelTransferHelper;
import com.rmh.itemmagnet.magnet.MagnetLocator;
import com.rmh.itemmagnet.magnet.MagnetSlot;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MagnetListener implements Listener {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final UnlockService unlockService;
    private final MagnetLocator magnetLocator;

    public MagnetListener(
            ItemMagnetPlugin plugin,
            MagnetItemService itemService,
            UnlockService unlockService,
            MagnetLocator magnetLocator
    ) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.unlockService = unlockService;
        this.magnetLocator = magnetLocator;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var antiAfk = plugin.getConfigManager().getMagnetConfig().getAntiAfk();
        if (antiAfk.isEnabled()) {
            plugin.getMagnetService().getAfkTracker().seed(
                    event.getPlayer(),
                    antiAfk.getRequiredBlocksMoved()
            );
        }
        unlockService.discoverRecipesOnJoin(event.getPlayer());
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onFuelTransfer(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        Optional<MagnetSlot> magnetSlotOptional = magnetLocator.locate(player, plugin.getConfigManager().getMagnetConfig());
        if (magnetSlotOptional.isEmpty()) {
            return;
        }

        MagnetSlot magnetSlot = magnetSlotOptional.get();
        Optional<FuelTransferHelper.FuelHand> fuelHandOptional = FuelTransferHelper.findFuelHand(player, magnetSlot, itemService);
        if (fuelHandOptional.isEmpty()) {
            return;
        }

        denyInteraction(event);

        FuelTransferHelper.FuelHand fuelHand = fuelHandOptional.get();
        FuelTransferResult result = itemService.transferFuel(
                player,
                magnetSlot,
                fuelHand.stack(),
                fuelHand.slot()
        );
        handleFuelTransferResult(player, result, fuelHand.stack().getType());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onFuelBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        if (!FuelTransferHelper.hasMagnetAndFuel(
                player,
                plugin.getConfigManager().getMagnetConfig(),
                magnetLocator,
                itemService
        )) {
            return;
        }
        if (itemService.getFuelConfig(event.getItemInHand().getType()) == null) {
            return;
        }
        event.setCancelled(true);
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

    private void denyInteraction(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }

    private void handleFuelTransferResult(Player player, FuelTransferResult result, Material fuelMaterial) {
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        if (result.status() == FuelTransferStatus.FULL) {
            player.sendMessage(TextUtil.component(TextUtil.color(messages.format("magnet.fuel-full", Map.of()))));
            return;
        }
        if (!result.transferred()) {
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        magnetLocator.locate(player, plugin.getConfigManager().getMagnetConfig()).ifPresent(magnetSlot -> {
            itemService.read(magnetSlot.getItemStack()).ifPresent(data -> {
                placeholders.put("charge", String.valueOf(data.getCharge()));
                placeholders.put("max_charge", String.valueOf(data.getTier().getMaxCharge()));
            });
        });
        player.sendMessage(TextUtil.component(TextUtil.color(messages.format("magnet.fuel-transferred", placeholders))));
        if (result.boostDurationSeconds() > 0) {
            placeholders.put("seconds", String.valueOf(result.boostDurationSeconds()));
            player.sendMessage(TextUtil.component(TextUtil.color(messages.format("magnet.boost-active", placeholders))));
        }
        plugin.getMagnetService().getSoundService().playFuel(player, fuelMaterial);
    }
}
