package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ReversibleShapedRecipe extends BShapedRecipe {
	private boolean anyDirection = true;
	
	public ReversibleShapedRecipe(int width, int height, ItemStack[] input, ItemStack output) {
    	super(width, height, input, output);
    }
	
	public void setReverseOnly() {
		anyDirection = false;
	}
	
	public boolean matches(InventoryCrafting craftingInventory, World w) {
        return anyDirection && super.matches(craftingInventory, w);
    }
	
	public ItemStack[] getRecipeInput() {
		return this.recipeItems.clone();
	}
}
