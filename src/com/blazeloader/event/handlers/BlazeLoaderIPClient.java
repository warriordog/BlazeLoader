package com.blazeloader.event.handlers;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.handlers.client.EventHandlerClient;
import com.blazeloader.event.listeners.ModEventListener;
import com.blazeloader.event.listeners.TickListener;
import com.blazeloader.event.listeners.client.ClientBlockListener;
import com.blazeloader.event.listeners.client.GuiListener;
import com.blazeloader.event.listeners.client.InventoryListener;
import com.blazeloader.event.listeners.client.OverrideListener;
import com.blazeloader.event.listeners.client.ClientPlayerListener;
import com.blazeloader.event.listeners.client.ProfilerListener;
import com.blazeloader.event.listeners.client.ClientWorldListener;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

/**
 * BlazeLoader InterfaceProvider
 */
public class BlazeLoaderIPClient extends BlazeLoaderIP {
    public static final InterfaceProvider instance = new BlazeLoaderIPClient();
    
    protected BlazeLoaderIPClient() {}
    
    /**
     * The provider should call back against the supplied delegate in order to advertise the interfaces
     * it provides.
     *
     * @param delegate Interface registration delegate
     */
    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate) {
        delegate.registerInterface(ClientBlockListener.class);
        delegate.registerInterface(GuiListener.class);
        delegate.registerInterface(InventoryListener.class);
        delegate.registerInterface(OverrideListener.class);
        delegate.registerInterface(ClientPlayerListener.class);
        delegate.registerInterface(ProfilerListener.class);
        delegate.registerInterface(ClientWorldListener.class);
    }

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    @Override
    public void initProvider() {

    }

    public void addBlockEvent(ClientBlockListener e) {
        EventHandlerClient.blockEventClients.add(e);
    }

    public void addClientEvent(GuiListener e) {
        EventHandlerClient.guiEventClients.add(e);
    }

    public void addInventoryEvent(InventoryListener e) {
        EventHandlerClient.inventoryEventClients.add(e);
    }

    public void addOverrideEvent(OverrideListener e) {
        EventHandlerClient.overrideEventClients.add(e);
    }

    public void addPlayerEvent(ClientPlayerListener e) {
        EventHandlerClient.playerEventClients.add(e);
    }

    public void addProfilerEvent(ProfilerListener e) {
        EventHandlerClient.profilerEventClients.add(e);
    }

    public void addWorldEventHandler(ClientWorldListener e) {
        EventHandlerClient.worldEventClients.add(e);
    }
}
