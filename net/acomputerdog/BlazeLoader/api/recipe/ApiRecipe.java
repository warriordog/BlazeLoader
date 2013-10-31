package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MinecraftPackageAccessor;

/**
 * API function related to vanilla crafting
 */
public class ApiRecipe {
	
	/**
	 * Registers a shaped recipe.
	 * @param output An itemStack with the recipe output
	 * @param args An object array on how to form the recipe e.g. "##", "##", "##", Character.valueOf('#'), new ItemStack(Block.door)
	 */
	public static void addShapedRecipe(ItemStack output, Object... args){
		MinecraftPackageAccessor.addShapedRecipe(output, args);
	}
	
	/**
	 * Registers a shapeless recipe.
	 * @param output An itemStack with the recipe output
	 * @param args An object array of itemStacks to use
	 */
	public static void addShapelessRecipe(ItemStack output, Object... args){
		MinecraftPackageAccessor.addShapelessRecipe(output, args);
	}
	
	/**
	 * Registers a furnace recipe.
	 * @param input ID for the block/item to be smelted
	 * @param output An itemStack with the recipe output
	 * @param xp Float value with the amount of xp recieved when coocking an item/block
	 */
	public static void addSmeltingRecipe(int input, ItemStack output, float xp){
		FurnaceRecipes.smelting().addSmelting(input, output, xp);
	}

}
