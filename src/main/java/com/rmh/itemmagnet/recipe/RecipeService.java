package com.rmh.itemmagnet.recipe;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.RecipeConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetItemService;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public final class RecipeService {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final Map<String, NamespacedKey> recipeKeys = new HashMap<>();

    public RecipeService(ItemMagnetPlugin plugin, MagnetItemService itemService) {
        this.plugin = plugin;
        this.itemService = itemService;
    }

    public void registerRecipes() {
        unregisterRecipes();
        for (TierConfig tier : plugin.getConfigManager().getMagnetConfig().getTiers().values()) {
            RecipeConfig recipe = tier.getRecipe();
            if (!recipe.isEnabled() || recipe.getShape().isEmpty()) {
                continue;
            }
            NamespacedKey key = new NamespacedKey(plugin, tier.getId());
            recipeKeys.put(tier.getId(), key);

            ItemStack result = itemService.create(tier, 0);
            ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
            shapedRecipe.shape(recipe.getShape().toArray(new String[0]));
            for (Map.Entry<Character, org.bukkit.Material> entry : recipe.getIngredients().entrySet()) {
                shapedRecipe.setIngredient(entry.getKey(), entry.getValue());
            }
            Bukkit.addRecipe(shapedRecipe);
        }
    }

    public void unregisterRecipes() {
        for (NamespacedKey key : recipeKeys.values()) {
            Bukkit.removeRecipe(key);
        }
        recipeKeys.clear();
    }
}
