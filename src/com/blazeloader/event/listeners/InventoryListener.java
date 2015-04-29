package com.blazeloader.event.listeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that handle inventory events
 * 
 * TODO: Needs implementing
 */
public interface InventoryListener extends BLMod {
	
	/**
	 * Called when an entity attempts to pick up an item.
	 * 
	 * @param entity	The entity picking up the item
	 * @param theItem	The itemstack being picked up
	 * @return True if the item can be picked up, otherwise false to block it.
	 */
	public boolean onItemPickup(Entity entity, InventoryEventArgs args);
	
	/**
	 * Called when an entity tries to drop an item.
	 * 
	 * @param droppingEntity	The entity trying to drop an item
	 * @param itemDropped		The itemstack being dropped
	 * @return True if the item can be dropped, otherwise false to block it.
	 */
	public boolean onDropItem(Entity droppingEntity, InventoryEventArgs args);
	
	/**
	 * Occurs when the player changes the selected slot in their hotbar
	 * @param player			The player
	 * @param selectedSlot		The slot being selected.
	 */
	public void onSlotSelectionChange(EntityPlayer player, int selectedSlot);
	
	public static class InventoryEventArgs {
		public final boolean dropAround;
		public final boolean traceItem;
		
    	private ItemStack theItem;
    	private boolean stackChanged = false;

        public InventoryEventArgs(ItemStack item, boolean dropA, boolean trace) {
            theItem = item;
            dropAround = dropA;
            traceItem = trace;
        }
        
        public void setItemStack(ItemStack stack) {
        	theItem = stack;
        	stackChanged = true;
        }
        
        public ItemStack getItemStack() {
        	return theItem;
        }
    }
}
