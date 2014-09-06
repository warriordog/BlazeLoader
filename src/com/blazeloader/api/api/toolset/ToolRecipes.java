package com.blazeloader.api.api.toolset;

import com.blazeloader.api.api.recipe.BLCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesTools;

public class ToolRecipes {
    private static final String[][] getRecipePatterns() {
        return (String[][]) ReflectionUtils.getField(RecipesTools.class, new RecipesTools(), 0).get();
    }

    private final BLCraftingManager manager;

    public ToolRecipes(BLCraftingManager CraftingManager) {
        manager = CraftingManager;
    }

    public void AddToolSetRecipes(Item material, Item... tools) {
        AddRecipes(material, tools, false);
    }

    public void AddVanillaToolSetRecipes(Item material, Item... tools) {
        AddRecipes(material, tools, true);
    }

    private void AddRecipes(Item material, Item[] tools, boolean vanilla) {
        String[][] patterns = getRecipePatterns();
        BLCraftingManager man = vanilla ? getVanillaManager() : manager;

        for (int i = 0; i < tools.length && i < patterns.length; i++) {
            man.addRecipe(new ItemStack(tools[i]),
                    patterns[i], '#', Items.stick, 'X', material);
        }
    }

    private BLCraftingManager getVanillaManager() {
        return BLCraftingManager.getInstance(BLCraftingManager.VanillaCraftingManagerKey);
    }
}
