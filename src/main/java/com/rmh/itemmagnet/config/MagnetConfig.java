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
    private final boolean fuelUseEffectiveRadius;
    private final boolean pullArmSwing;
    private final boolean showChargeBar;
    private final Particle particleType;
    private final int denyMessageCooldownTicks;
    private final boolean pullExperience;
    private final HoldMode holdMode;
    private final MultiMagnetPolicy multiMagnetPolicy;
    private final boolean disableInCreative;
    private final boolean disableInSpectator;
    private final WorldFilterConfig worldFilter;
    private final SoundsConfig sounds;
    private final MetricsConfig metrics;
    private final AntiAfkConfig antiAfk;
    private final HeightConfig height;
    private final Map<String, FuelConfig> fuel;
    private final LandsConfig lands;
    private final WorldGuardConfig worldGuard;
    private final TownyConfig towny;
    private final GriefPreventionConfig griefPrevention;
    private final Map<String, TierConfig> tiers;
    private final CommandsConfig commands;

    public MagnetConfig(
            String preset,
            int scanIntervalTicks,
            int maxItemsPerTick,
            double pullStepBlocks,
            double pickupDistance,
            boolean sneakToDisable,
            double fuelRadius,
            boolean fuelUseEffectiveRadius,
            boolean pullArmSwing,
            boolean showChargeBar,
            Particle particleType,
            int denyMessageCooldownTicks,
            boolean pullExperience,
            HoldMode holdMode,
            MultiMagnetPolicy multiMagnetPolicy,
            boolean disableInCreative,
            boolean disableInSpectator,
            WorldFilterConfig worldFilter,
            SoundsConfig sounds,
            MetricsConfig metrics,
            AntiAfkConfig antiAfk,
            HeightConfig height,
            Map<String, FuelConfig> fuel,
            LandsConfig lands,
            WorldGuardConfig worldGuard,
            TownyConfig towny,
            GriefPreventionConfig griefPrevention,
            Map<String, TierConfig> tiers,
            CommandsConfig commands
    ) {
        this.preset = preset;
        this.scanIntervalTicks = scanIntervalTicks;
        this.maxItemsPerTick = maxItemsPerTick;
        this.pullStepBlocks = pullStepBlocks;
        this.pickupDistance = pickupDistance;
        this.sneakToDisable = sneakToDisable;
        this.fuelRadius = fuelRadius;
        this.fuelUseEffectiveRadius = fuelUseEffectiveRadius;
        this.pullArmSwing = pullArmSwing;
        this.showChargeBar = showChargeBar;
        this.particleType = particleType;
        this.denyMessageCooldownTicks = denyMessageCooldownTicks;
        this.pullExperience = pullExperience;
        this.holdMode = holdMode;
        this.multiMagnetPolicy = multiMagnetPolicy;
        this.disableInCreative = disableInCreative;
        this.disableInSpectator = disableInSpectator;
        this.worldFilter = worldFilter;
        this.sounds = sounds;
        this.metrics = metrics;
        this.antiAfk = antiAfk;
        this.height = height;
        this.fuel = Collections.unmodifiableMap(new LinkedHashMap<>(fuel));
        this.lands = lands;
        this.worldGuard = worldGuard;
        this.towny = towny;
        this.griefPrevention = griefPrevention;
        this.tiers = Collections.unmodifiableMap(new LinkedHashMap<>(tiers));
        this.commands = commands;
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

    public boolean isFuelUseEffectiveRadius() {
        return fuelUseEffectiveRadius;
    }

    public boolean isPullArmSwing() {
        return pullArmSwing;
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

    public boolean isPullExperience() {
        return pullExperience;
    }

    public HoldMode getHoldMode() {
        return holdMode;
    }

    public MultiMagnetPolicy getMultiMagnetPolicy() {
        return multiMagnetPolicy;
    }

    public boolean isDisableInCreative() {
        return disableInCreative;
    }

    public boolean isDisableInSpectator() {
        return disableInSpectator;
    }

    public WorldFilterConfig getWorldFilter() {
        return worldFilter;
    }

    public SoundsConfig getSounds() {
        return sounds;
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

    public TownyConfig getTowny() {
        return towny;
    }

    public GriefPreventionConfig getGriefPrevention() {
        return griefPrevention;
    }

    public Map<String, TierConfig> getTiers() {
        return tiers;
    }

    public TierConfig getTier(String id) {
        return tiers.get(id);
    }

    public CommandsConfig getCommands() {
        return commands;
    }
}
