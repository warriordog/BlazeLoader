package net.acomputerdog.BlazeLoader.api.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class BShapelessRecipe extends ShapelessRecipes {
	private final List recipeItems;
	
	private int craftingWidth = 3;
    private int craftingHeight = 3;
	
	public BShapelessRecipe(ItemStack output, List input) {
        super(output, input);
        recipeItems = input;
    }
	
	public void setCraftingSize(int width, int height) {
    	craftingWidth = width;
    	craftingHeight = height;
    }
	
	public boolean matches(InventoryCrafting craftingInventory, World w) {
        ArrayList workingSet = new ArrayList(recipeItems);

        for (int col = 0; col < craftingWidth; ++col) {
            for (int row = 0; row < craftingHeight; ++row) {
                ItemStack stack = craftingInventory.getStackInRowAndColumn(row, col);

                if (stack != null) {
                    boolean result = false;
                    Iterator iter = workingSet.iterator();

                    while (iter.hasNext()) {
                        ItemStack next = (ItemStack)iter.next();

                        if (stack == next && (next.getItemDamage() == 32767 || stack.getItemDamage() == next.getItemDamage())) {
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
