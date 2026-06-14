package com.rmh.itemmagnet.config;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public final class TierConfig {

    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final boolean enchantGlint;
    private final double radius;
    private final int maxCharge;
    private final double baseDrainPerSecond;
    private final double extraDrainPerItem;
    private final double boostDrainMultiplier;
    private final double minRadius;
    private final double maxRadius;
    private final List<Material> blacklist;
    private final UnlockConfig unlock;
    private final RecipeConfig recipe;

    public TierConfig(
            String id,
            Material material,
            String displayName,
            List<String> lore,
            boolean enchantGlint,
            double radius,
            int maxCharge,
            double baseDrainPerSecond,
            double extraDrainPerItem,
            double boostDrainMultiplier,
            double minRadius,
            double maxRadius,
            List<Material> blacklist,
            UnlockConfig unlock,
            RecipeConfig recipe
    ) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = Collections.unmodifiableList(lore);
        this.enchantGlint = enchantGlint;
        this.radius = radius;
        this.maxCharge = maxCharge;
        this.baseDrainPerSecond = baseDrainPerSecond;
        this.extraDrainPerItem = extraDrainPerItem;
        this.boostDrainMultiplier = boostDrainMultiplier;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.blacklist = Collections.unmodifiableList(blacklist);
        this.unlock = unlock;
        this.recipe = recipe;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isEnchantGlint() {
        return enchantGlint;
    }

    public double getRadius() {
        return radius;
    }

    public int getMaxCharge() {
        return maxCharge;
    }

    public double getBaseDrainPerSecond() {
        return baseDrainPerSecond;
    }

    public double getExtraDrainPerItem() {
        return extraDrainPerItem;
    }

    public double getBoostDrainMultiplier() {
        return boostDrainMultiplier;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public double getMaxRadius() {
        return maxRadius;
    }

    public List<Material> getBlacklist() {
        return blacklist;
    }

    public UnlockConfig getUnlock() {
        return unlock;
    }

    public RecipeConfig getRecipe() {
        return recipe;
    }
}
