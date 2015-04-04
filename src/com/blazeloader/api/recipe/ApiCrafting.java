package com.blazeloader.api.recipe;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.*;

public class ApiCrafting {

	private static final Map<Integer, BLCraftingManager> instances = new HashMap<Integer, BLCraftingManager>();
	private static int nextId = 1;
	
	static {
		instances.put(0, new BLCraftingManager(0, CraftingManager.getInstance().getRecipeList()));
	}
	
	/**
	 * Gets a wrapped instance of the normal CraftingManager.
	 * @return Manager instance of CraftingManager
	 */
	public static BLCraftingManager getVanillaCraftingManager() {
		return instances.get(0);
	}
	
	/**
	 * Intended for compatibility with mods that implemement their
	 * own CraftingManageers based off of the vanilla one.
	 * 
	 * Will parse a vanilla minecraft CraftingManager to a Blazeloader apis compatible Manager.
	 * 
	 * It is not recommened to use this method often. Rather start off with a Manager
	 * or keep a reference to the converted result for later use.
	 * 
	 * @param manager	CraftingManager to convert
	 * 
	 * @return Manager corresponding to the given CraftingManager
	 */
	public static BLCraftingManager toManager(CraftingManager manager) {
		for (BLCraftingManager i : instances.values()) {
			if (i.equals(manager)) return i;
		}
		return createCraftingManager((ArrayList<IRecipe>)manager.getRecipeList());
	}
	
	/**
	 * Gets a CraftingManager from the pool by it's unique id.
	 * 
	 * @param id	integer id of the manager you'd like to find.
	 * 
	 * @return Manager or null if not found.
	 */
	public static final BLCraftingManager getManagerFromId(int id) {
		return instances.containsKey(id) ? instances.get(id) : null;
	}
	
	/**
	 * Creates a brand spanking **new** Crafting Manager.
	 */
	public static BLCraftingManager createCraftingManager() {
		return createCraftingManager(new ArrayList<IRecipe>());
	}

	private final static BLCraftingManager createCraftingManager(ArrayList<IRecipe> startingRecipes) {
		int id = nextId++;
		instances.put(id, new BLCraftingManager(id, startingRecipes));
		return instances.get(id);
	}
	
	/**
	 * Custom implementation of the CraftingManager.
	 * Supports additional functionality such as reverse crafting,
	 * crafting areas greater than 3x3 and methods for removing recipes.
	 *
	 */
	public static final class BLCraftingManager implements Comparable<BLCraftingManager> {
		private final int id;
		private final List<IRecipe> recipes;

		private BLCraftingManager(int n, List<IRecipe> recipes) {
			id = n;
			this.recipes = recipes;
		}
		
		/**
		 * Gets the unique integer id for this CraftingManager.
		 * Can be used to retrieve this manager again from the pool of CraftingManagers.
		 * 
		 * @return integer id
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * Returns an unmodifieable list of recipes registered to this CraftingManager.
		 * 
		 * @return List of Recipes
		 */
		public List<IRecipe> getRecipeList() {
			return Collections.unmodifiableList(recipes);
		}
		
	    /**
	     * Adds a shaped recipe to this CraftingManager.
	     * 
	     * @param output	ItemStack output for this recipe
	     * @param input		Strings of recipe pattern followed by chars mapped to Items/Blocks/ItemStacks
	     */
	    public ShapedRecipe addRecipe(ItemStack output, Object... input) {
	    	ShapedRecipe result = createShaped(output, false, input);
	        recipes.add(result);
	        return result;
	    }
	    
	    /**
	     * Adds a shapeless crafting recipe to this CraftingManager.
	     *  
	     * @param output	ItemStack output for this recipe
	     * @param input		An array of ItemStack's Item's and Block's that make up the recipe.
	     */
	    public ShapelessRecipe addShapelessRecipe(ItemStack output, Object... input) {
	    	ShapelessRecipe result = createShapeless(output, false, input);
	        recipes.add(result);
	        return result;
	    }
	    
