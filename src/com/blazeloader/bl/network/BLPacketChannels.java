package com.blazeloader.bl.network;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import com.blazeloader.bl.main.BLMain;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.core.CommonPluginChannelListener;
import com.mumfrey.liteloader.core.ServerPluginChannels;
import com.mumfrey.liteloader.core.PluginChannels.ChannelPolicy;

//FIXME: Figure out how to add a Plugin Channel for a LiteAPI. Seriously, how does this work?
public class BLPacketChannels implements CommonPluginChannelListener {
    public static void sendPacket(EntityPlayerMP player, Packet p, Channel channel) {
    	PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
    	try {
			p.writePacketData(buf);
			ServerPluginChannels.sendMessage(player, channel.getChannelIdentifier(), buf, ChannelPolicy.DISPATCH_ALWAYS);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public String getName() {
		return BLMain.instance().getPluginChannelName();
	}

	@Override
	public List<String> getChannels() {
		List<String> result = new ArrayList<String>();
		for (Channel i : Channel.values()) {
			result.add(i.getChannelIdentifier());
		}
		return ImmutableList.copyOf(result);
	}
}
