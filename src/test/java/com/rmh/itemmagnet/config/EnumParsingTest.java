package com.rmh.itemmagnet.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumParsingTest {

    @Test
    void unlockTypeParsesNone() {
        assertEquals(UnlockType.NONE, UnlockType.valueOf("NONE"));
    }

    @Test
    void regionModeParsesBlacklist() {
        assertEquals(RegionMode.BLACKLIST, RegionMode.valueOf("BLACKLIST"));
    }

    @Test
    void claimedLandParsesMemberOnly() {
        assertEquals(ClaimedLandPolicy.MEMBER_ONLY, ClaimedLandPolicy.valueOf("MEMBER_ONLY"));
    }
}
