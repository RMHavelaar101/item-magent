package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.filter.PullBlockReason;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockedProgressRuleTest {

    @Test
    void matchesReasonAndMaterial() {
        BlockedProgressRule rule = new BlockedProgressRule(
                "filtered_iron",
                "iron_collector",
                null,
                null,
                1,
                List.of("PLAYER_BLACKLIST"),
                List.of("IRON_ORE", "DEEPSLATE_IRON_ORE")
        );

        assertTrue(rule.matches(PullBlockReason.PLAYER_BLACKLIST, "IRON_ORE"));
        assertFalse(rule.matches(PullBlockReason.TIER_BLACKLIST, "IRON_ORE"));
        assertFalse(rule.matches(PullBlockReason.PLAYER_BLACKLIST, "GOLD_ORE"));
    }

    @Test
    void emptyMaterialsMatchesAnyMaterial() {
        BlockedProgressRule rule = new BlockedProgressRule(
                "any_blocked",
                null,
                "blocksmined",
                "unknown",
                1,
                List.of("PLAYER_BLACKLIST", "TIER_BLACKLIST"),
                List.of()
        );

        assertTrue(rule.matches(PullBlockReason.TIER_BLACKLIST, "STONE"));
        assertFalse(rule.matches(PullBlockReason.PROTECTION, "STONE"));
    }
}
