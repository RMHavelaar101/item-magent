package com.rmh.itemmagnet.filter;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialFilterResolverTest {

    @Test
    void resolvesExplicitMaterials() {
        MaterialFilterRule rule = MaterialFilterResolver.resolve(
                List.of("COBBLESTONE", "DIRT"),
                List.of(),
                Logger.getLogger("test")
        );
        assertTrue(rule.blocks(Material.COBBLESTONE));
        assertTrue(rule.blocks(Material.DIRT));
        assertFalse(rule.blocks(Material.DIAMOND));
    }

    @Test
    void skipsInvalidTagGracefully() {
        MaterialFilterRule rule = MaterialFilterResolver.resolve(
                List.of(),
                List.of("minecraft:not_a_real_tag"),
                Logger.getLogger("test")
        );
        assertFalse(rule.blocks(Material.DIAMOND));
        assertTrue(rule.getTags().contains("minecraft:not_a_real_tag"));
    }
}
