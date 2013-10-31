package net.minecraft.src;

/**
 * Class that allows access to the various Package Private fields in Minecraft's code.
 */
public class MinecraftPackageAccessor {
    public static void addShapelessRecipe(ItemStack output, Object... args){
        CraftingManager.getInstance().addShapelessRecipe(output, args);
    }

    public static void addShapedRecipe(ItemStack output, Object... args){
        CraftingManager.getInstance().addRecipe(output, args);
    }
}
