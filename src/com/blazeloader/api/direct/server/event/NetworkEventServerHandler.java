package com.blazeloader.api.direct.server.event;

import com.blazeloader.api.direct.base.event.NetworkEventBaseHandler;
import net.minecraft.network.NetHandlerPlayServer;

public interface NetworkEventServerHandler extends NetworkEventBaseHandler {

    /**
     * Triggered when the server recieves a Custom packet sent in the name of this mod
     *
     * @param args Packet arguments
     */
    public void eventServerRecieveCustomPayload(NetHandlerPlayServer handler, NetworkEventBaseHandler.PacketEventArgs args);
}
