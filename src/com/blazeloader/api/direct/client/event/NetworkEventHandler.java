package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.direct.base.event.NetworkEventBaseHandler;
import net.minecraft.client.network.NetHandlerPlayClient;

/**
 * Interface for mods that handle network events
 */
public interface NetworkEventHandler extends NetworkEventBaseHandler {

    /**
     * Triggered when the client recieves a Custom packet sent in the name of this mod
     *
     * @param args Packet arguments
     */
    public void eventClientRecieveCustomPayload(NetHandlerPlayClient handler, PacketEventArgs args);

}
