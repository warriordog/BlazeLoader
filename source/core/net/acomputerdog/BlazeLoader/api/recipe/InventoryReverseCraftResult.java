package net.acomputerdog.BlazeLoader.api.recipe;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;

/**
 * Used by reverse crafting tables to detect changes to their result slot
 * and output a result to their crafting grid
 */
public class InventoryReverseCraftResult extends InventoryCraftResult {
    private Container eventHandler;

    public InventoryReverseCraftResult(Container container) {
        eventHandler = container;
    }

    public void setInventorySlotContentsSafe(int slotIndex, ItemStack contents) {
        super.setInventorySlotContents(slotIndex, contents);
    }

    public void setInventorySlotContents(int slotIndex, ItemStack contents) {
        super.setInventorySlotContents(slotIndex, contents);
        this.eventHandler.onCraftMatrixChanged(this);
    }
}
