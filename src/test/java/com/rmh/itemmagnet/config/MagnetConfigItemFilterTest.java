package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.filter.MaterialFilterResolver;
import com.rmh.itemmagnet.filter.MaterialFilterRule;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MagnetConfigItemFilterTest {

    @Test
    void tierBlacklistBlocksMaterial() {
        MaterialFilterRule blacklist = MaterialFilterResolver.resolve(
                java.util.List.of("COBBLESTONE"),
                java.util.List.of(),
                null
        );
        TierConfig tier = new TierConfig(
                "fragment",
                Material.FLINT_AND_STEEL,
                "&6Fragment",
                java.util.List.of(),
                true,
                0,
                6,
                1000,
                1,
                2,
                1.15,
                1,
                16,
                blacklist,
                false,
                MaterialFilterRule.empty(),
                true,
                new UnlockConfig(UnlockType.NONE, null, null, null, null, 0, null, null, null),
                new RecipeConfig(false, false, java.util.List.of(), java.util.Map.of())
        );

        assertFalse(tier.canPullMaterial(Material.COBBLESTONE));
        assertTrue(tier.canPullMaterial(Material.DIAMOND));
    }

    @Test
    void serverBlacklistRuleBlocksMaterial() {
        MaterialFilterRule serverRule = MaterialFilterResolver.resolve(
                java.util.List.of("BEDROCK"),
                java.util.List.of(),
                null
        );
        assertTrue(serverRule.blocks(Material.BEDROCK));
        assertFalse(serverRule.blocks(Material.DIAMOND));
    }
}
