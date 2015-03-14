package com.blazeloader.api.api.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class ReversibleShapelessRecipe extends ShapelessRecipe implements IReversibleRecipe {
	private boolean anyDirection = true;
	
	public ReversibleShapelessRecipe(ItemStack output, List input) {
		super(output, input);
	}
	
	public ReversibleShapelessRecipe setReverseOnly() {
		anyDirection = false;
		return this;
	}

	public boolean matches(InventoryCrafting craftingInventory, World w) {
		return anyDirection && super.matches(craftingInventory, w);
	}
	
	public boolean matchReverse(ItemStack output, int width, int height) {
		return width * height >= getRecipeSize() && ItemStack.areItemStacksEqual(output, getRecipeOutput());
	}
	
	public ItemStack[] getRecipeInput() {
		return recipeItems;
	}
}
