package net.acomputerdog.Blazeloader.api.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class BShapedRecipe extends ShapedRecipes
{
    /** How many horizontal slots this recipe is wide. */
    private int recipeWidth;

    /** How many vertical slots this recipe uses. */
    private int recipeHeight;
    
    
    private int craftingWidth = 3;
    private int craftingHeight = 3;

    /** Is a array of ItemStack that composes the recipe. */
    protected ItemStack[] recipeItems;

    public BShapedRecipe(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
    {
    	super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
        this.recipeWidth = par1;
        this.recipeHeight = par2;
        this.recipeItems = par3ArrayOfItemStack;
    }
    
    public void setCraftingSize(int width, int height) {
    	craftingWidth = width;
    	craftingHeight = height;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        for (int var3 = 0; var3 <= craftingWidth - this.recipeWidth; ++var3) {
            for (int var4 = 0; var4 <= craftingHeight - this.recipeHeight; ++var4) {
                if (this.checkMatch(par1InventoryCrafting, var3, var4, true))
                {
                    return true;
                }

                if (this.checkMatch(par1InventoryCrafting, var3, var4, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting par1InventoryCrafting, int par2, int par3, boolean par4)
    {
        for (int var5 = 0; var5 < craftingWidth; ++var5)
        {
            for (int var6 = 0; var6 < craftingHeight; ++var6)
            {
                int var7 = var5 - par2;
                int var8 = var6 - par3;
                ItemStack var9 = null;

                if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight)
                {
                    if (par4)
                    {
                        var9 = this.recipeItems[this.recipeWidth - var7 - 1 + var8 * this.recipeWidth];
                    }
                    else
                    {
                        var9 = this.recipeItems[var7 + var8 * this.recipeWidth];
                    }
                }

                ItemStack var10 = par1InventoryCrafting.getStackInRowAndColumn(var5, var6);

                if (var10 != null || var9 != null)
                {
                    if (var10 == null && var9 != null || var10 != null && var9 == null)
                    {
                        return false;
                    }

                    if (var9 != var10)
                    {
                        return false;
                    }

                    if (var9.getItemDamage() != 32767 && var9.getItemDamage() != var10.getItemDamage())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}

