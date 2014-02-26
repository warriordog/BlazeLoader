package net.acomputerdog.BlazeLoader.api.gui;

import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class ContainerOpenedEventArgs {
	public final boolean locked;
	public final String invName;
	public final int slotsCount;
	
	public ContainerOpenedEventArgs(S2DPacketOpenWindow packet) {
		locked = packet.func_148900_g();
		invName = packet.func_148902_e().split(":?:")[1];
		slotsCount = packet.func_148898_f();
	}
}
