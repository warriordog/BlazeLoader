package net.acomputerdog.BlazeLoader.api.recipe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class BlCraftingManager {
	public static final int VanillaCraftingManagerKey = 0;
	
	private static final Map<Integer, List<IRecipe>> recipeManagers = new HashMap();
	private static final CraftingManager instance = CraftingManager.getInstance();
	
	private final int key;
	
	
	public static int registerCraftingManager() {
		int id = recipeManagers.size() + 1;
		recipeManagers.put(id, new ArrayList());
		return id;
	}
	
	private BlCraftingManager(int id) {
		key = id;
	}
	
	/**
	 * Returns a ManillaCraftingManager instance connected to a recipe list by the given id
	 * */
	public static final BlCraftingManager getInstance(int id) {
		return new BlCraftingManager(id);
	}
	
	/**
	 * Creates and registers a new ManillaCraftingManager
	 * 
	 */
	public static final BlCraftingManager getNewInstance() {
		return getInstance(registerCraftingManager());
	}
	
	public final int getKey() {
		return key;
	}
	
	public final List getRecipeList() {
		if (recipeManagers.containsKey(key)) {
			if (recipeManagers.get(key) == null) {
				recipeManagers.put(key, new ArrayList());
			}
			return recipeManagers.get(key);
		}
		return instance.getRecipeList();
	}
	
	/**
	 * Adds a recipe to the normal crafting manager
	 * @param stack
	 * @param args
	 * @return resulting recipe
	 */
	public static ShapedRecipes addVanillaRecipe(ItemStack stack, Object ... args) {
		ShapedRecipes reci = BlCraftingManager.getInstance(VanillaCraftingManagerKey).makeRecipe(stack, 0, args);
		instance.getRecipeList().add(reci);
		return reci;
	}
	
	/**
	 * Adds a recipe to this crafting manager
	 * @param stack
	 * @param args
	 * @return resulting recipe
	 */
	public ShapedRecipes addRecipe(ItemStack stack, Object ... args) {
		return addRecipe(0, stack, args);
	}
	
	/**
	 * Adds a recipe to this crafting manager that can be used both forward and in reverse
	 * @param stack
	 * @param args
	 * @return resulting recipe
	 */
	public ShapedRecipes addReversableRecipe(ItemStack stack, Object ... args) {
		return addRecipe(1, stack, args);
	}
	
	/**
	 * Adds a recipe to this crafting manager that can only be used in reverse
	 * @param stack
	 * @param args
	 * @return resulting recipe
	 */
	public ShapedRecipes addReverseRecipe(ItemStack stack, Object ... args) {
		return addRecipe(2, stack, args);
	}
			
	private ShapedRecipes addRecipe(int reversable, ItemStack stack, Object ... args) {
		ShapedRecipes reci = makeRecipe(stack, reversable, args);
		getRecipeList().add(reci);
		return reci;
	}
	
	private ShapedRecipes makeRecipe(ItemStack par1ItemStack, int reversable, Object ... inputObjects) {
		String recipePattern = "";
		int i = 0, len = 0, pointer = 0;

		if (inputObjects[i] instanceof String[]) {
			String[] inputStrings = (String[])((String[])inputObjects[i++]);

			for (int k = 0; k < inputStrings.length; ++k) {
				String inputString = inputStrings[k];
				++pointer;
				len = inputString.length();
				recipePattern += inputString;
			}
		} else {
			while (inputObjects[i] instanceof String) {
				String inputPatternString = (String)inputObjects[i++];
				++pointer;
				len = inputPatternString.length();
				recipePattern += inputPatternString;
			}
		}

		HashMap inputMapping;
		for (inputMapping = new HashMap(); i < inputObjects.length; i += 2) {
			Character var13 = (Character)inputObjects[i];
			ItemStack var14 = null;

			if (inputObjects[i + 1] instanceof Item) {
				var14 = new ItemStack((Item)inputObjects[i + 1]);
			} else if (inputObjects[i + 1] instanceof Block) {
				var14 = new ItemStack((Block)inputObjects[i + 1], 1, 32767);
			} else if (inputObjects[i + 1] instanceof ItemStack) {
				var14 = (ItemStack)inputObjects[i + 1];
			}

			inputMapping.put(var13, var14);
		}

		ItemStack[] inputStacks = new ItemStack[len * pointer];

		for (int k = 0; k < len * pointer; ++k) {
			char var10 = recipePattern.charAt(k);

			if (inputMapping.containsKey(Character.valueOf(var10))) {
				inputStacks[k] = ((ItemStack)inputMapping.get(Character.valueOf(var10))).copy();
			} else {
				inputStacks[k] = null;
			}
		}

		if (reversable > 0) {
			ReversableShapedRecipe result = new ReversableShapedRecipe(len, pointer, inputStacks, par1ItemStack);
			if (reversable == 2) {
				result.setReverseOnly();
			}
			return result;
		}
		return new BShapedRecipe(len, pointer, inputStacks, par1ItemStack);
	}
	
	/**
	 * Adds a shapeless recipe to the normal crafting manager
	 * @param stack
	 * @param args
	 */
	public static void addVanillaShapelessRecipe(ItemStack stack, Object ... args) {
		instance.getRecipeList().add(BlCraftingManager.getInstance(VanillaCraftingManagerKey).makeShapelessRecipe(stack, args));
	}
	
	/**
	 * Adds a shapeless recipe to this crafting manager
	 * @param stack
	 * @param args
	 */
	public void addShapelessRecipe(ItemStack stack, Object ... args) {
		getRecipeList().add(makeShapelessRecipe(stack, args));
	}

	private BShapelessRecipe makeShapelessRecipe(ItemStack par1ItemStack, Object ... par2ArrayOfObj) {
		ArrayList inputStacks = new ArrayList();
		
		for (Object inputObj : par2ArrayOfObj) {
			if (inputObj instanceof ItemStack) {
				inputStacks.add(((ItemStack)inputObj).copy());
			} else if (inputObj instanceof Item) {
				inputStacks.add(new ItemStack((Item)inputObj));
			} else {
				if (!(inputObj instanceof Block)) throw new RuntimeException("Invalid shapeless recipe!");
				
				inputStacks.add(new ItemStack((Block)inputObj));
			}
		}
		
		return new BShapelessRecipe(par1ItemStack, inputStacks);
	}
    
	/**
	 * Gets the result of the recipe that matches the crafting table input
	 * @param craftingInventory
	 * @param w
	 * @return
	 */
	public ItemStack findMatchingRecipe(InventoryCrafting craftingInventory, World w) {
		return findMatchingRecipe(craftingInventory, w, 3, 3);
	}
    
	/**
	 * Gets the result of the recipe that matches the crafting table input 
	 * @param craftingInventory
	 * @param w
	 * @param width: table width
	 * @param height: table height
	 * @return
	 */
	public ItemStack findMatchingRecipe(InventoryCrafting craftingInventory, World w, int width, int height) {
		int i, pointer = 0;
		ItemStack one = null, two = null;
		
		for (i = 0; i < craftingInventory.getSizeInventory(); ++i) {
			ItemStack checkedStack = craftingInventory.getStackInSlot(i);

			if (checkedStack != null) {
				if (pointer == 0) {
					one = checkedStack;
				} else if (pointer == 1) {
					two = checkedStack;
				}

				++pointer;
			}
		}

		if (pointer == 2 && one == two && one.stackSize == 1 && two.stackSize == 1 && one.getItem().isDamageable()) {
			Item itemOne = one.getItem();
			int damageRemainder = itemOne.getMaxDamage() - one.getItemDamageForDisplay();
			int otherDanageRemainder = itemOne.getMaxDamage() - two.getItemDamageForDisplay();
			int damageOff = damageRemainder + otherDanageRemainder + itemOne.getMaxDamage() * 5 / 100;
			int resultDamage = itemOne.getMaxDamage() - damageOff;
	
			if (resultDamage < 0) {
				resultDamage = 0;
			}

			return new ItemStack(one.getItem(), 1, resultDamage);
		}
        
        	List<IRecipe> recipes = getRecipeList();
		for (IRecipe var12 : recipes) {
			if (var12 instanceof BShapedRecipe) {
				((BShapedRecipe)var12).setCraftingSize(width, height);
			} else if (var12 instanceof BShapelessRecipe) {
				((BShapelessRecipe)var12).setCraftingSize(width, height);
			}
            	
			if (var12.matches(craftingInventory, w)) {
				return var12.getCraftingResult(craftingInventory);
			}
		}

		return null;
	}
    
	/**
	 * Gets the input items for recipe that has the matching result
	 * @param result
	 * @param width: table width
	 * @param height: table height
	 * @return
	 */
	public ItemStack[] getRecipeInput(ItemStack result, int width, int height) {
		List<IRecipe> recipes = getRecipeList();
		for (IRecipe i : recipes) {
			if (i instanceof ReversableShapedRecipe) {
				ReversableShapedRecipe r = (ReversableShapedRecipe)i;
				r.setCraftingSize(width, height);
				if (ItemStack.areItemStacksEqual(r.getRecipeOutput(), result)) {
					return r.getRecipeInput();
				}
			}
		}
		
		return null;
	}
    
	/**
	 * Removes a recipe from the regular crafting manager
	 * @param resultItem
	 * @param totalRemovals
	 */
	public static void RemoveVanillaRecipe(ItemStack resultItem, int totalRemovals) {
		RemoveRecipe(resultItem, totalRemovals, VanillaCraftingManagerKey);
	}
	
	/**
	 * Removes a recipe from this crafting manager
	 * @param resultItem
	 * @param totalRemovals
	 */
	public void RemoveRecipe(ItemStack resultItem, int totalRemovals) {
		RemoveRecipe(resultItem, totalRemovals, key);
	}
	
	/**
	 * Code by yope_fried inspired by pigalot, modified by awr
	*/
	private static void RemoveRecipe(ItemStack resultItem, int totalRemovals, int recipeManagerId) {
		List<IRecipe> recipes;
		
		if (recipeManagerId == VanillaCraftingManagerKey) {
			recipes = instance.getRecipeList();
		} else {
			recipes = BlCraftingManager.getInstance(recipeManagerId).getRecipeList();
		}
		int count = 0;

		for (int i = 0; i < recipes.size(); i++) {
			IRecipe tmpRecipe = recipes.get(i);

			if (tmpRecipe instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes)tmpRecipe;
				ItemStack recipeResult = recipe.getRecipeOutput();

				if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
					recipes.remove(i--);
					count++;

					if (count == totalRemovals) {
						return;
					}
				}
			}
		}
	}
}
