package com.rmh.itemmagnet.item;

import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MagnetData {

    private final TierConfig tier;
    private int charge;
    private int boostLevel;
    private long boostExpiryTick;

    public MagnetData(TierConfig tier, int charge, int boostLevel, long boostExpiryTick) {
        this.tier = tier;
        this.charge = charge;
        this.boostLevel = boostLevel;
        this.boostExpiryTick = boostExpiryTick;
    }

    public TierConfig getTier() {
        return tier;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getBoostLevel() {
        return boostLevel;
    }

    public void setBoostLevel(int boostLevel) {
        this.boostLevel = boostLevel;
    }

    public long getBoostExpiryTick() {
        return boostExpiryTick;
    }

    public void setBoostExpiryTick(long boostExpiryTick) {
        this.boostExpiryTick = boostExpiryTick;
    }

    public boolean isBoostActive(long currentTick) {
        return boostLevel > 0 && boostExpiryTick > currentTick;
    }
}
