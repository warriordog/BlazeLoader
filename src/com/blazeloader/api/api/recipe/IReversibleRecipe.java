package com.blazeloader.api.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public interface IReversibleRecipe extends IRecipe {
	public boolean matchReverse(ItemStack output, int width, int height);
	
	public ItemStack[] getRecipeInput();
}
