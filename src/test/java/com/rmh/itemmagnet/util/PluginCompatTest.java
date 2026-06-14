package com.rmh.itemmagnet.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginCompatTest {

    @Test
    void normalizeVersionStripsLeadingV() {
        assertEquals("1.1.0", PluginCompat.normalizeVersion("v1.1.0"));
        assertEquals("1.0.0", PluginCompat.normalizeVersion("1.0.0"));
    }

    @Test
    void isNewerVersionTreatsEquivalentTagsAsSame() {
        assertFalse(PluginCompat.isNewerVersion("v1.1.0", "1.1.0"));
        assertTrue(PluginCompat.isNewerVersion("v1.2.0", "1.1.0"));
    }
}
