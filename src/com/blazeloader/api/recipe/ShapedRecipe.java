package com.blazeloader.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class ShapedRecipe extends ShapedRecipes {
	
	private int recipeWidth, recipeHeight;
	protected ItemStack[] recipeItems;
	
	public ShapedRecipe(int width, int height, ItemStack[] input, ItemStack output) {
		super(width, height, input, output);
		recipeWidth = width;
		recipeHeight = height;
		recipeItems = input;
	}
	
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inventory, World worldIn) {
        for (int x = 0; x <= inventory.getWidth() - recipeWidth; x++) {
            for (int y = 0; y <= inventory.getHeight() - recipeHeight; y++) {
                if (checkMatch(inventory, x, y, true)) return true;
                if (checkMatch(inventory, x, y, false)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting inventory, int x, int y, boolean flag) {
        for (int X = 0; X < inventory.getWidth(); X++) {
            for (int Y = 0; Y < inventory.getHeight(); Y++) {
                int var7 = X - x;
                int var8 = Y - y;
                ItemStack var9 = null;

                if (var7 >= 0 && var8 >= 0 && var7 < recipeWidth && var8 < recipeHeight)
                {
                    if (flag) {
                        var9 = recipeItems[recipeWidth - var7 - 1 + var8 * recipeWidth];
                    } else {
                        var9 = recipeItems[var7 + var8 * recipeWidth];
                    }
                }
            	
                ItemStack stack = inventory.getStackInRowAndColumn(X, Y);
                if (stack != null || var9 != null) {
                    if (stack == null || var9 == null) return false;
                    if (var9.getItem() != stack.getItem()) return false;
                    if (var9.getMetadata() != 32767 && var9.getMetadata() != stack.getMetadata()) return false;
                }
            }
        }

        return true;
    }
}
