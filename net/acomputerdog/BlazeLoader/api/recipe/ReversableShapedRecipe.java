package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ReversableShapedRecipe extends BShapedRecipe {
	private boolean anyDirection = true;
	
	public ReversableShapedRecipe(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack) {
	    	super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
	}
	
	public void setReverseOnly() {
		anyDirection = false;
	}
	
	public boolean matches(InventoryCrafting grid, World w) {
		if (anyDirection) {
			return super.matches(grid, w);
		}
		return false;
	}
	
	public ItemStack[] getRecipeInput() {
		return this.recipeItems.clone();
	}
}
