package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.VerticalPullMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public final class PullPhysics {

    private static final double ITEM_HEIGHT = 0.45;
    private static final double ITEM_PROBE_OFFSET = 0.125;

    private PullPhysics() {
    }

    public static Location pullTargetFromPlayer(Player player) {
        return player.getLocation().clone().add(0, 0.55, 0);
    }

    public static boolean hasLineOfSight(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            return false;
        }
        Vector direction = to.toVector().subtract(from.toVector());
        double distance = direction.length();
        if (distance < 0.001) {
            return true;
        }
        direction.normalize();
        BlockIterator iterator = new BlockIterator(from.getWorld(), from.toVector(), direction, 0.0, (int) Math.ceil(distance));
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getLocation().distanceSquared(from) < 0.25) {
                continue;
            }
            if (block.getLocation().distanceSquared(to) < 0.25) {
                break;
            }
            if (block.isPassable()) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static Location stepToward(Location from, Location target, double step) {
        return stepToward(from, target, step, VerticalPullMode.DIRECT);
    }

    public static Location stepToward(Location from, Location target, double step, VerticalPullMode mode) {
        if (mode == VerticalPullMode.HORIZONTAL_FIRST) {
            return stepTowardHorizontalFirst(from, target, step);
        }
        return stepTowardDirect(from, target, step);
    }

    private static Location stepTowardDirect(Location from, Location target, double step) {
        World world = from.getWorld();
        Vector delta = target.toVector().subtract(from.toVector());
        double distance = delta.length();
        if (distance <= step) {
            Location destination = target.clone();
            return canItemStepTo(from, destination) ? destination : from;
        }
        Vector movement = delta.normalize().multiply(step);
        Location next = from.clone().add(movement);
        if (!canItemStepTo(from, next)) {
            return from;
        }
        next.setWorld(world);
        return next;
    }

    private static Location stepTowardHorizontalFirst(Location from, Location target, double step) {
        Location moved = tryHorizontalStep(from, target, step);
        if (moved != null) {
            return moved;
        }

        moved = tryVerticalStep(from, target, step);
        if (moved != null) {
            return moved;
        }

        moved = tryAxisComponentStep(from, target, step);
        if (moved != null) {
            return moved;
        }

        Location direct = stepTowardDirect(from, target, step);
        if (!direct.equals(from)) {
            return direct;
        }

        return from;
    }

    private static Location tryHorizontalStep(Location from, Location target, double step) {
        double deltaX = target.getX() - from.getX();
        double deltaZ = target.getZ() - from.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (horizontalDistance <= 0.05) {
            return null;
        }

        double horizontalStep = Math.min(step, horizontalDistance);
        Vector movement = new Vector(deltaX, 0, deltaZ).normalize().multiply(horizontalStep);
        Location next = from.clone().add(movement);
        if (!canItemStepTo(from, next)) {
            return null;
        }
        next.setWorld(from.getWorld());
        return next;
    }

    private static Location tryVerticalStep(Location from, Location target, double step) {
        double deltaY = target.getY() - from.getY();
        if (Math.abs(deltaY) <= 0.05) {
            return null;
        }

        double verticalStep = Math.min(step, Math.abs(deltaY));
        Location next = from.clone().add(0, Math.signum(deltaY) * verticalStep, 0);
        if (!canItemStepTo(from, next)) {
            return null;
        }
        next.setWorld(from.getWorld());
        return next;
    }

    private static Location tryAxisComponentStep(Location from, Location target, double step) {
        double deltaX = target.getX() - from.getX();
        if (Math.abs(deltaX) > 0.05) {
            double xStep = Math.signum(deltaX) * Math.min(step, Math.abs(deltaX));
            Location next = from.clone().add(xStep, 0, 0);
            if (canItemStepTo(from, next)) {
                next.setWorld(from.getWorld());
                return next;
            }
        }

        double deltaZ = target.getZ() - from.getZ();
        if (Math.abs(deltaZ) > 0.05) {
            double zStep = Math.signum(deltaZ) * Math.min(step, Math.abs(deltaZ));
            Location next = from.clone().add(0, 0, zStep);
            if (canItemStepTo(from, next)) {
                next.setWorld(from.getWorld());
                return next;
            }
        }

        return null;
    }

    static boolean canItemStepTo(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            return false;
        }
        if (!canItemExistAt(to)) {
            return false;
        }

        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        double deltaZ = to.getZ() - from.getZ();
        double horizontalDistanceSquared = deltaX * deltaX + deltaZ * deltaZ;

        if (Math.abs(deltaY) <= 0.001 && horizontalDistanceSquared > 0.001) {
            return hasHorizontalItemPath(from, to);
        }
        if (horizontalDistanceSquared <= 0.001 && Math.abs(deltaY) > 0.001) {
            return isVerticalItemPathClear(from, to);
        }

        return hasLineOfSight(from, to);
    }

    private static boolean hasHorizontalItemPath(Location from, Location to) {
        Location probeFrom = probeLocation(from);
        Location probeTo = probeLocation(to);
        return hasLineOfSight(probeFrom, probeTo);
    }

    private static boolean isVerticalItemPathClear(Location from, Location to) {
        World world = from.getWorld();
        int blockX = from.getBlockX();
        int blockZ = from.getBlockZ();
        int minBlockY = (int) Math.floor(Math.min(from.getY(), to.getY()) - 0.05);
        int maxBlockY = (int) Math.floor(Math.max(from.getY(), to.getY()) + ITEM_HEIGHT);
        int startBlockY = (int) Math.floor(from.getY() - 0.05);

        for (int y = minBlockY; y <= maxBlockY; y++) {
            Block block = world.getBlockAt(blockX, y, blockZ);
            if (block.isPassable()) {
                continue;
            }
            if (y <= startBlockY) {
                continue;
            }
            return false;
        }
        return canItemExistAt(to);
    }

    private static boolean canItemExistAt(Location location) {
        Block feet = location.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        return feet.isPassable() && head.isPassable();
    }

    private static Location probeLocation(Location location) {
        return location.clone().add(0, ITEM_PROBE_OFFSET, 0);
    }

    public static double distance(Location first, Location second) {
        return first.distance(second);
    }
}
