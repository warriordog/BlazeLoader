package com.blazeloader.event.handlers.client;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.handlers.ModEventHandler;
import com.blazeloader.event.handlers.TickEventHandler;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

/**
 * BlazeLoader InterfaceProvider
 */
public class BlazeLoaderIPClient implements InterfaceProvider {
    public static final BlazeLoaderIPClient instance = new BlazeLoaderIPClient();

    private BlazeLoaderIPClient() {
    }

    /**
     * Base type of Listeners which can consume events provided by this provider
     */
    @Override
    public Class<? extends Listener> getListenerBaseType() {
        return BLMod.class;
    }

    /**
     * The provider should call back against the supplied delegate in order to advertise the interfaces
     * it provides.
     *
     * @param delegate Interface registration delegate
     */
    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate) {
        delegate.registerInterface(BlockEventClientHandler.class);
        delegate.registerInterface(GuiEventClientHandler.class);
        delegate.registerInterface(ModEventHandler.class);
        delegate.registerInterface(InventoryEventClientHandler.class);
        delegate.registerInterface(OverrideEventClientHandler.class);
        delegate.registerInterface(PlayerEventClientHandler.class);
        delegate.registerInterface(ProfilerEventClientHandler.class);
        delegate.registerInterface(TickEventHandler.class);
        delegate.registerInterface(WorldEventClientHandler.class);
    }

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    @Override
    public void initProvider() {

    }

    public void addBlockEvent(BlockEventClientHandler e) {
        EventHandlerClient.blockEventClients.add(e);
    }

    public void addClientEvent(GuiEventClientHandler e) {
        EventHandlerClient.guiEventClients.add(e);
    }

    public void addGenericEvent(ModEventHandler e) {
        EventHandlerClient.modEventHandlers.add(e);
    }

    public void addInventoryEvent(InventoryEventClientHandler e) {
        EventHandlerClient.inventoryEventClients.add(e);
    }

    public void addOverrideEvent(OverrideEventClientHandler e) {
        EventHandlerClient.overrideEventClients.add(e);
    }

    public void addPlayerEvent(PlayerEventClientHandler e) {
        EventHandlerClient.playerEventClients.add(e);
    }

    public void addProfilerEvent(ProfilerEventClientHandler e) {
        EventHandlerClient.profilerEventClients.add(e);
    }

    public void addTickEvent(TickEventHandler e) {
        EventHandlerClient.tickEventHandlers.add(e);
    }

    public void addWorldEventHandler(WorldEventClientHandler e) {
        EventHandlerClient.worldEventClients.add(e);
    }
}
