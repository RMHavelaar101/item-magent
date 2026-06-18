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
        return findItems(player, radius, 0);
    }

    public static List<Item> findItems(Player player, double horizontalRadius, double verticalReachBlocks) {
        World world = player.getWorld();
        Location center = player.getLocation();
        double horizontalSearch = Math.max(0.5, horizontalRadius);
        double verticalSearch = MagnetReach.effectiveVerticalSearch(horizontalRadius, verticalReachBlocks);
        Collection<Entity> entities = world.getNearbyEntities(
                center,
                horizontalSearch,
                verticalSearch,
                horizontalSearch,
                entity -> entity instanceof Item
        );
        List<Item> items = new ArrayList<>();
        for (Entity entity : entities) {
            Item item = (Item) entity;
            if (!item.isValid() || item.isDead()) {
                continue;
            }
            if (!MagnetReach.isWithinPullRange(center, item.getLocation(), horizontalRadius, verticalReachBlocks)) {
                continue;
            }
            items.add(item);
        }
        return items;
    }
}
