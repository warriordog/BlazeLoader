package com.blazeloader.api.direct.server.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class BShapedRecipe extends ShapedRecipes {
    /**
     * How many horizontal slots this recipe uses.
     */
    private final int recipeWidth;

    /**
     * How many vertical slots this recipe uses.
     */
    private final int recipeHeight;

    /**
     * How many slots wide the grid is.
     */
    private int craftingWidth = 3;

    /**
     * How many slots tall the grid is.
     */
    private int craftingHeight = 3;

    /**
     * Is a array of ItemStack that composes the recipe.
     */
    protected final ItemStack[] recipeItems;

    public BShapedRecipe(int width, int height, ItemStack[] inputStacks, ItemStack output) {
        super(width, height, inputStacks, output);
        this.recipeWidth = width;
        this.recipeHeight = height;
        this.recipeItems = inputStacks;
    }

    public void setCraftingSize(int width, int height) {
        craftingWidth = width;
        craftingHeight = height;
    }

    /**
     * Used to check if a recipe matches the current crafting inventory
     */
    public boolean matches(InventoryCrafting craftingInventory, World w) {
        for (int x = 0; x <= craftingWidth - this.recipeWidth; ++x) {
            for (int y = 0; y <= craftingHeight - this.recipeHeight; ++y) {
                if (this.checkMatch(craftingInventory, x, y, true)) {
                    return true;
                }

                if (this.checkMatch(craftingInventory, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is a match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting craftingInventory, int x, int y, boolean flag) {
        for (int j = 0; j < craftingWidth; ++j) {
            for (int k = 0; k < craftingHeight; ++k) {
                int col = j - x;
                int row = k - y;
                ItemStack result = null;

                if (col >= 0 && row >= 0 && col < recipeWidth && row < recipeHeight) {
                    if (flag) {
                        result = recipeItems[this.recipeWidth - col - 1 + row * recipeWidth];
                    } else {
                        result = recipeItems[col + row * recipeWidth];
                    }
                }

                ItemStack existing = craftingInventory.getStackInRowAndColumn(j, k);

                if (existing != null || result != null) {
                    if (existing == null || result == null) {
                        return false;
                    }

                    if (result != existing) {
                        return false;
                    }

                    int resultDurability = result.getCurrentDurability();
                    if (resultDurability != 32767 && resultDurability != existing.getCurrentDurability()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
