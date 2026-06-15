package com.rmh.itemmagnet.api;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.api.event.MagnetTierUnlockedEvent;
import com.rmh.itemmagnet.support.IntegrationTestConfigLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMagnetApiTest {

    private ServerMock server;
    private ItemMagnetPlugin plugin;

    @BeforeEach
    void setUp() throws Exception {
        MockBukkit.unmock();
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ItemMagnetPlugin.class);
        IntegrationTestConfigLoader.reloadWithBundledConfig(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void grantUnlockFiresEvent() {
        PlayerMock player = server.addPlayer("Explorer");
        AtomicReference<String> unlockedTier = new AtomicReference<>();

        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            void onUnlock(MagnetTierUnlockedEvent event) {
                unlockedTier.set(event.getTierId());
            }
        }, plugin);

        ItemMagnetApi.grantUnlock(player, "survey");

        assertEquals("survey", unlockedTier.get());
    }

    @Test
    void giveMagnetAddsItemToInventory() {
        PlayerMock player = server.addPlayer("Explorer");

        ItemMagnetApi.giveMagnet(player, "fragment", 42);

        assertNotNull(player.getInventory().getItem(0));
        assertTrue(plugin.getItemService().isMagnet(player.getInventory().getItem(0)));
    }

    @Test
    void getHookStatusReturnsIntegrationKeys() {
        assertTrue(ItemMagnetApi.isEnabled());
        assertNotNull(ItemMagnetApi.getHookStatus().get("lands"));
        assertNotNull(ItemMagnetApi.getHookStatus().get("placeholderapi"));
    }
}
