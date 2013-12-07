package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;

/**
 * Class that allows access to the various Package Private fields in Minecraft's code.
 */
public class MinecraftPackageAccessorB {
    public static void addShapelessRecipe(ItemStack output, Object... args){
        CraftingManager.getInstance().addShapelessRecipe(output, args);
    }

    public static void addShapedRecipe(ItemStack output, Object... args){
        CraftingManager.getInstance().addRecipe(output, args);
    }
}
