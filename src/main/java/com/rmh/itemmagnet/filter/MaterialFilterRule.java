package com.rmh.itemmagnet.filter;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MaterialFilterRule {

    private final List<Material> materials;
    private final List<String> tags;
    private final Set<Material> expandedMaterials;

    public MaterialFilterRule(List<Material> materials, List<String> tags, Set<Material> expandedMaterials) {
        this.materials = Collections.unmodifiableList(materials);
        this.tags = Collections.unmodifiableList(tags);
        this.expandedMaterials = Collections.unmodifiableSet(expandedMaterials);
    }

    public static MaterialFilterRule empty() {
        return new MaterialFilterRule(List.of(), List.of(), Set.of());
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<String> getTags() {
        return tags;
    }

    public Set<Material> getExpandedMaterials() {
        return expandedMaterials;
    }

    public int getRuleCount() {
        return materials.size() + tags.size();
    }

    public boolean blocks(Material material) {
        return expandedMaterials.contains(material);
    }

    public MaterialFilterRule merged(MaterialFilterRule other) {
        Set<Material> mergedMaterials = new HashSet<>(materials);
        mergedMaterials.addAll(other.materials);
        Set<String> mergedTags = new HashSet<>(tags);
        mergedTags.addAll(other.tags);
        Set<Material> mergedExpanded = new HashSet<>(expandedMaterials);
        mergedExpanded.addAll(other.expandedMaterials);
        return new MaterialFilterRule(
                List.copyOf(mergedMaterials),
                List.copyOf(mergedTags),
                mergedExpanded
        );
    }
}
