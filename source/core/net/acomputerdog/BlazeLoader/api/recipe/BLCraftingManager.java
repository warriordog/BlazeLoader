package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLCraftingManager {
    public static final int VanillaCraftingManagerKey = 0;

    private static final Map<Integer, List<IRecipe>> recipeManagers = new HashMap<Integer, List<IRecipe>>();
    private static final CraftingManager instance = CraftingManager.getInstance();

    private final int key;


    public static int registerCraftingManager() {
        int id = recipeManagers.size() + 1;
        recipeManagers.put(id, new ArrayList<IRecipe>());
        return id;
    }

    private BLCraftingManager(int id) {
        key = id;
    }

    /**
     * Returns a BLCraftingManager instance connected to a recipe list by the given id
     */
    public static BLCraftingManager getInstance(int id) {
        return new BLCraftingManager(id);
    }

    /**
     * Creates and registers a new BLCraftingManager
     */
    public static BLCraftingManager getNewInstance() {
        return getInstance(registerCraftingManager());
    }

    public final int getKey() {
        return key;
    }

    public final List<IRecipe> getRecipeList() {
        if (recipeManagers.containsKey(key)) {
            if (recipeManagers.get(key) == null) {
                recipeManagers.put(key, new ArrayList<IRecipe>());
            }
            return recipeManagers.get(key);
        }
        return (List<IRecipe>) instance.getRecipeList();
    }

    /**
     * Adds a recipe to the normal crafting manager
     *
     * @param output The output item
     * @param input  The item recipe
     * @return resulting recipe
     */
    public static ShapedRecipes addVanillaRecipe(ItemStack output, Object... input) {
        ShapedRecipes recipe = BLCraftingManager.getInstance(VanillaCraftingManagerKey).makeRecipe(output, 0, input);
        ((List<IRecipe>) instance.getRecipeList()).add(recipe);
        return recipe;
    }

    /**
     * Adds a recipe to this crafting manager
     *
     * @param output The output item
     * @param input  The item recipe
     * @return resulting recipe
     */
    public ShapedRecipes addRecipe(ItemStack output, Object... input) {
        return addRecipe(0, output, input);
    }

    /**
     * Adds a recipe to this crafting manager that can be used both forward and in reverse
     *
     * @param output The output item
     * @param input  The item recipe
     * @return resulting recipe
     */
    public ShapedRecipes addReversibleRecipe(ItemStack output, Object... input) {
        return addRecipe(1, output, input);
    }

    /**
     * Adds a recipe to this crafting manager that can only be used in reverse
     *
     * @param output The output item
     * @param input  The item recipe
     * @return resulting recipe
     */
    public ShapedRecipes addReverseRecipe(ItemStack output, Object... input) {
        return addRecipe(2, output, input);
    }

    private ShapedRecipes addRecipe(int reversible, ItemStack stack, Object... args) {
        ShapedRecipes recipe = makeRecipe(stack, reversible, args);
        getRecipeList().add(recipe);
        return recipe;
    }

    private ShapedRecipes makeRecipe(ItemStack par1ItemStack, int reversible, Object... inputObjects) {
        String recipePattern = "";
        int i = 0, len = 0, pointer = 0;

        if (inputObjects[i] instanceof String[]) {
            String[] inputStrings = (String[]) inputObjects[i++];

            for (String inputString : inputStrings) {
                ++pointer;
                len = inputString.length();
                recipePattern += inputString;
            }
        } else {
            while (inputObjects[i] instanceof String) {
                String inputPatternString = (String) inputObjects[i++];
                ++pointer;
                len = inputPatternString.length();
                recipePattern += inputPatternString;
            }
        }

        HashMap<Character, ItemStack> inputMapping;
        for (inputMapping = new HashMap<Character, ItemStack>(); i < inputObjects.length; i += 2) {
            Character var13 = (Character) inputObjects[i];
            ItemStack var14 = null;

            if (inputObjects[i + 1] instanceof Item) {
                var14 = new ItemStack((Item) inputObjects[i + 1]);
            } else if (inputObjects[i + 1] instanceof Block) {
                var14 = new ItemStack((Block) inputObjects[i + 1], 1, 32767);
            } else if (inputObjects[i + 1] instanceof ItemStack) {
                var14 = (ItemStack) inputObjects[i + 1];
            }

            inputMapping.put(var13, var14);
        }

        ItemStack[] inputStacks = new ItemStack[len * pointer];

        for (int k = 0; k < len * pointer; ++k) {
            char var10 = recipePattern.charAt(k);

            if (inputMapping.containsKey(Character.valueOf(var10))) {
                inputStacks[k] = (inputMapping.get(Character.valueOf(var10))).copy();
            } else {
                inputStacks[k] = null;
            }
        }

        if (reversible > 0) {
            ReversibleShapedRecipe result = new ReversibleShapedRecipe(len, pointer, inputStacks, par1ItemStack);
            if (reversible == 2) {
                result.setReverseOnly();
            }
            return result;
        }
        return new BShapedRecipe(len, pointer, inputStacks, par1ItemStack);
    }

    /**
     * Adds a shapeless recipe to the normal crafting manager
     *
     * @param output The output item
     * @param input  The item recipe
     */
    public static void addVanillaShapelessRecipe(ItemStack output, Object... input) {
        ((List<IRecipe>) instance.getRecipeList()).add(BLCraftingManager.getInstance(VanillaCraftingManagerKey).makeShapelessRecipe(output, input));
    }

    /**
     * Adds a shapeless recipe to this crafting manager
     *
     * @param output The output item
     * @param input  The item recipe
     */
    public void addShapelessRecipe(ItemStack output, Object... input) {
        getRecipeList().add(makeShapelessRecipe(output, input));
    }

    private BShapelessRecipe makeShapelessRecipe(ItemStack par1ItemStack, Object... par2ArrayOfObj) {
        ArrayList<ItemStack> inputStacks = new ArrayList<ItemStack>();

        for (Object inputObj : par2ArrayOfObj) {
            if (inputObj instanceof ItemStack) {
                inputStacks.add(((ItemStack) inputObj).copy());
            } else if (inputObj instanceof Item) {
                inputStacks.add(new ItemStack((Item) inputObj));
            } else {
                if (!(inputObj instanceof Block)) throw new RuntimeException("Invalid shapeless recipe!");

                inputStacks.add(new ItemStack((Block) inputObj));
            }
        }

        return new BShapelessRecipe(par1ItemStack, inputStacks);
    }

    /**
     * Gets the result of the recipe that matches the crafting table input
     *
     * @param craftingInventory The crafting inventory where this crafting is taking place.
     * @param w                 The world the inventory is located in
     * @return Return a matching recipe
     */
    public ItemStack findMatchingRecipe(InventoryCrafting craftingInventory, World w) {
        return findMatchingRecipe(craftingInventory, w, 3, 3);
    }

    /**
     * Gets the result of the recipe that matches the crafting table input
     *
     * @param craftingInventory The crafting inventory where this crafting is taking place.
     * @param w                 The world the inventory is located in
     * @param width:            table width
     * @param height:           table height
     * @return Return a matching recipe
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
            int otherDamageRemainder = itemOne.getMaxDamage() - two.getItemDamageForDisplay();
            int damageOff = damageRemainder + otherDamageRemainder + itemOne.getMaxDamage() * 5 / 100;
            int resultDamage = itemOne.getMaxDamage() - damageOff;

            if (resultDamage < 0) {
                resultDamage = 0;
            }

            return new ItemStack(one.getItem(), 1, resultDamage);
        }

        List<IRecipe> recipes = getRecipeList();
        for (IRecipe var12 : recipes) {
            if (var12 instanceof BShapedRecipe) {
                ((BShapedRecipe) var12).setCraftingSize(width, height);
            } else if (var12 instanceof BShapelessRecipe) {
                ((BShapelessRecipe) var12).setCraftingSize(width, height);
            }

            if (var12.matches(craftingInventory, w)) {
                return var12.getCraftingResult(craftingInventory);
            }
        }

        return null;
    }

    /**
     * Gets the input items for recipe that has the matching result
     *
     * @param result  The item being identified
     * @param width:  table width
     * @param height: table height
     * @return Return the input items for the specified recipe
     */
    public ItemStack[] getRecipeInput(ItemStack result, int width, int height) {
        List<IRecipe> recipes = getRecipeList();
        for (IRecipe i : recipes) {
            if (i instanceof ReversibleShapedRecipe) {
                ReversibleShapedRecipe r = (ReversibleShapedRecipe) i;
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
     *
     * @param resultItem    The item to remove
     * @param totalRemovals The number of possible recipes to remove
     */
    public static void RemoveVanillaRecipe(ItemStack resultItem, int totalRemovals) {
        RemoveRecipe(resultItem, totalRemovals, VanillaCraftingManagerKey);
    }

    /**
     * Removes a recipe from this crafting manager
     *
     * @param resultItem    The item to remove
     * @param totalRemovals The number of possible recipes to remove
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
            recipes = (List<IRecipe>) instance.getRecipeList();
        } else {
            recipes = BLCraftingManager.getInstance(recipeManagerId).getRecipeList();
        }
        int count = 0;

        for (int i = 0; i < recipes.size(); i++) {
            IRecipe tmpRecipe = recipes.get(i);

            if (tmpRecipe instanceof ShapedRecipes) {
                ShapedRecipes recipe = (ShapedRecipes) tmpRecipe;
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
