package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.filter.PullBlockReason;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BlockedProgressRule {

    private final String id;
    private final String questId;
    private final String stat;
    private final String subStat;
    private final int amount;
    private final Set<PullBlockReason> reasons;
    private final Set<String> materials;

    public BlockedProgressRule(
            String id,
            String questId,
            String stat,
            String subStat,
            int amount,
            List<String> reasonNames,
            List<String> materialNames
    ) {
        this.id = id;
        this.questId = questId;
        this.stat = stat;
        this.subStat = subStat;
        this.amount = amount;
        this.reasons = reasonNames.stream()
                .map(name -> PullBlockReason.valueOf(name.toUpperCase(Locale.ROOT)))
                .collect(Collectors.toSet());
        this.materials = materialNames == null || materialNames.isEmpty()
                ? Set.of()
                : materialNames.stream().map(name -> name.toUpperCase(Locale.ROOT)).collect(Collectors.toSet());
    }

    public String getId() {
        return id;
    }

    public String getQuestId() {
        return questId;
    }

    public String getStat() {
        return stat;
    }

    public String getSubStat() {
        return subStat;
    }

    public int getAmount() {
        return amount;
    }

    public boolean matches(PullBlockReason reason, String materialName) {
        if (reasons.isEmpty() || !reasons.contains(reason)) {
            return false;
        }
        if (materials.isEmpty()) {
            return true;
        }
        return materialName != null && materials.contains(materialName.toUpperCase(Locale.ROOT));
    }
}
