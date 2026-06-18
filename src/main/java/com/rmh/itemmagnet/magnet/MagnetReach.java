package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;

public final class MagnetReach {

    private MagnetReach() {
    }

    public static boolean isWithinPullRange(
            Location center,
            Location target,
            double horizontalRadius,
            double verticalReachBlocks
    ) {
        if (verticalReachBlocks <= 0) {
            return center.distanceSquared(target) <= horizontalRadius * horizontalRadius;
        }

        double deltaX = target.getX() - center.getX();
        double deltaZ = target.getZ() - center.getZ();
        if (deltaX * deltaX + deltaZ * deltaZ > horizontalRadius * horizontalRadius) {
            return false;
        }

        return Math.abs(target.getY() - center.getY()) <= verticalReachBlocks;
    }

    public static boolean isWithinPickupRange(
            Location playerLocation,
            Location itemLocation,
            double pickupDistance,
            double verticalReachBlocks
    ) {
        if (verticalReachBlocks <= 0) {
            return playerLocation.distance(itemLocation) <= pickupDistance;
        }

        double deltaX = itemLocation.getX() - playerLocation.getX();
        double deltaZ = itemLocation.getZ() - playerLocation.getZ();
        if (deltaX * deltaX + deltaZ * deltaZ > pickupDistance * pickupDistance) {
            return false;
        }

        double verticalLimit = Math.max(verticalReachBlocks, pickupDistance);
        return Math.abs(itemLocation.getY() - playerLocation.getY()) <= verticalLimit;
    }

    public static double effectiveVerticalSearch(double horizontalRadius, double verticalReachBlocks) {
        if (verticalReachBlocks > 0) {
            return Math.max(0.5, verticalReachBlocks);
        }
        return Math.max(0.5, horizontalRadius);
    }
}
