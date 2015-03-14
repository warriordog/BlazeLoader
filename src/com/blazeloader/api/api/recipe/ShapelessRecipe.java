package com.blazeloader.api.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

public class ShapelessRecipe extends ShapelessRecipes {
	
	protected final ItemStack[] recipeItems;
	
	public ShapelessRecipe(ItemStack output, List input) {
		super(output, input);
		recipeItems = (ItemStack[]) input.toArray(new ItemStack[input.size()]);
	}
}
