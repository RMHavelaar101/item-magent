package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public final class PullPhysics {

    private PullPhysics() {
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
        World world = from.getWorld();
        Vector delta = target.toVector().subtract(from.toVector());
        double distance = delta.length();
        if (distance <= step) {
            return target.clone();
        }
        Vector movement = delta.normalize().multiply(step);
        Location next = from.clone().add(movement);
        if (!hasLineOfSight(from, next)) {
            return from;
        }
        next.setWorld(world);
        return next;
    }

    public static double distance(Location first, Location second) {
        return first.distance(second);
    }
}
