package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.ProximityLoreZone;
import org.bukkit.Location;

public final class ProximityLoreMatcher {

    private ProximityLoreMatcher() {
    }

    public static boolean isInside(Location playerLocation, ProximityLoreZone zone) {
        if (playerLocation == null || playerLocation.getWorld() == null) {
            return false;
        }
        if (!playerLocation.getWorld().getName().equalsIgnoreCase(zone.getWorld())) {
            return false;
        }
        double dx = playerLocation.getX() - zone.getX();
        double dz = playerLocation.getZ() - zone.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal > zone.getRadius()) {
            return false;
        }
        return Math.abs(playerLocation.getY() - zone.getY()) <= zone.getYTolerance();
    }
}
