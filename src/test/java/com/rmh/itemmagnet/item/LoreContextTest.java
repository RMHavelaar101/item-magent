package com.rmh.itemmagnet.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoreContextTest {

    @Test
    void formatsRadiusToOneDecimal() {
        LoreContext context = new LoreContext(9.25, 6.0);
        assertEquals("9.3", context.formatRadius());
        assertEquals("6.0", context.formatBaseRadius());
    }

    @Test
    void baseOnlyUsesSameValue() {
        LoreContext context = LoreContext.baseOnly(6.0);
        assertEquals("6.0", context.formatRadius());
        assertEquals("6.0", context.formatBaseRadius());
    }
}
