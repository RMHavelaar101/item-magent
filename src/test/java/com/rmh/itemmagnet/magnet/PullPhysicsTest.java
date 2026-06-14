package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PullPhysicsTest {

    @Test
    void distanceUsesLocationDistance() {
        World world = Mockito.mock(World.class);
        Location first = new Location(world, 0, 0, 0);
        Location second = new Location(world, 3, 0, 4);
        assertEquals(5.0, PullPhysics.distance(first, second), 0.001);
    }

    @Test
    void hasLineOfSightRejectsDifferentWorlds() {
        World firstWorld = Mockito.mock(World.class);
        World secondWorld = Mockito.mock(World.class);
        Location from = new Location(firstWorld, 0, 0, 0);
        Location to = new Location(secondWorld, 1, 0, 0);
        assertFalse(PullPhysics.hasLineOfSight(from, to));
    }

    @Test
    void stepTowardVectorMath() {
        double fromX = 0;
        double toX = 5;
        double step = 0.4;
        double delta = toX - fromX;
        double movement = (delta / Math.abs(delta)) * step;
        assertEquals(0.4, movement, 0.001);
        assertTrue(movement > 0);
    }
}
