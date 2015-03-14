package com.blazeloader.api.api.toolset;

import com.blazeloader.api.api.recipe.ApiCrafting;
import com.blazeloader.api.obf.BLOBF;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesTools;

public class ToolRecipes {
    private static final String[][] patterns = new RecipesTools().recipePatterns;

    private final ApiCrafting.Manager manager;

    public ToolRecipes() {
        this(ApiCrafting.getVanillaCraftingManager());
    }
    
    public ToolRecipes(ApiCrafting.Manager CraftingManager) {
        manager = CraftingManager;
    }

    public void AddToolSetRecipes(Item material, Item... tools) {
        for (int i = 0; i < tools.length && i < patterns.length; i++) {
            manager.addRecipe(new ItemStack(tools[i]),
                    patterns[i], '#', Items.stick, 'X', material);
        }
    }
}
