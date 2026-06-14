package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.HeightConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.config.UndergroundConfig;
import com.rmh.itemmagnet.item.MagnetData;
import org.bukkit.entity.Player;

public final class RadiusCalculator {

    private RadiusCalculator() {
    }

    public static double calculateEffectiveRadius(MagnetConfig config, TierConfig tier, MagnetData data, Player player, long currentTick) {
        double radius = tier.getRadius();
        HeightConfig height = config.getHeight();
        if (height.getUnderground().isEnabled()) {
            UndergroundConfig underground = height.getUnderground();
            if (player.getLocation().getBlockY() < underground.getThresholdY()) {
                radius += underground.getRadiusModifier();
            } else {
                radius += height.getSurface().getRadiusModifier();
            }
        } else {
            radius += height.getSurface().getRadiusModifier();
        }

        if (data.isBoostActive(currentTick)) {
            int boostLevel = data.getBoostLevel();
            FuelConfig blockFuel = config.getFuel().get("REDSTONE_BLOCK");
            if (blockFuel != null) {
                radius += blockFuel.getRadiusBonus() * boostLevel;
            }
        }

        return Math.max(tier.getMinRadius(), Math.min(tier.getMaxRadius(), radius));
    }

    public static double getDrainMultiplier(MagnetConfig config, Player player, MagnetData data) {
        double multiplier = 1.0;
        if (config.getHeight().getUnderground().isEnabled()
                && player.getLocation().getBlockY() < config.getHeight().getUnderground().getThresholdY()) {
            multiplier *= config.getHeight().getUnderground().getDrainMultiplier();
        }
        if (data.getBoostLevel() > 0) {
            multiplier *= data.getTier().getBoostDrainMultiplier();
        }
        return multiplier;
    }

    public static boolean isHeightAllowed(MagnetConfig config, Player player) {
        HeightConfig height = config.getHeight();
        if (!height.isUseYRange()) {
            return true;
        }
        int y = player.getLocation().getBlockY();
        return y >= height.getMinY() && y <= height.getMaxY();
    }
}
