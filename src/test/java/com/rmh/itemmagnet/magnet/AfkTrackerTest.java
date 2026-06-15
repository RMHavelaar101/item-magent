package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AfkTrackerTest {

    private static final double REQUIRED_BLOCKS = 2.0;
    private static final int WINDOW_SECONDS = 60;

    @Test
    void isAfkReturnsFalseWhenNoRecordExists() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();

        assertFalse(tracker.isAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    @Test
    void shouldNotifyAfkReturnsFalseWhenNoRecordExists() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();

        assertFalse(tracker.shouldNotifyAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    @Test
    void markAfkNotifiedSuppressesRepeatNotifications() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();
        tracker.seed(player, REQUIRED_BLOCKS);
        tracker.markAfkNotified(player);

        assertFalse(tracker.shouldNotifyAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    @Test
    void seedPreventsImmediateAfkOnJoin() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();
        tracker.seed(player, REQUIRED_BLOCKS);

        assertFalse(tracker.isAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    @Test
    void clearRemovesTrackingState() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();
        tracker.seed(player, REQUIRED_BLOCKS);
        tracker.clear(player);

        assertFalse(tracker.isAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    @Test
    void markAfkNotifiedCreatesRecordWhenMissing() {
        AfkTracker tracker = new AfkTracker();
        Player player = mockPlayer();

        tracker.markAfkNotified(player);

        assertFalse(tracker.shouldNotifyAfk(player, REQUIRED_BLOCKS, WINDOW_SECONDS));
    }

    private Player mockPlayer() {
        Player player = mock(Player.class);
        World world = mock(World.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getLocation()).thenReturn(new Location(world, 0, 64, 0));
        when(world.getName()).thenReturn("world");
        return player;
    }
}
