package net.acomputerdog.BlazeLoader.api.recipe;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;
import net.minecraft.src.ShapelessRecipes;

/**
 * API function related to vanilla crafting
 */
public class ApiRecipe {
	private static CraftingManager craftingManager = CraftingManager.getInstance();
	private static FurnaceRecipes smeltingManager = FurnaceRecipes.smelting();
	
	/**
	 * registers a shaped recipe.
	 * @param output An itemStack with the recipe output
	 * @param args An object array on how to form the recipe e.g. "##", "##", "##", Character.valueOf('#'), new ItemStack(Block.door)
	 */
	public static void addShapedRecipe(ItemStack output, Object... args)
	{
		craftingManager.getRecipeList().add(createShapedRecipe(output, args));
	}
	
	/**
	 * registers a shapeless recipe.
	 * @param output An itemStack with the recipe output
	 * @param args An object array of itemStacks to use
	 */
	public static void addShapelessRecipe(ItemStack output, Object... args)
	{
		craftingManager.getRecipeList().add(createShapelessRecipe(output, args));
	}
	
	/**
	 * 
	 * @param input ID for the block/item to be smelted
	 * @param output An itemStack with the recipe output
	 * @param xp Float value with the amount of xp recieved when coocking an item/block
	 */
	public static void addSmeltingRecipe(int input, ItemStack output, float xp)
	{
		smeltingManager.addSmelting(input, output, xp);
	}
	
	@Deprecated //workaroud for the private methods in Minecraft code, will change when we have ASM (copied from Minecraft code)
	private static ShapedRecipes createShapedRecipe(ItemStack output, Object... args)
	{
		 String var3 = "";
	        int var4 = 0;
	        int var5 = 0;
	        int var6 = 0;

	        if (args[var4] instanceof String[])
	        {
	            String[] var7 = (String[])((String[])args[var4++]);

	            for (int var8 = 0; var8 < var7.length; ++var8)
	            {
	                String var9 = var7[var8];
	                ++var6;
	                var5 = var9.length();
	                var3 = var3 + var9;
	            }
	        }
	        else
	        {
	            while (args[var4] instanceof String)
	            {
	                String var11 = (String)args[var4++];
	                ++var6;
	                var5 = var11.length();
	                var3 = var3 + var11;
	            }
	        }

	        HashMap var12;

	        for (var12 = new HashMap(); var4 < args.length; var4 += 2)
	        {
	            Character var13 = (Character)args[var4];
	            ItemStack var14 = null;

	            if (args[var4 + 1] instanceof Item)
	            {
	                var14 = new ItemStack((Item)args[var4 + 1]);
	            }
	            else if (args[var4 + 1] instanceof Block)
	            {
	                var14 = new ItemStack((Block)args[var4 + 1], 1, 32767);
	            }
	            else if (args[var4 + 1] instanceof ItemStack)
	            {
	                var14 = (ItemStack)args[var4 + 1];
	            }

	            var12.put(var13, var14);
	        }

	        ItemStack[] var15 = new ItemStack[var5 * var6];

	        for (int var16 = 0; var16 < var5 * var6; ++var16)
	        {
	            char var10 = var3.charAt(var16);

	            if (var12.containsKey(Character.valueOf(var10)))
	            {
	                var15[var16] = ((ItemStack)var12.get(Character.valueOf(var10))).copy();
	            }
	            else
	            {
	                var15[var16] = null;
	            }
	        }

	        ShapedRecipes var17 = new ShapedRecipes(var5, var6, var15, output);
	        return var17;
	    }
	
	@Deprecated //workaroud for the private methods in Minecraft code, will change when we have ASM (copied from Minecraft code)
	private static ShapelessRecipes createShapelessRecipe(ItemStack output, Object... args)
	{
		ArrayList var3 = new ArrayList();
        Object[] var4 = args;
        int var5 = args.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            Object var7 = var4[var6];

            if (var7 instanceof ItemStack)
            {
                var3.add(((ItemStack)var7).copy());
            }
            else if (var7 instanceof Item)
            {
                var3.add(new ItemStack((Item)var7));
            }
            else
            {
                if (!(var7 instanceof Block))
                {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                var3.add(new ItemStack((Block)var7));
            }
        }

       ShapelessRecipes recipe = new ShapelessRecipes(output, var3);
       return recipe;
	}
}
