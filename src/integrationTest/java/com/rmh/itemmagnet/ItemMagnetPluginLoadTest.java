package com.rmh.itemmagnet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMagnetPluginLoadTest {

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
    void pluginEnables() {
        assertNotNull(plugin.getMagnetService());
        assertNotNull(plugin.getItemService());
    }
}
