package com.blazeloader.bl.main;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.handlers.EventHandler;
import com.blazeloader.event.listeners.ChunkListener;
import com.blazeloader.event.listeners.EntityConstructingListener;
import com.blazeloader.event.listeners.InventoryListener;
import com.blazeloader.event.listeners.StartupListener;
import com.blazeloader.event.listeners.PlayerListener;
import com.blazeloader.event.listeners.TickListener;
import com.blazeloader.event.listeners.WorldListener;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

public class BlazeLoaderInterfaceProvider implements InterfaceProvider {
	
	private static BlazeLoaderInterfaceProvider instance;
	
	protected BlazeLoaderInterfaceProvider() {
		instance = this;
	}
	
	protected static final BlazeLoaderInterfaceProvider instance() {
		return instance;
	}
	
    @Override
    public Class<? extends Listener> getListenerBaseType() {
        return BLMod.class;
    }
    
	@Override
	public void registerInterfaces(InterfaceRegistrationDelegate delegate) {
		delegate.registerInterface(StartupListener.class);
		delegate.registerInterface(TickListener.class);
		delegate.registerInterface(WorldListener.class);
		delegate.registerInterface(PlayerListener.class);
		delegate.registerInterface(ChunkListener.class);
		delegate.registerInterface(EntityConstructingListener.class);
		delegate.registerInterface(InventoryListener.class);
	}
	
    @Override
    public void initProvider() {

    }
    
    public void addGenericEvent(StartupListener e) {
        EventHandler.modEventHandlers.add(e);
    }
    
    public void addTickEvent(TickListener e) {
        EventHandler.tickEventHandlers.add(e);
    }
    
    public void addWorldEvent(WorldListener e) {
    	EventHandler.worldEventHandlers.add(e);
    }
    
    public void addPlayerEvent(PlayerListener e) {
    	EventHandler.playerEventHandlers.add(e);
    }
    
    public void addInventoryEvent(InventoryListener e) {
        EventHandler.inventoryEventHandlers.add(e);
    }
    
    public void addChunkEvent(ChunkListener e) {
    	EventHandler.chunkEventHandlers.add(e);
    }
    
    public void addEntityConstructingEvent(EntityConstructingListener e) {
    	EventHandler.entityEventHandlers.add(e);
    }
}
