package com.rmh.itemmagnet.listener;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MagnetListenerTest {

    private ServerMock server;
    private ItemMagnetPlugin plugin;
    private MagnetListener listener;

    @BeforeEach
    void setUp() {
        MockBukkit.unmock();
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ItemMagnetPlugin.class);
        listener = new MagnetListener(
                plugin,
                plugin.getItemService(),
                plugin.getUnlockService(),
                plugin.getMagnetLocator()
        );
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void joinSeedsAfkTracker() {
        PlayerMock player = server.addPlayer("Explorer");
        listener.onJoin(new PlayerJoinEvent(player, ""));
        assertNotNull(plugin.getMagnetService().getAfkTracker());
    }
}
