package net.acomputerdog.BlazeLoader.event.args;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEventArgs<V extends Packet> {
	
	public final INetHandler netHandler;
	public final V packet;
	public final String channel;
	public final String[] args;
	
	public PacketEventArgs(INetHandler handler, V pack, String packetId) {
		netHandler = handler;
		packet = pack;
		
		String[] splitId = packetId.split("|");
		int index = 1;
		if (splitId.length > 1) {
			channel = splitId[1];
			if (splitId.length > 2) {
				args = new String[splitId.length - 2];
				for (int i = 2; i < splitId.length; i++) {
					args[i - 2] = splitId[i];
				}
			} else {
				args = new String[0];
			}
		} else {
			channel = splitId[0];
			args = new String[0];
		}
	}
}
