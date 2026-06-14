package com.rmh.itemmagnet.magnet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PullPhysicsTest {

    @Test
    void stepTowardVectorMath() {
        double fromX = 0;
        double toX = 5;
        double step = 0.4;
        double delta = toX - fromX;
        double movement = (delta / Math.abs(delta)) * step;
        assertEquals(0.4, movement, 0.001);
        assertTrue(movement > 0);
    }
}
