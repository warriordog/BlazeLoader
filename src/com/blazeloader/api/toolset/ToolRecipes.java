package com.blazeloader.api.toolset;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesTools;

import com.blazeloader.api.recipe.ApiCrafting;
import com.blazeloader.api.recipe.ApiCrafting.BLCraftingManager;

public class ToolRecipes {
    private static final String[][] patterns = new RecipesTools().recipePatterns;
    
    private final BLCraftingManager manager;
	
    private Item stickMaterial = Items.stick;
    
    public ToolRecipes() {
        this(ApiCrafting.getVanillaCraftingManager());
    }

    public ToolRecipes(BLCraftingManager CraftingManager) {
        manager = CraftingManager;
        AddToolSetRecipes(Items.stick, Items.stick);
    }

    public void AddToolSetRecipes(Item toolMaterial, Item... tools) {
        for (int i = 0; i < tools.length && i < patterns.length; i++) {
            manager.addRecipe(new ItemStack(tools[i]), patterns[i], '#', stickMaterial, 'X', toolMaterial);
        }
    }
    
    public ToolRecipes setStick(Item stick) {
    	stickMaterial = stick;
    	return this;
    }
}
