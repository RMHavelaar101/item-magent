package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.filter.MaterialFilterRule;
import com.rmh.itemmagnet.filter.PullBlockReason;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class TierConfig {

    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final boolean enchantGlint;
    private final int customModelData;
    private final double radius;
    private final int maxCharge;
    private final double baseDrainPerSecond;
    private final double extraDrainPerItem;
    private final double boostDrainMultiplier;
    private final double minRadius;
    private final double maxRadius;
    private final MaterialFilterRule blacklistRule;
    private final boolean whitelistEnabled;
    private final MaterialFilterRule whitelistRule;
    private final boolean pullExperience;
    private final UnlockConfig unlock;
    private final RecipeConfig recipe;

    public TierConfig(
            String id,
            Material material,
            String displayName,
            List<String> lore,
            boolean enchantGlint,
            int customModelData,
            double radius,
            int maxCharge,
            double baseDrainPerSecond,
            double extraDrainPerItem,
            double boostDrainMultiplier,
            double minRadius,
            double maxRadius,
            MaterialFilterRule blacklistRule,
            boolean whitelistEnabled,
            MaterialFilterRule whitelistRule,
            boolean pullExperience,
            UnlockConfig unlock,
            RecipeConfig recipe
    ) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = Collections.unmodifiableList(lore);
        this.enchantGlint = enchantGlint;
        this.customModelData = customModelData;
        this.radius = radius;
        this.maxCharge = maxCharge;
        this.baseDrainPerSecond = baseDrainPerSecond;
        this.extraDrainPerItem = extraDrainPerItem;
        this.boostDrainMultiplier = boostDrainMultiplier;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.blacklistRule = blacklistRule;
        this.whitelistEnabled = whitelistEnabled;
        this.whitelistRule = whitelistRule;
        this.pullExperience = pullExperience;
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

    public int getCustomModelData() {
        return customModelData;
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

    public MaterialFilterRule getBlacklistRule() {
        return blacklistRule;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public MaterialFilterRule getWhitelistRule() {
        return whitelistRule;
    }

    public boolean isPullExperience() {
        return pullExperience;
    }

    public boolean canPullMaterial(Material material) {
        return getBlockReason(material).isEmpty();
    }

    public Optional<PullBlockReason> getBlockReason(Material material) {
        if (blacklistRule.blocks(material)) {
            return Optional.of(PullBlockReason.TIER_BLACKLIST);
        }
        if (whitelistEnabled && !whitelistRule.getExpandedMaterials().isEmpty()
                && !whitelistRule.getExpandedMaterials().contains(material)) {
            return Optional.of(PullBlockReason.TIER_WHITELIST);
        }
        return Optional.empty();
    }

    public UnlockConfig getUnlock() {
        return unlock;
    }

    public RecipeConfig getRecipe() {
        return recipe;
    }
}
