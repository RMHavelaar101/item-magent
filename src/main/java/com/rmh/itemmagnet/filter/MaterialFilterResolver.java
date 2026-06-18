package com.rmh.itemmagnet.filter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class MaterialFilterResolver {

    private static final Map<String, String> MATERIAL_ALIASES = Map.of(
            "DEEPSLATE_COBBLESTONE", "COBBLED_DEEPSLATE"
    );

    private MaterialFilterResolver() {
    }

    public static MaterialFilterRule resolve(List<String> materialNames, List<String> tagNames, Logger logger) {
        List<Material> materials = new ArrayList<>();
        if (materialNames != null) {
            for (String entry : materialNames) {
                Material material = matchMaterial(entry);
                if (material != null) {
                    materials.add(material);
                } else if (logger != null) {
                    logger.warning("Unknown material in filter list: " + entry);
                }
            }
        }

        List<String> tags = new ArrayList<>();
        Set<Material> expanded = new HashSet<>(materials);
        if (tagNames != null) {
            for (String tagName : tagNames) {
                String normalized = normalizeTagName(tagName);
                tags.add(normalized);
                Tag<Material> tag = resolveTag(normalized);
                if (tag == null) {
                    if (logger != null) {
                        logger.warning("Unknown material tag in filter list: " + tagName);
                    }
                    continue;
                }
                expanded.addAll(tag.getValues());
            }
        }

        return new MaterialFilterRule(materials, tags, expanded);
    }

    public static boolean isBlocked(MaterialFilterRule rule, Material material) {
        return rule != null && rule.blocks(material);
    }

    public static boolean isBlockedByTags(List<String> tagNames, Material material) {
        if (tagNames == null || tagNames.isEmpty()) {
            return false;
        }
        for (String tagName : tagNames) {
            Tag<Material> tag = resolveTag(normalizeTagName(tagName));
            if (tag != null && tag.isTagged(material)) {
                return true;
            }
        }
        return false;
    }

    private static Tag<Material> resolveTag(String tagName) {
        NamespacedKey key = NamespacedKey.fromString(tagName);
        if (key == null) {
            return null;
        }
        try {
            return Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
        } catch (IllegalStateException | NullPointerException exception) {
            return null;
        }
    }

    private static String normalizeTagName(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return tagName;
        }
        if (tagName.contains(":")) {
            return tagName.toLowerCase(Locale.ROOT);
        }
        return "minecraft:" + tagName.toLowerCase(Locale.ROOT);
    }

    private static Material matchMaterial(String entry) {
        if (entry == null || entry.isBlank()) {
            return null;
        }
        String normalized = entry.toUpperCase(Locale.ROOT);
        Material material = Material.matchMaterial(normalized);
        if (material != null) {
            return material;
        }
        String alias = MATERIAL_ALIASES.get(normalized);
        if (alias == null) {
            return null;
        }
        return Material.matchMaterial(alias);
    }
}
