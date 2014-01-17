package manilla.util.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ReversableShapedRecipe extends BShapedRecipe {
	private boolean anyDirection = true;
	
	public ReversableShapedRecipe(int width, int height, ItemStack[] input, ItemStack output) {
    	super(width, height, input, output);
    }
	
	public void setReverseOnly() {
		anyDirection = false;
	}
	
	public boolean matches(InventoryCrafting craftingInventory, World w) {
		if (anyDirection) {
			return super.matches(craftingInventory, w);
		}
		return false;
	}
	
	public ItemStack[] getRecipeInput() {
		return this.recipeItems.clone();
	}
}
