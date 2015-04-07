package com.blazeloader.event.handlers;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.ChunkListener;
import com.blazeloader.event.listeners.EntityConstructingListener;
import com.blazeloader.event.listeners.ModEventListener;
import com.blazeloader.event.listeners.PlayerListener;
import com.blazeloader.event.listeners.TickListener;
import com.blazeloader.event.listeners.WorldListener;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

public class BlazeLoaderIP implements InterfaceProvider {
	
	public static final InterfaceProvider instance = new BlazeLoaderIP();
	
	protected BlazeLoaderIP() {}
	
    /**
     * Base type of Listeners which can consume events provided by this provider
     */
    @Override
    public Class<? extends Listener> getListenerBaseType() {
        return BLMod.class;
    }
    
	@Override
	public void registerInterfaces(InterfaceRegistrationDelegate delegate) {
		delegate.registerInterface(ModEventListener.class);
		delegate.registerInterface(TickListener.class);
		delegate.registerInterface(WorldListener.class);
		delegate.registerInterface(PlayerListener.class);
		delegate.registerInterface(ChunkListener.class);
		delegate.registerInterface(EntityConstructingListener.class);
	}

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    @Override
    public void initProvider() {

    }
    
    public void addGenericEvent(ModEventListener e) {
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
    
    public void addChunkEvent(ChunkListener e) {
    	EventHandler.chunkEventHandlers.add(e);
    }
    
    public void addEntityConstructingEvent(EntityConstructingListener e) {
    	EventHandler.entityEventHandlers.add(e);
    }
}
