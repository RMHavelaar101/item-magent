package com.rmh.itemmagnet.config;

public final class AntiAfkConfig {

    private final boolean enabled;
    private final double requiredBlocksMoved;
    private final int windowSeconds;
    private final boolean disableAutoFuelWhenAfk;
    private final boolean notifyOnce;

    public AntiAfkConfig(
            boolean enabled,
            double requiredBlocksMoved,
            int windowSeconds,
            boolean disableAutoFuelWhenAfk,
            boolean notifyOnce
    ) {
        this.enabled = enabled;
        this.requiredBlocksMoved = requiredBlocksMoved;
        this.windowSeconds = windowSeconds;
        this.disableAutoFuelWhenAfk = disableAutoFuelWhenAfk;
        this.notifyOnce = notifyOnce;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getRequiredBlocksMoved() {
        return requiredBlocksMoved;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }

    public boolean isDisableAutoFuelWhenAfk() {
        return disableAutoFuelWhenAfk;
    }

    public boolean isNotifyOnce() {
        return notifyOnce;
    }
}
