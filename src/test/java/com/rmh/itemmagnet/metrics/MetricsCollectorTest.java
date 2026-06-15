package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.filter.PullBlockReason;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsCollectorTest {

    @Test
    void aggregatesBlockReasonCounts() {
        MetricsCollector collector = new MetricsCollector();
        collector.recordBlock(PullBlockReason.PLAYER_BLACKLIST);
        collector.recordBlock(PullBlockReason.PLAYER_BLACKLIST);
        collector.recordBlock(PullBlockReason.PROTECTION);

        assertEquals(2, collector.getBlockCount(PullBlockReason.PLAYER_BLACKLIST));
        assertEquals(1, collector.getBlockCount(PullBlockReason.PROTECTION));
        assertEquals(PullBlockReason.PLAYER_BLACKLIST, collector.getTopBlockReason());
        assertEquals("player_blacklist", collector.getTopBlockReasonName());
        assertTrue(collector.hasBlockReason(PullBlockReason.PLAYER_BLACKLIST));
        assertFalse(collector.hasBlockReason(PullBlockReason.INVENTORY_FULL));
    }
}
