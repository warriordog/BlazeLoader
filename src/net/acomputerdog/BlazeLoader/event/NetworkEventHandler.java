package net.acomputerdog.BlazeLoader.event;

import com.mumfrey.liteloader.LiteMod;
import net.acomputerdog.BlazeLoader.event.args.PacketEventArgs;
import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public interface NetworkEventHandler extends BLMod {

    /**
     * Triggered when the client recieves a Custom packet sent in the name of this mod
     *
     * @param args
     */
    public void eventClientRecieveCustomPayload(NetHandlerPlayClient handler, PacketEventArgs args);

    /**
     * Triggered when the server recieves a Custom packet sent in the name of this mod
     *
     * @param args
     */
    public void eventServerRecieveCustomPayload(NetHandlerPlayServer handler, PacketEventArgs args);
}
