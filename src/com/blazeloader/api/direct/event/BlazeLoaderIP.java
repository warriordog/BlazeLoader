package com.blazeloader.api.direct.event;

import com.blazeloader.api.core.mod.BLMod;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

/**
 * BlazeLoader InterfaceProvider
 */
public class BlazeLoaderIP implements InterfaceProvider {
    public static final BlazeLoaderIP instance = new BlazeLoaderIP();

    private BlazeLoaderIP() {
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
        delegate.registerInterface(ClientEventHandler.class);
        delegate.registerInterface(GenericEventHandler.class);
        delegate.registerInterface(InventoryEventHandler.class);
        delegate.registerInterface(NetworkEventHandler.class);
        delegate.registerInterface(OverrideEventHandler.class);
        delegate.registerInterface(PlayerEventHandler.class);
        delegate.registerInterface(ProfilerEventHandler.class);
        delegate.registerInterface(TickEventHandler.class);
        delegate.registerInterface(WorldEventHandler.class);
    }

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    @Override
    public void initProvider() {

    }

    public void addBlockEvent(BlockEventHandler e) {
        EventHandler.blockEventHandlers.add(e);
    }

    public void addClientEvent(ClientEventHandler e) {
        EventHandler.clientEventHandlers.add(e);
    }

    public void addGenericEvent(GenericEventHandler e) {
        EventHandler.genericEventHandlers.add(e);
    }

    public void addInventoryEvent(InventoryEventHandler e) {
        EventHandler.inventoryEventHandlers.add(e);
    }

    public void addNetworkEvent(NetworkEventHandler e) {
        EventHandler.networkEventHandlers.add(e);
    }

    public void addOverrideEvent(OverrideEventHandler e) {
        EventHandler.overrideEventHandlers.add(e);
    }

    public void addPlayerEvent(PlayerEventHandler e) {
        EventHandler.playerEventHandlers.add(e);
    }

    public void addProfilerEvent(ProfilerEventHandler e) {
        EventHandler.profilerEventHandlers.add(e);
    }

    public void addTickEvent(TickEventHandler e) {
        EventHandler.tickEventHandlers.add(e);
    }

    public void addWorldEventHandler(WorldEventHandler e) {
        EventHandler.worldEventHandlers.add(e);
    }
}