	    /**
	     * Adds a shaped recipe to this CraftingManager.
	     * 
	     * @param output	ItemStack output for this recipe
	     * @param input		Strings of recipe pattern followed by chars mapped to Items/Blocks/ItemStacks
	     */
	    public ReversibleShapedRecipe addReverseRecipe(ItemStack output, Object... input) {
	    	ShapedRecipe result = createShaped(output, true, input);
	        recipes.add(result);
	        return (ReversibleShapedRecipe)result;
	    }

	    /**
	     * Adds a shapeless crafting recipe to this CraftingManager.
	     *  
	     * @param output	ItemStack output for this recipe
	     * @param input		An array of ItemStack's Item's and Block's that make up the recipe.
	     */
	    public ReversibleShapelessRecipe addReverseShapelessRecipe(ItemStack output, Object... input) {
	    	ShapelessRecipe result = createShapeless(output, true, input);
	        recipes.add(result);
	        return (ReversibleShapelessRecipe)result;
	    }
	    
	    /**
	     * Adds an IRecipe to this RecipeManager.
	     *  
	     * @param recipe A recipe that will be added to the recipe list.
	     */
	    public void addRecipe(ShapelessRecipe recipe) {
	        recipes.add(recipe);
	    }
	    
	    /**
	     * Adds an IRecipe to this RecipeManager.
	     *  
	     * @param recipe A recipe that will be added to the recipe list.
	     */
	    public void addRecipe(ShapedRecipe recipe) {
	        recipes.add(recipe);
	    }
	    
	    /**
	     * Adds an IRecipe to this RecipeManager.
	     *  
	     * @param recipe A recipe that will be added to the recipe list.
	     */
	    public void addRecipe(IReversibleRecipe recipe) {
	        recipes.add(recipe);
	    }
	    
	    /**
	     * Removes the given recipe
	     * 
	     * @param recipe	recipe to be removed
	     * 
	     * @return true if the recipe was removed, false otherwise
	     */
	    public boolean removeRecipe(IRecipe recipe) {
	    	int index = recipes.indexOf(recipe);
	    	if (index >= 0) {
	    		recipes.remove(index);
	    		return true;
	    	}
	    	return false;
	    }
	    
	    /**
	     * Removes recipes for the given item
	     * 
	     * @param result	ItemStack result of the recipe to be removed
	     * 
	     * @return total number of successful removals
	     */
	    public int removeRecipe(ItemStack result) {
	    	return removeRecipe(result, -1);
	    }
	    
	    /**
	     * Removes recipes for the given item
	     * 
	     * @param result		ItemStack result of the recipe to be removed
	     * @param maxRemovals	Maximum number of removals
	     * 
	     * @return total number of successful removals
	     */
	    public int removeRecipe(ItemStack result, int maxRemovals) {
	    	int count = 0;
	    	for (int i = 0; i < recipes.size(); i++) {
	    		if (recipes.get(i).getRecipeOutput() == result) {
	    			count++;
	    			recipes.remove(i);
	    			if (maxRemovals > 0 && count >= maxRemovals) return count;
	    		}
	    	}
	    	return count;
	    }
	    
	    private ShapedRecipe createShaped(ItemStack output, boolean reverse, Object... input) {
	        String recipe = "";
	        int index = 0;
	        int width = 0;
	        int height = 0;

	        if (input[index] instanceof String[]) {
	            for (String i : (String[])input[index++]) {
	                ++height;
	                width = i.length();
	                recipe += i;
	            }
	        } else {
	            while (input[index] instanceof String) {
	                String line = (String)input[index++];
	                ++height;
	                width = line.length();
	                recipe += line;
	            }
	        }

	        HashMap<Character, ItemStack> stackmap = new HashMap<Character, ItemStack>();
	        while (index < input.length) {
				char var13 = (Character) input[index];
				ItemStack var15 = null;
				if (input[index + 1] instanceof Item) {
	                var15 = new ItemStack((Item)input[index + 1]);
	            } else if (input[index + 1] instanceof Block) {
	                var15 = new ItemStack((Block)input[index + 1], 1, 32767);
	            } else if (input[index + 1] instanceof ItemStack) {
	                var15 = (ItemStack)input[index + 1];
	            }
	            stackmap.put(var13, var15);
	            index += 2;
	        }

	        ItemStack[] stacks = new ItemStack[width * height];
	        for (int i = 0; i < width * height; i++) {
	            char key = recipe.charAt(i);
	            if (stackmap.containsKey(key)) {
	                stacks[i] = stackmap.get(key).copy();
	            } else {
	                stacks[i] = null;
	            }
	        }
	        if (reverse) return new ReversibleShapedRecipe(width, height, stacks, output);
	        return new ShapedRecipe(width, height, stacks, output);
	    }
	    
