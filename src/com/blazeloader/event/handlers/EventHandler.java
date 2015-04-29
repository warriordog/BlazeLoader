package com.blazeloader.event.handlers;

import com.blazeloader.api.entity.EntityPropertyManager;
import com.blazeloader.api.world.UnpopulatedChunksQ;
import com.blazeloader.event.listeners.*;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
    public static final HandlerList<ModEventListener> modEventHandlers = new HandlerList<ModEventListener>(ModEventListener.class);
    public static final HandlerList<InventoryListener> inventoryHandlers = new HandlerList<InventoryListener>(InventoryListener.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<TickListener> tickEventHandlers = new HandlerList<TickListener>(TickListener.class);
    public static final HandlerList<WorldListener> worldEventHandlers = new HandlerList<WorldListener>(WorldListener.class);
    public static final HandlerList<PlayerListener> playerEventHandlers = new HandlerList<PlayerListener>(PlayerListener.class);
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
    
    public static void eventItemPickup(EventInfo<EntityLivingBase> event, Entity entityItem, int stackSize) {
    	EntityLivingBase entity = event.getSource();
    	if (entityItem.isDead && !entity.worldObj.isRemote && entityItem instanceof EntityItem) {
    		ItemStack item = ((EntityItem)entityItem).getEntityItem();
    		InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(item, false, false);
	    	if (!inventoryHandlers.all().onItemPickup(event.getSource(), args)) {
	    		event.cancel();
	    	}
    	}
    }
    
    public static void eventDropItem(ReturnEventInfo<EntityPlayer, EntityItem> event, ItemStack droppedItem, boolean dropAround, boolean traceItem) {
    	if (droppedItem != null && droppedItem.stackSize > 0) {
    		InventoryListener.InventoryEventArgs args = new InventoryListener.InventoryEventArgs(droppedItem, dropAround, traceItem);
	    	if (!inventoryHandlers.all().onDropItem(event.getSource(), args)) {
	    		droppedItem.stackSize = 0;
	    	} else {
	    		if (!args.getItemStack().equals(droppedItem)) {
	    			droppedItem = args.getItemStack();
	    		}
	    	}
    	}
    }
}
