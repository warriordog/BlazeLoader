package net.acomputerdog.BlazeLoader.event;

import java.util.List;

import net.acomputerdog.BlazeLoader.event.args.PacketEventArgs;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public interface NetworkEventHandler {
	
	/**
	 * Triggered when the client recieves a Custom packet sent in the name of this mod
	 * @param args
	 */
	public void eventClientRecieveCustomPayload(NetHandlerPlayClient handler, PacketEventArgs<S3FPacketCustomPayload> args);
	
	/**
	 * Triggered when the server recieves a Custom packet sent in the name of this mod
	 * @param args
	 */
	public void eventServerRecieveCustomPayload(NetHandlerPlayServer handler, PacketEventArgs<C17PacketCustomPayload> args);
}
