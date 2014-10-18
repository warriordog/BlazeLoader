package com.blazeloader.api.direct.server.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BShapelessRecipe extends ShapelessRecipes {
    private final List<ItemStack> recipeItems;

    private int craftingWidth = 3;
    private int craftingHeight = 3;

    public BShapelessRecipe(ItemStack output, List<ItemStack> input) {
        super(output, input);
        recipeItems = input;
    }

    public void setCraftingSize(int width, int height) {
        craftingWidth = width;
        craftingHeight = height;
    }

    public boolean matches(InventoryCrafting craftingInventory, World w) {
        ArrayList<ItemStack> workingSet = new ArrayList<ItemStack>(recipeItems);

        for (int col = 0; col < craftingWidth; ++col) {
            for (int row = 0; row < craftingHeight; ++row) {
                ItemStack stack = craftingInventory.getStackInRowAndColumn(row, col);

                if (stack != null) {
                    boolean result = false;

                    for (ItemStack next : workingSet) {
                        int nextDurability = next.getItemDamage();
                        if (stack == next && (nextDurability == 32767 || stack.getItemDamage() == nextDurability)) {
                            result = true;
                            workingSet.remove(next);
                            break;
                        }
                    }

                    if (!result) {
                        return false;
                    }
                }
            }
        }

        return workingSet.isEmpty();
    }
}
