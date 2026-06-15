package com.rmh.itemmagnet.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumParsingTest {

    @Test
    void unlockTypeParsesNone() {
        assertEquals(UnlockType.NONE, UnlockType.valueOf("NONE"));
    }

  @Test
  void unlockTypeParsesLpGroup() {
    assertEquals(UnlockType.LP_GROUP, UnlockType.valueOf("LP_GROUP"));
  }

  @Test
  void unlockTypeParsesMcmmoSkill() {
    assertEquals(UnlockType.MCMMO_SKILL, UnlockType.valueOf("MCMMO_SKILL"));
  }

  @Test
  void regionModeParsesBlacklist() {
        assertEquals(RegionMode.BLACKLIST, RegionMode.valueOf("BLACKLIST"));
    }

    @Test
    void claimedLandParsesMemberOnly() {
        assertEquals(ClaimedLandPolicy.MEMBER_ONLY, ClaimedLandPolicy.valueOf("MEMBER_ONLY"));
    }

    @Test
    void holdModeParsesInventory() {
        assertEquals(HoldMode.INVENTORY, HoldMode.valueOf("INVENTORY"));
    }

    @Test
    void multiMagnetPolicyParsesBestTier() {
        assertEquals(MultiMagnetPolicy.BEST_TIER, MultiMagnetPolicy.valueOf("BEST_TIER"));
    }

    @Test
    void worldFilterAllowsWhenModeNone() {
        WorldFilterConfig filter = new WorldFilterConfig(RegionMode.NONE, java.util.List.of("spawn"));
        assertTrue(filter.isAllowed("spawn"));
        assertTrue(filter.isAllowed("world"));
    }

    @Test
    void worldFilterBlacklistBlocksListedWorld() {
        WorldFilterConfig filter = new WorldFilterConfig(RegionMode.BLACKLIST, java.util.List.of("spawn"));
        assertFalse(filter.isAllowed("spawn"));
        assertTrue(filter.isAllowed("world"));
    }
}
