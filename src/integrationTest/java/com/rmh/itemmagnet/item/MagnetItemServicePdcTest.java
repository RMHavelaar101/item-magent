package com.rmh.itemmagnet.item;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.support.IntegrationTestTiers;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MagnetItemServicePdcTest {

    private ItemMagnetPlugin plugin;
    private MagnetItemService itemService;
    private PdcKeys keys;

    @BeforeEach
    void setUp() {
        MockBukkit.unmock();
        MockBukkit.mock();
        plugin = MockBukkit.load(ItemMagnetPlugin.class);
        itemService = plugin.getItemService();
        keys = new PdcKeys(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void roundTripsMagnetData() {
        var tier = IntegrationTestTiers.fragmentTier();
        ItemStack item = itemService.create(tier, 50);

        assertTrue(itemService.isMagnet(item));
        var pdc = item.getItemMeta().getPersistentDataContainer();
        assertEquals((byte) 1, pdc.get(keys.isMagnet, PdcKeys.BYTE));
        assertEquals("fragment", pdc.get(keys.tierId, PdcKeys.STRING));
        assertEquals(50, pdc.get(keys.charge, PdcKeys.INTEGER));
    }
}