	    private ShapelessRecipe createShapeless(ItemStack output, boolean reverse, Object ... input) {
	        ArrayList itemStacks = Lists.newArrayList();
	        for (int i = 0; i < input.length; i++) {
	            Object obj = input[i];
	            if (obj instanceof ItemStack) {
	            	itemStacks.add(((ItemStack)obj).copy());
	            } else if (obj instanceof Item) {
	            	itemStacks.add(new ItemStack((Item)obj));
	            } else {
	                if (!(obj instanceof Block)) throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + obj.getClass().getName() + "!");
	                itemStacks.add(new ItemStack((Block)obj));
	            }
	        }
	        if (reverse) return new ReversibleShapelessRecipe(output, itemStacks);
	        return new ShapelessRecipe(output, itemStacks);
	    }
	    
	    /**
	     * Retrieves the result of a matched recipe in this RecipeManager.
	     * 
	     * @param inventory		inventory containing the crafting materials
	     * @param world			the world that the crafting is being done in (usually the world of the player)
	     * 
	     * @return ItemStack result or null if none match
	     */
	    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world) {
	        for (IRecipe i : recipes) {
	        	if (i.matches(inventory, world)) return i.getCraftingResult(inventory);
	        }
	        return null;
	    }
	    
	    /**
	     * Retrieves the input required to craft the given item.
	     * 
	     * @param recipeOutput	ItemStack you wish to uncraft
	     * @param width			width of crafting table
	     * @param height		height of crafting table
	     * 
	     * @return ItemStack[] array of inventory contents needed
	     */
	    public ItemStack[] findRecipeInput(ItemStack recipeOutput, int width, int height) {
	    	for (IRecipe i : recipes) {
	    		if (i instanceof IReversibleRecipe) {
	    			IReversibleRecipe recipe = ((IReversibleRecipe)i);
	    			if (recipe.matchReverse(recipeOutput, width, height)) return recipe.getRecipeInput();
	    		}
	    	}
	    	return null;
	    }
	    
	    /**
	     * Gets the remaining contents for the inventory after performing a craft.
	     * 
	     * @param inventory		inventory containing the crafting materials
	     * @param world			the world that the crafting is being done in (usually the world of the player)
	     * 
	     * @return ItemStack[] array or remaining items
	     */
	    public ItemStack[] getUnmatchedInventory(InventoryCrafting inventory, World world) {
	        for (IRecipe i : recipes) {
	            if (i.matches(inventory, world)) return i.getRemainingItems(inventory);
	        }
	        ItemStack[] newInventory = new ItemStack[inventory.getSizeInventory()];
	        for (int i = 0; i < newInventory.length; i++) {
	            newInventory[i] = inventory.getStackInSlot(i);
	        }
	        return newInventory;
	    }
	    
	    public boolean equals(Object obj) {
			if (obj instanceof BLCraftingManager) {
				return ((BLCraftingManager) obj).id == id;
			}
	    	if (obj instanceof CraftingManager) {
	    		return recipes.equals(((CraftingManager)obj).getRecipeList());
	    	}
	    	if (obj instanceof List<?>) {
	    		return obj.equals(recipes);
	    	}
	    	return super.equals(obj);
	    }

		public int compareTo(BLCraftingManager o) {
			return o.id - id;
		}
	}
}