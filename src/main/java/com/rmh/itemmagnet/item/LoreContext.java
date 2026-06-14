package com.rmh.itemmagnet.item;

import java.util.Locale;

public final class LoreContext {

    private final double effectiveRadius;
    private final double baseRadius;

    public LoreContext(double effectiveRadius, double baseRadius) {
        this.effectiveRadius = effectiveRadius;
        this.baseRadius = baseRadius;
    }

    public static LoreContext baseOnly(double baseRadius) {
        return new LoreContext(baseRadius, baseRadius);
    }

    public double getEffectiveRadius() {
        return effectiveRadius;
    }

    public double getBaseRadius() {
        return baseRadius;
    }

    public String formatRadius() {
        return String.format(Locale.ROOT, "%.1f", effectiveRadius);
    }

    public String formatBaseRadius() {
        return String.format(Locale.ROOT, "%.1f", baseRadius);
    }
}
