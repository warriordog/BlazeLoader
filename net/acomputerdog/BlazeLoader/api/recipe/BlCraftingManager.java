package manilla.util.crafting;

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
		} else {
			return instance.getRecipeList();
		}
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
	
	private ShapedRecipes makeRecipe(ItemStack par1ItemStack, int reversable, Object ... par2ArrayOfObj) {
        String var3 = "";
        int var4 = 0, var5 = 0, var6 = 0;

        if (par2ArrayOfObj[var4] instanceof String[]) {
            String[] var7 = (String[])((String[])par2ArrayOfObj[var4++]);

            for (int var8 = 0; var8 < var7.length; ++var8) {
                String var9 = var7[var8];
                ++var6;
                var5 = var9.length();
                var3 = var3 + var9;
            }
        } else {
            while (par2ArrayOfObj[var4] instanceof String) {
                String var11 = (String)par2ArrayOfObj[var4++];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        }

        HashMap var12;

        for (var12 = new HashMap(); var4 < par2ArrayOfObj.length; var4 += 2) {
            Character var13 = (Character)par2ArrayOfObj[var4];
            ItemStack var14 = null;

            if (par2ArrayOfObj[var4 + 1] instanceof Item) {
                var14 = new ItemStack((Item)par2ArrayOfObj[var4 + 1]);
            } else if (par2ArrayOfObj[var4 + 1] instanceof Block) {
                var14 = new ItemStack((Block)par2ArrayOfObj[var4 + 1], 1, 32767);
            } else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack) {
                var14 = (ItemStack)par2ArrayOfObj[var4 + 1];
            }

            var12.put(var13, var14);
        }

        ItemStack[] var15 = new ItemStack[var5 * var6];

        for (int var16 = 0; var16 < var5 * var6; ++var16) {
            char var10 = var3.charAt(var16);

            if (var12.containsKey(Character.valueOf(var10))) {
                var15[var16] = ((ItemStack)var12.get(Character.valueOf(var10))).copy();
            } else {
                var15[var16] = null;
            }
        }

        if (reversable > 0) {
        	ReversableShapedRecipe result = new ReversableShapedRecipe(var5, var6, var15, par1ItemStack);
        	if (reversable == 2) {
        		result.setReverseOnly();
        	}
        	return result;
        }
        return new BShapedRecipe(var5, var6, var15, par1ItemStack);
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
        ArrayList var3 = new ArrayList();
        Object[] var4 = par2ArrayOfObj;

        for (Object var7 : par2ArrayOfObj) {
            if (var7 instanceof ItemStack) {
                var3.add(((ItemStack)var7).copy());
            } else if (var7 instanceof Item) {
                var3.add(new ItemStack((Item)var7));
            } else {
                if (!(var7 instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipe!");
                }

                var3.add(new ItemStack((Block)var7));
            }
        }

        return new BShapelessRecipe(par1ItemStack, var3);
    }
    
    /**
     * 
	 * Gets the result of the recipe that matches the crafting table input
     * @param par1InventoryCrafting
     * @param par2World
     * @return
     */
    public ItemStack findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World) {
    	return findMatchingRecipe(par1InventoryCrafting, par2World, 3, 3);
    }
    
    /**
     * Gets the result of the recipe that matches the crafting table input 
     * @param par1InventoryCrafting
     * @param par2World
     * @param width: table width
     * @param height: table height
     * @return
     */
    public ItemStack findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World, int width, int height) {
        int var3 = 0;
        ItemStack var4 = null;
        ItemStack var5 = null;
        int var6;

        for (var6 = 0; var6 < par1InventoryCrafting.getSizeInventory(); ++var6) {
            ItemStack var7 = par1InventoryCrafting.getStackInSlot(var6);

            if (var7 != null) {
                if (var3 == 0) {
                    var4 = var7;
                }

                if (var3 == 1) {
                    var5 = var7;
                }

                ++var3;
            }
        }

        if (var3 == 2 && var4 == var5 && var4.stackSize == 1 && var5.stackSize == 1 && var4.getItem().isDamageable()) {
            Item var11 = var4.getItem();
            int var13 = var11.getMaxDamage() - var4.getItemDamageForDisplay();
            int var8 = var11.getMaxDamage() - var5.getItemDamageForDisplay();
            int var9 = var13 + var8 + var11.getMaxDamage() * 5 / 100;
            int var10 = var11.getMaxDamage() - var9;

            if (var10 < 0) {
                var10 = 0;
            }

            return new ItemStack(var4.getItem(), 1, var10);
        } else {
        	List<IRecipe> recipes = getRecipeList();
            for (IRecipe var12 : recipes) {
            	if (var12 instanceof BShapedRecipe) {
            		((BShapedRecipe)var12).setCraftingSize(width, height);
            	} else if (var12 instanceof BShapelessRecipe) {
            		((BShapelessRecipe)var12).setCraftingSize(width, height);
            	}
            	
                if (var12.matches(par1InventoryCrafting, par2World)) {
                    return var12.getCraftingResult(par1InventoryCrafting);
                }
            }

            return null;
        }
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
