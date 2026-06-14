package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.HoldMode;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.MultiMagnetPolicy;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.item.MagnetItemService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class MagnetLocator {

    private final MagnetItemService itemService;

    public MagnetLocator(MagnetItemService itemService) {
        this.itemService = itemService;
    }

    public Optional<MagnetSlot> locate(Player player, MagnetConfig config) {
        HoldMode holdMode = config.getHoldMode();
        List<MagnetSlot> candidates = new ArrayList<>();

        if (holdMode == HoldMode.MAIN_HAND) {
            addCandidate(candidates, player.getInventory().getHeldItemSlot(), player.getInventory().getItemInMainHand());
            return candidates.isEmpty() ? Optional.empty() : Optional.of(candidates.get(0));
        }

        PlayerInventory inventory = player.getInventory();
        int start = 0;
        int end = holdMode == HoldMode.HOTBAR ? 8 : 35;
        for (int slot = start; slot <= end; slot++) {
            addCandidate(candidates, slot, inventory.getItem(slot));
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        if (config.getMultiMagnetPolicy() == MultiMagnetPolicy.FIRST_FOUND) {
            return Optional.of(candidates.get(0));
        }

        return candidates.stream()
                .max(Comparator.comparingDouble(slot -> tierScore(slot.getData().getTier())))
                .or(() -> Optional.of(candidates.get(0)));
    }

    private void addCandidate(List<MagnetSlot> candidates, int slot, ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return;
        }
        itemService.read(stack).ifPresent(data -> candidates.add(new MagnetSlot(slot, stack, data)));
    }

    private double tierScore(TierConfig tier) {
        return tier.getRadius();
    }
}
