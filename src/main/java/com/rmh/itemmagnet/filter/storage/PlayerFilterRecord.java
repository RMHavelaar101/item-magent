package com.rmh.itemmagnet.filter.storage;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerFilterRecord {

    private final UUID uuid;
    private final Set<Material> materials;
    private final Set<String> tags;
    private final boolean hintShown;

    public PlayerFilterRecord(UUID uuid, Set<Material> materials, Set<String> tags, boolean hintShown) {
        this.uuid = uuid;
        this.materials = Collections.unmodifiableSet(new HashSet<>(materials));
        this.tags = Collections.unmodifiableSet(new HashSet<>(tags));
        this.hintShown = hintShown;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean isHintShown() {
        return hintShown;
    }
}
