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
	 * Called when a player presses the Q key.
	 * <p>
	 * Occurs before onDropItem
	 * 
	 * @param player		The player
	 * @param dropAll		True if an entire stack is being dropped
	 * @param args			Inventory arguments, contains the item for this event
	 * @return True if the item can be dropped, otherwise false to cancel the event.
	 */
	public boolean onDropOneItem(EntityPlayer player, boolean dropAll, InventoryEventArgs args);
	
	/**
	 * Called when an entity tries to drop an item.
	 * 
	 * @param droppingEntity	The entity trying to drop an item
	 * @param itemDropped		The itemstack being dropped
	 * @param args			Inventory arguments, contains the item for this event
	 * @return True if the item can be dropped, otherwise false to cancel the event.
	 */
	public boolean onDropItem(Entity droppingEntity, boolean dropAround, boolean traceItems, InventoryEventArgs args);
	
	/**
	 * Called after an entity picks up an item.
	 * <p>
	 * If you wish to change the default pickup mechanic you will have to use PlayerListener.onEntityCollideWithPlayer to recieve the event more early.
	 * 
	 * @param entity		The entity picking up the item
	 * @param itemEntity	The entity being picked up
	 * @param amount		The number of items being picked up from this entity
	 */
	public void onItemPickup(Entity entity, Entity itemEntity, int amount);
	
	/**
	 * Called when an entity is about to pickup an item to equip as armour
	 * 
	 * @param entity		The entity picking up the item
	 * @param itemEntity	The entity being picked up
	 * @param amount		The number of items being picked up from this entity
	 * @return True if the item can be picked up, otherwise false to cancel the event.
	 */
	public boolean onEntityEquipItem(Entity entity, Entity itemEntity, int amount);
	
	/**
	 * Occurs when the player changes the selected slot in their hotbar
	 * @param player			The player
	 * @param item				The item placed in teh current slot
	 * @param selectedSlot		The slot being selected.
	 */
	public void onSlotSelectionChanged(EntityPlayer player, ItemStack item, int selectedSlot);
	
	public static class InventoryEventArgs {
		
    	private ItemStack theItem;
    	private boolean stackChanged = false;
    	
        public InventoryEventArgs(ItemStack stack) {
        	theItem = stack;
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
