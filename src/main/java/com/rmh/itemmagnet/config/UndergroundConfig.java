package com.rmh.itemmagnet.config;

public final class UndergroundConfig {

    private final boolean enabled;
    private final int thresholdY;
    private final double radiusModifier;
    private final double drainMultiplier;

    public UndergroundConfig(boolean enabled, int thresholdY, double radiusModifier, double drainMultiplier) {
        this.enabled = enabled;
        this.thresholdY = thresholdY;
        this.radiusModifier = radiusModifier;
        this.drainMultiplier = drainMultiplier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getThresholdY() {
        return thresholdY;
    }

    public double getRadiusModifier() {
        return radiusModifier;
    }

    public double getDrainMultiplier() {
        return drainMultiplier;
    }
}
