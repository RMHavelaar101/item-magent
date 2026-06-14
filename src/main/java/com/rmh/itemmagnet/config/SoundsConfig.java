package com.rmh.itemmagnet.config;

import org.bukkit.Sound;

import java.util.Optional;

public final class SoundsConfig {

    private final boolean enabled;
    private final Optional<Sound> pull;
    private final Optional<Sound> fuel;
    private final Optional<Sound> depleted;
    private final Optional<Sound> denied;

    public SoundsConfig(
            boolean enabled,
            Optional<Sound> pull,
            Optional<Sound> fuel,
            Optional<Sound> depleted,
            Optional<Sound> denied
    ) {
        this.enabled = enabled;
        this.pull = pull;
        this.fuel = fuel;
        this.depleted = depleted;
        this.denied = denied;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Optional<Sound> getPull() {
        return pull;
    }

    public Optional<Sound> getFuel() {
        return fuel;
    }

    public Optional<Sound> getDepleted() {
        return depleted;
    }

    public Optional<Sound> getDenied() {
        return denied;
    }
}
