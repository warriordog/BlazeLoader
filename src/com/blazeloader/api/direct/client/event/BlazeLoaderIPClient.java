package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.direct.base.event.ModEventBaseHandler;
import com.blazeloader.api.direct.base.event.TickEventBaseHandler;
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
        delegate.registerInterface(BlockEventHandler.class);
        delegate.registerInterface(GuiEventClientHandler.class);
        delegate.registerInterface(ModEventBaseHandler.class);
        delegate.registerInterface(InventoryEventHandler.class);
        delegate.registerInterface(OverrideEventClientHandler.class);
        delegate.registerInterface(PlayerEventClientHandler.class);
        delegate.registerInterface(ProfilerEventClientHandler.class);
        delegate.registerInterface(TickEventBaseHandler.class);
        delegate.registerInterface(WorldEventClientHandler.class);
    }

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    @Override
    public void initProvider() {

    }

    public void addBlockEvent(BlockEventHandler e) {
        EventHandlerClient.blockEventHandlers.add(e);
    }

    public void addClientEvent(GuiEventClientHandler e) {
        EventHandlerClient.clientEventHandlers.add(e);
    }

    public void addGenericEvent(ModEventBaseHandler e) {
        EventHandlerClient.modEventHandlers.add(e);
    }

    public void addInventoryEvent(InventoryEventHandler e) {
        EventHandlerClient.inventoryEventHandlers.add(e);
    }

    public void addOverrideEvent(OverrideEventClientHandler e) {
        EventHandlerClient.overrideEventHandlers.add(e);
    }

    public void addPlayerEvent(PlayerEventClientHandler e) {
        EventHandlerClient.playerEventHandlers.add(e);
    }

    public void addProfilerEvent(ProfilerEventClientHandler e) {
        EventHandlerClient.profilerEventHandlers.add(e);
    }

    public void addTickEvent(TickEventBaseHandler e) {
        EventHandlerClient.tickEventHandlers.add(e);
    }

    public void addWorldEventHandler(WorldEventClientHandler e) {
        EventHandlerClient.worldEventHandlers.add(e);
    }
}
