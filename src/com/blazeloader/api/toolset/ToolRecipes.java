package com.blazeloader.api.toolset;

import com.blazeloader.api.recipe.ApiCrafting;
import com.blazeloader.api.recipe.ApiCrafting.BLCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesTools;

public class ToolRecipes {
    private static final String[][] patterns = new RecipesTools().recipePatterns;

    private final BLCraftingManager manager;

    public ToolRecipes() {
        this(ApiCrafting.getVanillaCraftingManager());
    }

    public ToolRecipes(BLCraftingManager CraftingManager) {
        manager = CraftingManager;
    }

    public void AddToolSetRecipes(Item material, Item... tools) {
        for (int i = 0; i < tools.length && i < patterns.length; i++) {
            manager.addRecipe(new ItemStack(tools[i]),
                    patterns[i], '#', Items.stick, 'X', material);
        }
    }
}
