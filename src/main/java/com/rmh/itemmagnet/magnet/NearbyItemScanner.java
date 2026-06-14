package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class NearbyItemScanner {

    private NearbyItemScanner() {
    }

    public static List<Item> findItems(Player player, double radius) {
        World world = player.getWorld();
        Location center = player.getLocation();
        double searchRadius = Math.max(0.5, radius);
        Collection<Entity> entities = world.getNearbyEntities(center, searchRadius, searchRadius, searchRadius, entity -> entity instanceof Item);
        double radiusSquared = radius * radius;
        List<Item> items = new ArrayList<>();
        for (Entity entity : entities) {
            Item item = (Item) entity;
            if (!item.isValid() || item.isDead()) {
                continue;
            }
            if (item.getLocation().distanceSquared(center) > radiusSquared) {
                continue;
            }
            items.add(item);
        }
        return items;
    }
}
