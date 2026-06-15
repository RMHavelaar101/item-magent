package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.protection.ProtectionService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class PullEligibilityService {

    private final MagnetConfig config;
    private final PlayerFilterStorage playerFilterStorage;
    private final ProtectionService protectionService;

    public PullEligibilityService(
            MagnetConfig config,
            PlayerFilterStorage playerFilterStorage,
            ProtectionService protectionService
    ) {
        this.config = config;
        this.playerFilterStorage = playerFilterStorage;
        this.protectionService = protectionService;
    }

    public Optional<PullBlockReason> evaluateItemPull(Player player, ItemStack stack, TierConfig tier, Location itemLocation) {
        Material material = stack.getType();
        if (config.getServerItemFilter().blocks(material)) {
            return Optional.of(PullBlockReason.SERVER_BLACKLIST);
        }
        Optional<PullBlockReason> tierReason = tier.getBlockReason(material);
        if (tierReason.isPresent()) {
            return tierReason;
        }
        if (playerFilterStorage.isBlacklisted(player.getUniqueId(), material)) {
            return Optional.of(PullBlockReason.PLAYER_BLACKLIST);
        }
        if (!protectionService.canPull(player, itemLocation)) {
            return Optional.of(PullBlockReason.PROTECTION);
        }
        return Optional.empty();
    }

    public boolean hasInventorySpace(Player player, ItemStack stack) {
        if (player.getInventory().firstEmpty() != -1) {
            return true;
        }
        for (ItemStack slot : player.getInventory().getStorageContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                return true;
            }
            if (slot.isSimilar(stack) && slot.getAmount() < slot.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
}
