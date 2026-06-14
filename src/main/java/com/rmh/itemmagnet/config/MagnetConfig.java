package com.rmh.itemmagnet.config;

import org.bukkit.Particle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MagnetConfig {

    private final String preset;
    private final int scanIntervalTicks;
    private final int maxItemsPerTick;
    private final double pullStepBlocks;
    private final double pickupDistance;
    private final boolean sneakToDisable;
    private final double fuelRadius;
    private final boolean showChargeBar;
    private final Particle particleType;
    private final int denyMessageCooldownTicks;
    private final MetricsConfig metrics;
    private final AntiAfkConfig antiAfk;
    private final HeightConfig height;
    private final Map<String, FuelConfig> fuel;
    private final LandsConfig lands;
    private final WorldGuardConfig worldGuard;
    private final Map<String, TierConfig> tiers;

    public MagnetConfig(
            String preset,
            int scanIntervalTicks,
            int maxItemsPerTick,
            double pullStepBlocks,
            double pickupDistance,
            boolean sneakToDisable,
            double fuelRadius,
            boolean showChargeBar,
            Particle particleType,
            int denyMessageCooldownTicks,
            MetricsConfig metrics,
            AntiAfkConfig antiAfk,
            HeightConfig height,
            Map<String, FuelConfig> fuel,
            LandsConfig lands,
            WorldGuardConfig worldGuard,
            Map<String, TierConfig> tiers
    ) {
        this.preset = preset;
        this.scanIntervalTicks = scanIntervalTicks;
        this.maxItemsPerTick = maxItemsPerTick;
        this.pullStepBlocks = pullStepBlocks;
        this.pickupDistance = pickupDistance;
        this.sneakToDisable = sneakToDisable;
        this.fuelRadius = fuelRadius;
        this.showChargeBar = showChargeBar;
        this.particleType = particleType;
        this.denyMessageCooldownTicks = denyMessageCooldownTicks;
        this.metrics = metrics;
        this.antiAfk = antiAfk;
        this.height = height;
        this.fuel = Collections.unmodifiableMap(new LinkedHashMap<>(fuel));
        this.lands = lands;
        this.worldGuard = worldGuard;
        this.tiers = Collections.unmodifiableMap(new LinkedHashMap<>(tiers));
    }

    public String getPreset() {
        return preset;
    }

    public int getScanIntervalTicks() {
        return scanIntervalTicks;
    }

    public int getMaxItemsPerTick() {
        return maxItemsPerTick;
    }

    public double getPullStepBlocks() {
        return pullStepBlocks;
    }

    public double getPickupDistance() {
        return pickupDistance;
    }

    public boolean isSneakToDisable() {
        return sneakToDisable;
    }

    public double getFuelRadius() {
        return fuelRadius;
    }

    public boolean isShowChargeBar() {
        return showChargeBar;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public int getDenyMessageCooldownTicks() {
        return denyMessageCooldownTicks;
    }

    public MetricsConfig getMetrics() {
        return metrics;
    }

    public AntiAfkConfig getAntiAfk() {
        return antiAfk;
    }

    public HeightConfig getHeight() {
        return height;
    }

    public Map<String, FuelConfig> getFuel() {
        return fuel;
    }

    public LandsConfig getLands() {
        return lands;
    }

    public WorldGuardConfig getWorldGuard() {
        return worldGuard;
    }

    public Map<String, TierConfig> getTiers() {
        return tiers;
    }

    public TierConfig getTier(String id) {
        return tiers.get(id);
    }
}
