package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class NearbyExperienceScanner {

    private NearbyExperienceScanner() {
    }

    public static List<ExperienceOrb> findExperienceOrbs(Player player, double radius) {
        World world = player.getWorld();
        Location center = player.getLocation();
        double searchRadius = Math.max(0.5, radius);
        Collection<Entity> entities = world.getNearbyEntities(
                center,
                searchRadius,
                searchRadius,
                searchRadius,
                entity -> entity instanceof ExperienceOrb
        );
        double radiusSquared = radius * radius;
        List<ExperienceOrb> orbs = new ArrayList<>();
        for (Entity entity : entities) {
            ExperienceOrb orb = (ExperienceOrb) entity;
            if (!orb.isValid() || orb.isDead() || orb.getExperience() <= 0) {
                continue;
            }
            if (orb.getLocation().distanceSquared(center) > radiusSquared) {
                continue;
            }
            orbs.add(orb);
        }
        return orbs;
    }
}
