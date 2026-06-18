package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MagnetReachTest {

    private final World world = Mockito.mock(World.class);

    @Test
    void legacySphereUsesThreeDimensionalDistance() {
        Location center = new Location(world, 0, 64, 0);
        Location within = new Location(world, 3, 64, 4);
        Location outside = new Location(world, 4, 64, 4);

        assertTrue(MagnetReach.isWithinPullRange(center, within, 5, 0));
        assertFalse(MagnetReach.isWithinPullRange(center, outside, 5, 0));
    }

    @Test
    void verticalReachAllowsDifferentYWithinHorizontalRadius() {
        Location center = new Location(world, 0, 64, 0);
        Location oneBlockBelow = new Location(world, 2, 63, 0);
        Location threeBlocksBelow = new Location(world, 2, 61, 0);

        assertTrue(MagnetReach.isWithinPullRange(center, oneBlockBelow, 6, 2));
        assertFalse(MagnetReach.isWithinPullRange(center, threeBlocksBelow, 6, 2));
    }

    @Test
    void pickupRangeUsesHorizontalAndVerticalLimits() {
        Location player = new Location(world, 0, 64, 0);
        Location nearbyBelow = new Location(world, 1, 63, 0);
        Location tooFarBelow = new Location(world, 1, 61, 0);

        assertTrue(MagnetReach.isWithinPickupRange(player, nearbyBelow, 1.5, 2));
        assertFalse(MagnetReach.isWithinPickupRange(player, tooFarBelow, 1.5, 2));
    }
}
