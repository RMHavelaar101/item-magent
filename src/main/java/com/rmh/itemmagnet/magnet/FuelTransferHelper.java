package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.item.MagnetItemService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

public final class FuelTransferHelper {

    public static final int OFF_HAND_SLOT = 40;

    private FuelTransferHelper() {
    }

    public static Optional<FuelHand> findFuelHand(Player player, MagnetSlot magnetSlot, MagnetItemService itemService) {
        PlayerInventory inventory = player.getInventory();
        int magnetSlotIndex = magnetSlot.getSlot();
        int heldSlot = inventory.getHeldItemSlot();

        Optional<FuelHand> offHandFuel = fuelFromStack(inventory.getItemInOffHand(), OFF_HAND_SLOT, magnetSlotIndex, itemService);
        if (offHandFuel.isPresent()) {
            return offHandFuel;
        }

        Optional<FuelHand> mainHandFuel = fuelFromStack(inventory.getItemInMainHand(), heldSlot, magnetSlotIndex, itemService);
        if (mainHandFuel.isPresent()) {
            return mainHandFuel;
        }

        return Optional.empty();
    }

    public static boolean hasMagnetAndFuel(Player player, MagnetConfig config, MagnetLocator magnetLocator, MagnetItemService itemService) {
        return magnetLocator.locate(player, config)
                .flatMap(magnetSlot -> findFuelHand(player, magnetSlot, itemService))
                .isPresent();
    }

    private static Optional<FuelHand> fuelFromStack(
            ItemStack stack,
            int slot,
            int magnetSlotIndex,
            MagnetItemService itemService
    ) {
        if (slot == magnetSlotIndex) {
            return Optional.empty();
        }
        if (stack == null || stack.getType() == Material.AIR || itemService.isMagnet(stack)) {
            return Optional.empty();
        }
        if (itemService.getFuelConfig(stack.getType()) == null) {
            return Optional.empty();
        }
        return Optional.of(new FuelHand(slot, stack));
    }

    public record FuelHand(int slot, ItemStack stack) {
    }
}
