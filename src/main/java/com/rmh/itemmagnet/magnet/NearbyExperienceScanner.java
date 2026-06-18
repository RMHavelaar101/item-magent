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
        return findExperienceOrbs(player, radius, 0);
    }

    public static List<ExperienceOrb> findExperienceOrbs(
            Player player,
            double horizontalRadius,
            double verticalReachBlocks
    ) {
        World world = player.getWorld();
        Location center = player.getLocation();
        double horizontalSearch = Math.max(0.5, horizontalRadius);
        double verticalSearch = MagnetReach.effectiveVerticalSearch(horizontalRadius, verticalReachBlocks);
        Collection<Entity> entities = world.getNearbyEntities(
                center,
                horizontalSearch,
                verticalSearch,
                horizontalSearch,
                entity -> entity instanceof ExperienceOrb
        );
        List<ExperienceOrb> orbs = new ArrayList<>();
        for (Entity entity : entities) {
            ExperienceOrb orb = (ExperienceOrb) entity;
            if (!orb.isValid() || orb.isDead() || orb.getExperience() <= 0) {
                continue;
            }
            if (!MagnetReach.isWithinPullRange(center, orb.getLocation(), horizontalRadius, verticalReachBlocks)) {
                continue;
            }
            orbs.add(orb);
        }
        return orbs;
    }
}
