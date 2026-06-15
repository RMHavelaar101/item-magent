package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.ProximityLoreZone;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ProximityLoreMatcherTest {

    @Test
    void matchesInsideHorizontalRadiusAndYTolerance() {
        World world = Mockito.mock(World.class);
        when(world.getName()).thenReturn("Theryn");
        ProximityLoreZone zone = new ProximityLoreZone(
                "test",
                "Theryn",
                100,
                64,
                -200,
                10,
                8,
                List.of("hello"),
                Map.of()
        );
        Location inside = new Location(world, 105, 66, -205);
        Location outsideHorizontal = new Location(world, 120, 64, -200);
        Location outsideVertical = new Location(world, 100, 80, -200);

        assertTrue(ProximityLoreMatcher.isInside(inside, zone));
        assertFalse(ProximityLoreMatcher.isInside(outsideHorizontal, zone));
        assertFalse(ProximityLoreMatcher.isInside(outsideVertical, zone));
    }

    @Test
    void rejectsDifferentWorld() {
        World theryn = Mockito.mock(World.class);
        World other = Mockito.mock(World.class);
        when(theryn.getName()).thenReturn("Theryn");
        when(other.getName()).thenReturn("Other");
        ProximityLoreZone zone = new ProximityLoreZone(
                "test",
                "Theryn",
                0,
                64,
                0,
                5,
                8,
                List.of("hello"),
                Map.of()
        );

        assertFalse(ProximityLoreMatcher.isInside(new Location(other, 0, 64, 0), zone));
        assertTrue(ProximityLoreMatcher.isInside(new Location(theryn, 1, 64, 1), zone));
    }
}
