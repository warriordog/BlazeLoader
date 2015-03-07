package com.blazeloader.api.api.toolset;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.RecipesTools;

public class ToolRecipes {
    private static final String[][] patterns = new RecipesTools().recipePatterns;

    private final CraftingManager manager;

    public ToolRecipes(CraftingManager CraftingManager) {
        manager = CraftingManager;
    }

    public void AddToolSetRecipes(Item material, Item... tools) {
        AddRecipes(material, tools, false);
    }

    public void AddVanillaToolSetRecipes(Item material, Item... tools) {
        AddRecipes(material, tools, true);
    }

    private void AddRecipes(Item material, Item[] tools, boolean vanilla) {
        for (int i = 0; i < tools.length && i < patterns.length; i++) {
            manager.addRecipe(new ItemStack(tools[i]),
                    patterns[i], '#', Items.stick, 'X', material);
        }
    }
}
