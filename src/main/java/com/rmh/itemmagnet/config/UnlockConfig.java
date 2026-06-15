package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.List;

public final class UnlockConfig {

    private final UnlockType type;
    private final String permission;
    private final String advancement;
    private final String stat;
    private final String sub;
    private final long amount;
    private final String rank;
    private final String group;
    private final String skill;

    public UnlockConfig(
            UnlockType type,
            String permission,
            String advancement,
            String stat,
            String sub,
            long amount,
            String rank,
            String group,
            String skill
    ) {
        this.type = type;
        this.permission = permission;
        this.advancement = advancement;
        this.stat = stat;
        this.sub = sub;
        this.amount = amount;
        this.rank = rank;
        this.group = group;
        this.skill = skill;
    }

    public UnlockType getType() {
        return type;
    }

    public String getPermission() {
        return permission;
    }

    public String getAdvancement() {
        return advancement;
    }

    public String getStat() {
        return stat;
    }

    public String getSub() {
        return sub;
    }

    public long getAmount() {
        return amount;
    }

    public String getRank() {
        return rank;
    }

    public String getGroup() {
        return group;
    }

    public String getSkill() {
        return skill;
    }
}
