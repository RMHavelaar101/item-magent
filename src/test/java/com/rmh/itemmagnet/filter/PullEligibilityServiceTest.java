package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.protection.ProtectionService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PullEligibilityServiceTest {

    @Test
    void serverBlacklistTakesPriority() {
        MagnetConfig config = mock(MagnetConfig.class);
        MaterialFilterRule serverRule = MaterialFilterResolver.resolve(java.util.List.of("BEDROCK"), java.util.List.of(), null);
        when(config.getServerItemFilter()).thenReturn(serverRule);

        PlayerFilterStorage storage = mock(PlayerFilterStorage.class);
        when(storage.isBlacklisted(any(), any())).thenReturn(false);

        ProtectionService protection = mock(ProtectionService.class);
        when(protection.canPull(any(), any())).thenReturn(true);

        PullEligibilityService service = new PullEligibilityService(config, storage, protection);
        TierConfig tier = mock(TierConfig.class);
        when(tier.getBlockReason(any())).thenReturn(Optional.empty());

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(Material.BEDROCK);

        Optional<PullBlockReason> reason = service.evaluateItemPull(
                player,
                stack,
                tier,
                mock(Location.class)
        );
        assertEquals(Optional.of(PullBlockReason.SERVER_BLACKLIST), reason);
    }

    @Test
    void detectsInventorySpaceForEmptySlot() {
        MagnetConfig config = mock(MagnetConfig.class);
        when(config.getServerItemFilter()).thenReturn(MaterialFilterRule.empty());

        PlayerFilterStorage storage = mock(PlayerFilterStorage.class);
        ProtectionService protection = mock(ProtectionService.class);

        PullEligibilityService service = new PullEligibilityService(config, storage, protection);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.firstEmpty()).thenReturn(0);

        ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(Material.DIRT);

        assertTrue(service.hasInventorySpace(player, stack));
    }
}
