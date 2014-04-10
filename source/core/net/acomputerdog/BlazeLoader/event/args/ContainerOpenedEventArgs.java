package net.acomputerdog.BlazeLoader.event.args;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.MathHelper;

public class ContainerOpenedEventArgs {
	public final boolean locked;
	public final String invName;
	public final int slotsCount;
	
	public final int posX;
	public final int posY;
	public final int posZ;
	
	public ContainerOpenedEventArgs(EntityPlayer player, S2DPacketOpenWindow packet) {
		locked = packet.func_148900_g();
		invName = packet.func_148902_e().split(":?:")[1];
		slotsCount = packet.func_148898_f();
		
		posX = MathHelper.floor_double(player.posX);
		posY = MathHelper.floor_double(player.posY);
		posZ = MathHelper.floor_double(player.posZ);
	}
}
