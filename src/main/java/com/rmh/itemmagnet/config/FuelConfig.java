package com.rmh.itemmagnet.config;

import org.bukkit.Sound;

import java.util.Optional;

public final class FuelConfig {

    private final int chargePerItem;
    private final int radiusBonus;
    private final int boostLevelAdd;
    private final int boostDurationSeconds;
    private final Optional<Sound> sound;

    public FuelConfig(
            int chargePerItem,
            int radiusBonus,
            int boostLevelAdd,
            int boostDurationSeconds,
            Optional<Sound> sound
    ) {
        this.chargePerItem = chargePerItem;
        this.radiusBonus = radiusBonus;
        this.boostLevelAdd = boostLevelAdd;
        this.boostDurationSeconds = boostDurationSeconds;
        this.sound = sound;
    }

    public int getChargePerItem() {
        return chargePerItem;
    }

    public int getRadiusBonus() {
        return radiusBonus;
    }

    public int getBoostLevelAdd() {
        return boostLevelAdd;
    }

    public int getBoostDurationSeconds() {
        return boostDurationSeconds;
    }

    public Optional<Sound> getSound() {
        return sound;
    }
}
