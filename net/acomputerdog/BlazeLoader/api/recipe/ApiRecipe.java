package net.acomputerdog.BlazeLoader.api.recipe;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.HashMap;

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
		BlazeLoader.craftingManager.getRecipeList().add(createShapedRecipe(output, args));
	}
	
	/**
	 * Registers a shapeless recipe.
	 * @param output An itemStack with the recipe output
	 * @param args An object array of itemStacks to use
	 */
	public static void addShapelessRecipe(ItemStack output, Object... args){
		BlazeLoader.craftingManager.getRecipeList().add(createShapelessRecipe(output, args));
	}
	
	/**
	 * Registers a furnace recipe.
	 * @param input ID for the block/item to be smelted
	 * @param output An itemStack with the recipe output
	 * @param xp Float value with the amount of xp recieved when coocking an item/block
	 */
	public static void addSmeltingRecipe(int input, ItemStack output, float xp){
		BlazeLoader.smeltingManager.addSmelting(input, output, xp);
	}
	
	@Deprecated //workaround for the private methods in Minecraft code, will change when we have ASM (copied from Minecraft code)
	private static ShapedRecipes createShapedRecipe(ItemStack output, Object... args){
		String s = "";
	        int count = 0;
	        int num1 = 0;
	        int num2 = 0;

	        if (args[count] instanceof String[]){
	            String[] as = (String[])(args[count++]);

                for (String row : as) {
                    ++num2;
                    num1 = row.length();
                    s = s + row;
                }
	        }else{
	            while(args[count] instanceof String){
	                String row = (String)args[count++];
	                ++num2;
	                num1 = row.length();
	                s = s + row;
	            }
	        }

	        HashMap map;

	        for (map = new HashMap(); count < args.length; count += 2){
	            Character c = (Character)args[count];
	            ItemStack stack = null;

	            if (args[count + 1] instanceof Item) {
	                stack = new ItemStack((Item)args[count + 1]);
	            }else if (args[count + 1] instanceof Block){
	                stack = new ItemStack((Block)args[count + 1], 1, 32767);
	            }else if (args[count + 1] instanceof ItemStack){
	                stack = (ItemStack)args[count + 1];
	            }

	            map.put(c, stack);
	        }

	        ItemStack[] items = new ItemStack[num1 * num2];

	        for (int j = 0; j < num1 * num2; ++j){
	            char c = s.charAt(j);

	            if (map.containsKey(Character.valueOf(c))){
	                items[j] = ((ItemStack)map.get(Character.valueOf(c))).copy();
	            }else{
	                items[j] = null;
	            }
	        }

        return new ShapedRecipes(num1, num2, items, output);
	}
	
	@Deprecated //workaround for the private methods in Minecraft code, will change when we have ASM (copied from Minecraft code)
	private static ShapelessRecipes createShapelessRecipe(ItemStack output, Object... args){
		ArrayList items = new ArrayList();

        for (Object obj : args) {
            if (obj instanceof ItemStack) {
                items.add(((ItemStack) obj).copy());
            } else if (obj instanceof Item) {
                items.add(new ItemStack((Item) obj));
            } else {
                if (!(obj instanceof Block)) {
                    throw new IllegalArgumentException("Invalid shapeless recipe!");
                }

                items.add(new ItemStack((Block) obj));
            }
        }

        return new ShapelessRecipes(output, items);
	}
}
