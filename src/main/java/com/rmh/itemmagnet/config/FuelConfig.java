package com.rmh.itemmagnet.config;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class FuelConfig {

    private final int chargePerItem;
    private final int radiusBonus;
    private final int boostLevelAdd;
    private final int boostDurationSeconds;

    public FuelConfig(int chargePerItem, int radiusBonus, int boostLevelAdd, int boostDurationSeconds) {
        this.chargePerItem = chargePerItem;
        this.radiusBonus = radiusBonus;
        this.boostLevelAdd = boostLevelAdd;
        this.boostDurationSeconds = boostDurationSeconds;
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
}
