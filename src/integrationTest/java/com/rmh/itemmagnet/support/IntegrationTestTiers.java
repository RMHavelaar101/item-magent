package com.rmh.itemmagnet.support;

import com.rmh.itemmagnet.config.RecipeConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.config.UnlockConfig;
import com.rmh.itemmagnet.config.UnlockType;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public final class IntegrationTestTiers {

    private IntegrationTestTiers() {
    }

    public static TierConfig fragmentTier() {
        return tier("fragment", UnlockType.NONE);
    }

    public static TierConfig commandGatedTier() {
        return tier("fragment", UnlockType.COMMAND);
    }

    private static TierConfig tier(String id, UnlockType unlockType) {
        return new TierConfig(
                id,
                Material.STONE,
                "Test Fragment Magnet",
                List.of("&7Test tier"),
                false,
                8.0,
                100,
                0.5,
                0.1,
                1.5,
                4.0,
                16.0,
                List.of(),
                false,
                List.of(),
                true,
                new UnlockConfig(unlockType, null, null, null, null, 0L, null, null, null),
                new RecipeConfig(false, true, List.of("XXX"), Map.of('X', Material.STONE))
        );
    }
}
