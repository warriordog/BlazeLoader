package com.blazeloader.api.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class ReversibleShapedRecipe extends ShapedRecipe implements IReversibleRecipe {
	private boolean anyDirection = true;
	
	public ReversibleShapedRecipe(int width, int height, ItemStack[] input, ItemStack output) {
		super(width, height, input, output);
	}

	public ReversibleShapedRecipe setReverseOnly() {
		anyDirection = false;
		return this;
	}

	public boolean matches(InventoryCrafting craftingInventory, World w) {
		return anyDirection && super.matches(craftingInventory, w);
	}
	
	public boolean matchReverse(ItemStack output, int width, int height) {
		return ItemStack.areItemStacksEqual(output, getRecipeOutput());
	}
	
	public ItemStack[] getRecipeInput() {
		return recipeItems;
	}
}