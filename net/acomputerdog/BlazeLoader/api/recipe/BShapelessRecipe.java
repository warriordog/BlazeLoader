package net.acomputerdog.BlazeLoader.api.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class BShapelessRecipe extends ShapelessRecipes {
	private final List recipeItems;
	
	private int craftingWidth = 3;
    private int craftingHeight = 3;
	
	public BShapelessRecipe(ItemStack par1ItemStack, List par2List) {
        super(par1ItemStack, par2List);
        recipeItems = par2List;
    }
	
	public void setCraftingSize(int width, int height) {
    	craftingWidth = width;
    	craftingHeight = height;
    }
	
	public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
        ArrayList var3 = new ArrayList(recipeItems);

        for (int var4 = 0; var4 < craftingWidth; ++var4) {
            for (int var5 = 0; var5 < craftingHeight; ++var5) {
                ItemStack var6 = par1InventoryCrafting.getStackInRowAndColumn(var5, var4);

                if (var6 != null) {
                    boolean var7 = false;
                    Iterator var8 = var3.iterator();

                    while (var8.hasNext()) {
                        ItemStack var9 = (ItemStack)var8.next();

                        if (var6 == var9 && (var9.getItemDamage() == 32767 || var6.getItemDamage() == var9.getItemDamage())) {
                            var7 = true;
                            var3.remove(var9);
                            break;
                        }
                    }

                    if (!var7) {
                        return false;
                    }
                }
            }
        }

        return var3.isEmpty();
    }
}
