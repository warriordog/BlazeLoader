package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * API function related to vanilla crafting
 */
public class ApiRecipe {

    /**
     * Registers a shaped recipe.
     *
     * @param output An itemStack with the recipe output
     * @param args   An object array on how to form the recipe e.g. "##", "##", "##", Character.valueOf('#'), new ItemStack(Block.door)
     */
    public static void addCraftingRecipeShaped(ItemStack output, Object... args) {
        CraftingManager.getInstance().addRecipe(output, args);
    }

    /**
     * Registers a shapeless recipe.
     *
     * @param output An itemStack with the recipe output
     * @param args   An object array of itemStacks to use
     */
    public static void addCraftingRecipeShapeless(ItemStack output, Object... args) {
    	CraftingManager.getInstance().addShapelessRecipe(output, args);
    }

    /**
     * Registers a furnace recipe.
     *
     * @param block  Block type to be smelted
     * @param output An itemStack with the recipe output
     * @param xp     Float value with the amount of xp recieved when coocking an item/block
     */
    public static void addSmeltingRecipe(Block block, ItemStack output, float xp) {
        FurnaceRecipes.smelting().func_151393_a(block, output, xp);
    }

    /**
     * Registers a furnace recipe.
     *
     * @param item   item to be smelted
     * @param output An itemStack with the recipe output
     * @param xp     Float value with the amount of xp recieved when coocking an item/block
     */
    public static void addSmeltingRecipe(Item item, ItemStack output, float xp) {
        FurnaceRecipes.smelting().func_151396_a(item, output, xp);
    }

    /**
     * Registers a furnace recipe.
     *
     * @param itemstack itemStack to be smelted
     * @param output    An itemStack with the recipe output
     * @param xp        Float value with the amount of xp recieved when coocking an item/block
     */
    public static void addSmeltingRecipe(ItemStack itemstack, ItemStack output, float xp) {
        FurnaceRecipes.smelting().func_151394_a(itemstack, output, xp);
    }
}
