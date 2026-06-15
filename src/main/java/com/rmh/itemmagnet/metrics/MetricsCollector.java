package com.rmh.itemmagnet.metrics;

import com.rmh.itemmagnet.filter.PullBlockReason;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class MetricsCollector {

    private final EnumMap<PullBlockReason, AtomicLong> blockCounts = new EnumMap<>(PullBlockReason.class);

    public MetricsCollector() {
        for (PullBlockReason reason : PullBlockReason.values()) {
            blockCounts.put(reason, new AtomicLong(0));
        }
    }

    public void recordBlock(PullBlockReason reason) {
        if (reason == null) {
            return;
        }
        blockCounts.computeIfAbsent(reason, ignored -> new AtomicLong(0)).incrementAndGet();
    }

    public long getBlockCount(PullBlockReason reason) {
        AtomicLong counter = blockCounts.get(reason);
        return counter == null ? 0 : counter.get();
    }

    public PullBlockReason getTopBlockReason() {
        PullBlockReason top = null;
        long topCount = 0;
        for (Map.Entry<PullBlockReason, AtomicLong> entry : blockCounts.entrySet()) {
            long count = entry.getValue().get();
            if (count > topCount) {
                topCount = count;
                top = entry.getKey();
            }
        }
        return topCount == 0 ? null : top;
    }

    public String getTopBlockReasonName() {
        PullBlockReason top = getTopBlockReason();
        return top == null ? "none" : top.name().toLowerCase(Locale.ROOT);
    }

    public boolean hasBlockReason(PullBlockReason reason) {
        return getBlockCount(reason) > 0;
    }
}
