package com.blazeloader.event.handlers;

import com.blazeloader.api.entity.EntityPropertyManager;
import com.blazeloader.api.world.UnpopulatedChunksQ;
import com.blazeloader.event.listeners.*;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedPlayerList;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Side-independent event handler
 */
public class EventHandler {
	/**
	 * Used where there is a chance a mod can call the method that triggers the event.
	 * Added to prevent infinite recursion inside an event.
	 */
	protected static boolean isInEvent = false;
	
    public static final HandlerList<StartupListener> modEventHandlers = new HandlerList<StartupListener>(StartupListener.class);
    public static final HandlerList<InventoryListener> inventoryEventHandlers = new HandlerList<InventoryListener>(InventoryListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<TickListener> tickEventHandlers = new HandlerList<TickListener>(TickListener.class);
    public static final HandlerList<WorldListener> worldEventHandlers = new HandlerList<WorldListener>(WorldListener.class);
    public static final HandlerList<PlayerListener> playerEventHandlers = new HandlerList<PlayerListener>(PlayerListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<ChunkListener> chunkEventHandlers = new HandlerList<ChunkListener>(ChunkListener.class);
    public static final HandlerList<EntityConstructingListener> entityEventHandlers = new HandlerList<EntityConstructingListener>(EntityConstructingListener.class);

    public static void eventTick() {
        tickEventHandlers.all().onTick();
    }

    public static void eventStart() {
        modEventHandlers.all().start();
    }

    public static void eventEnd() {
        modEventHandlers.all().stop();
    }
    
    //TODO: Check if these are still needed
    public static void overrideTickBlocksAndAmbiance(WorldServer world) {
        worldEventHandlers.all().onBlocksAndAmbianceTicked(world);
    }

    public static void overrideTickServerWorld(WorldServer world) {
        worldEventHandlers.all().onServerTick(world);
    }
    //
    
    public static void eventPlayerLoggedIn(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().onPlayerLoginMP(event.getSource(), player);
    }

    public static void eventPlayerLoggedOut(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().onPlayerLogoutMP(event.getSource(), player);
    }

    public static <ReturnType> void eventRecreatePlayerEntity(ReturnEventInfo<IntegratedPlayerList, ReturnType> event, EntityPlayerMP oldPlayer, int dimension, boolean didWin) {
        playerEventHandlers.all().onPlayerRespawnMP(event.getSource(), oldPlayer, dimension, !didWin);
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        boolean allow = isAllowed;
        for (PlayerListener mod : playerEventHandlers) {
            allow = mod.onPlayerTryLoginMP(username, isAllowed);
        }
        return allow;
    }

    public static void eventOnChunkLoad(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        if (!chunk.isTerrainPopulated()) {
            UnpopulatedChunksQ.instance().push(chunk);
        }
        chunkEventHandlers.all().onChunkLoad(chunk);
    }

    public static void eventOnChunkUnload(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        UnpopulatedChunksQ.instance().pop(chunk);
        chunkEventHandlers.all().onChunkUnload(chunk);
    }
    
    public static void initEntity(EventInfo<Entity> event, World w) {
    	entityEventHandlers.all().onEntityConstructed(event.getSource());
    	EntityPropertyManager.entityinit(event.getSource());
    }
    
    public static void eventCollideWithPlayer(EventInfo<EntityPlayer> event, Entity entity) {
    	if (playerEventHandlers.size() > 0 && !playerEventHandlers.all().onEntityCollideWithPlayer(entity, event.getSource())) {
    		event.cancel();
    	}
    }
    
    public static void eventChangeCurrentItem(EventInfo<InventoryPlayer> event, int increment) {
    	if (inventoryEventHandlers.size() > 0) {
	    	InventoryPlayer inventory = event.getSource();
	    	int newIndex = event.getSource().currentItem + (increment > 0 ? 1 : increment < 0 ? -1 : 0);
	    	if (newIndex > -1) {
	    		for (newIndex -= increment; newIndex < 0; newIndex += InventoryPlayer.getHotbarSize()) {
	                ;
	            }
	    		
	    		newIndex = newIndex % InventoryPlayer.getHotbarSize();
		    	
				if (!inventoryEventHandlers.all().onSlotSelectionChanged(inventory.player, inventory.getStackInSlot(newIndex), newIndex)) {
					event.cancel();
				}
	    	}
    	}
    }
    
    public static void eventOnItemPickup(EventInfo<EntityLivingBase> event, Entity itemEntity, int amount) {
    	inventoryEventHandlers.all().onItemPickup(event.getSource(), itemEntity, amount);
    }
    
    public static void eventUpdateEquipmentIfNeeded(EventInfo<EntityLiving> event, EntityItem entityItem) {
    	if (inventoryEventHandlers.size() > 0) {
	    	ItemStack pickedUp = entityItem.getEntityItem();
	    	InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(pickedUp);
	    	if (!inventoryEventHandlers.all().onEntityEquipItem(event.getSource(), entityItem, 1)) {
	    		event.cancel();
	    	} else {
	    		if (!pickedUp.equals(args.getItemStack())) {
	    			entityItem.setEntityItemStack(args.getItemStack());
	    		}
	    	}
    	}
    }
    
    public static void eventEntityDropItem(ReturnEventInfo<Entity, EntityItem> event, ItemStack droppedItem, float yOffset) {
    	if (!isInEvent && droppedItem != null && droppedItem.stackSize > 0) {
    		if (inventoryEventHandlers.size() > 0) {
	    		Entity entity = event.getSource();
	    		InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(droppedItem);
		    	if (!inventoryEventHandlers.all().onDropItem(entity, false, false, args)) {
		    		event.setReturnValue(null);
		    		event.cancel();
		    	} else {
		    		if (!droppedItem.equals(args.getItemStack())) {
		    			isInEvent = true;
		    			event.setReturnValue(entity.entityDropItem(args.getItemStack(), yOffset));
		    			isInEvent = false;
		    			event.cancel();
		    		}
		    	}
	    	}
    	}
    }
    
    //Because otherwise items held in an inventory get deleted when a mod cancels the drop.
    private static ItemStack savedHeldItemStack;
    public static void eventDropItem(ReturnEventInfo<EntityPlayer, EntityItem> event, ItemStack droppedItem, boolean dropAround, boolean traceItem) {
    	savedHeldItemStack = null;
    	if (!isInEvent && droppedItem != null && droppedItem.stackSize > 0) {
    		if (inventoryEventHandlers.size() > 0) {
	    		EntityPlayer player = event.getSource();
	    		ItemStack held = player.inventory.getItemStack();
	    		InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(droppedItem);
		    	if (!inventoryEventHandlers.all().onDropItem(player, dropAround, traceItem, args)) {
		    		event.setReturnValue(null);
		    		if (held != null) {
			    		if (!held.equals(droppedItem)) {
			    			held.stackSize += droppedItem.stackSize;
			    		} else {
			    			savedHeldItemStack = held;
			    		}
		    		}
		    		event.cancel();
		    	} else {
		    		if (!droppedItem.equals(args.getItemStack())) {
		    			isInEvent = true;
		    			event.setReturnValue(player.dropItem(args.getItemStack(), dropAround, traceItem));
		    			isInEvent = false;
		    			event.cancel();
		    		}
		    	}
    		}
    	}
    }
    
    //Now we then have to put the item back
    public static void eventSlotClick(ReturnEventInfo<Container, ItemStack> event, int slotId, int clickedButton, int mode, EntityPlayer player) {
    	if (savedHeldItemStack != null) {
	    	if (event.getReturnValue() == null && player.inventory.getItemStack() == null) {
	    		player.inventory.setItemStack(savedHeldItemStack);
	    		savedHeldItemStack = null;
	    	}
    	}
    }
    
    public static void eventDropOneItem(ReturnEventInfo<EntityPlayer, EntityItem> event, boolean dropAll) {
    	if (inventoryEventHandlers.size() > 0) {
	    	EntityPlayer player = event.getSource();
	    	ItemStack droppedItem = player.inventory.getCurrentItem().copy();
	    	if (droppedItem != null) {
		    	if (!dropAll) droppedItem.stackSize = 1;
		    	InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(droppedItem);
		    	if (!inventoryEventHandlers.all().onDropOneItem(player, dropAll, args)) {
		    		event.setReturnValue(null);
		    		event.cancel();
		    	}
		    	if (!droppedItem.equals(args.getItemStack())) {
		    		event.setReturnValue(player.dropItem(args.getItemStack(), false, true));
		    		player.inventory.decrStackSize(player.inventory.currentItem, droppedItem.stackSize);
		    		event.cancel();
		    	}
	    	}
    	}
    }
}
