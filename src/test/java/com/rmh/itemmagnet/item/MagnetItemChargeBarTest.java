package com.rmh.itemmagnet.item;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MagnetItemChargeBarTest {

    @Test
    void stackableMaterialsSkipDurabilityChargeBar() {
        assertFalse(supportsDurabilityChargeBar(Material.RECOVERY_COMPASS));
        assertFalse(supportsDurabilityChargeBar(Material.COMPASS));
        assertFalse(supportsDurabilityChargeBar(Material.CLOCK));
    }

    @Test
    void damageableMaterialsSupportDurabilityChargeBar() {
        assertTrue(supportsDurabilityChargeBar(Material.DIAMOND_PICKAXE));
        assertTrue(supportsDurabilityChargeBar(Material.IRON_SWORD));
    }

    private static boolean supportsDurabilityChargeBar(Material material) {
        return material.getMaxDurability() > 0;
    }
}
