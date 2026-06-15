package com.rmh.itemmagnet.command;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.support.IntegrationTestTiers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMagnetCommandTest {

    private ServerMock server;
    private ItemMagnetPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.unmock();
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ItemMagnetPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void givePutsMagnetInInventory() {
        PlayerMock staff = server.addPlayer("Staff");
        staff.addAttachment(plugin, "itemmagnet.give", true);
        PlayerMock target = server.addPlayer("Explorer");
        target.getInventory().addItem(plugin.getItemService().create(IntegrationTestTiers.fragmentTier(), 25));

        assertTrue(plugin.getItemService().isMagnet(target.getInventory().getItem(0)));
    }
}
