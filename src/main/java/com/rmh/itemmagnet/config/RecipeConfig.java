package com.rmh.itemmagnet.config;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RecipeConfig {

    private final boolean enabled;
    private final boolean hidden;
    private final List<String> shape;
    private final Map<Character, Material> ingredients;

    public RecipeConfig(boolean enabled, boolean hidden, List<String> shape, Map<Character, Material> ingredients) {
        this.enabled = enabled;
        this.hidden = hidden;
        this.shape = Collections.unmodifiableList(shape);
        this.ingredients = Collections.unmodifiableMap(ingredients);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public List<String> getShape() {
        return shape;
    }

    public Map<Character, Material> getIngredients() {
        return ingredients;
    }
}
