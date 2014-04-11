package net.acomputerdog.BlazeLoader.api.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.event.NetworkEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.MinecraftServer;

public class ApiNetwork {
	private static void sendCustomPayload_do(INetHandler handler, NetworkEventHandler mod, String[] args, byte[] data) {
		if (handler instanceof NetHandlerPlayServer) {
			((NetHandlerPlayServer)handler).sendPacket(getCustomPayloadPacket(mod, data, args));
		} else if (handler instanceof NetHandlerPlayClient) {
			((NetHandlerPlayClient)handler).addToSendQueue(getCustomPayloadPacket(mod, data, args));
		}
	}
	
	private static String getChannel(NetworkEventHandler mod, String[] args) {
		String channel = "BL|" + mod.toString();
		for (String i : args) {
			channel += "|" + i;
		}
		return channel;
	}
	
	/**
	 * Gets a CustomPayload packet that can be handled by this mod
	 * @param sender	Mod sending the packet	
	 * @param data		The data to be sent
	 * @param args		Additional channel information
	 * @return a S3FPacketCustomPayload with a predefined channel and payload
	 */
	public static Packet getCustomPayloadPacket(NetworkEventHandler sender, byte[] data, String... args) {
		return new S3FPacketCustomPayload(getChannel(sender, args), data);
	}
	
	/**
	 * Gets a CustomPayload packet that can be handled by this mod
	 * @param sender	Mod sending the packet	
	 * @param data		The data to be sent
	 * @param args		Additional channel information
	 * @return a S3FPacketCustomPayload with a predefined channel and payload
	 */
	public static Packet getCustomPayloadPacket(NetworkEventHandler sender, ByteBuf data, String... args) {
		return new S3FPacketCustomPayload(getChannel(sender, args), data.array());
	}
	
	public static class Client {
		/**
		 * Sends a custom payload packet to be handled by this mod on the server side.
		 * @param sender	Mod sending the packet
		 * @param data		The data to be sent
		 * @param args		Additional channel information
		 */
		public static void sendCustomPayload(NetworkEventHandler sender, byte[] data, String... args) {
			sendCustomPayload_do(ApiGeneral.theMinecraft.getNetHandler(), sender, args, data);
		}
		
		/**
		 * Sends a custom payload packet to be handled by this mod on the server side.
		 * @param sender	Mod sending the packet
		 * @param data		The data to be sent
		 * @param args		Additional channel information
		 */
		public static void sendCustomPayload(NetworkEventHandler sender, ByteBuf data, String... args) {
			sendCustomPayload(sender, data.array(), args);
		}
		
		/**
		 * Simply sends a packet.
		 * @param p
		 */
		public static void sendPacket(Packet p) {
			ApiGeneral.theMinecraft.getNetHandler().addToSendQueue(p);
		}
	}
	
	public static class Server {
		/**
		 * Sends a custom payload packet to be handled by this mod on the client side.
		 * @param sender	Mod sending the packet
		 * @param reciever	The player to recieve the packet
		 * @param data		The data to be sent
		 * @param args		Additional channel information
		 */
		public static void sendCustomPayload(NetworkEventHandler sender, EntityPlayerMP reciever, byte[] data, String... args) {
			sendCustomPayload_do(reciever.playerNetServerHandler, sender, args, data);
		}
		
		/**
		 * Sends a custom payload packet to be handled by this mod on the client side.
		 * @param sender	Mod sending the packet
		 * @param reciever	The player to recieve the packet
		 * @param data		The data to be sent
		 * @param args		Additional channel information
		 */
		public static void sendCustomPayload(NetworkEventHandler sender, EntityPlayerMP reciever, ByteBuf data, String... args) {
			sendCustomPayload(sender, reciever, data.array(), args);
		}
		
		/**
		 * Sends a packet to all players present on the server
		 * @param p	Packet to send
		 */
	    public static void sendToAllPlayers(Packet p) {
	    	if (MinecraftServer.getServer() != null) {
	    		MinecraftServer.getServer().getConfigurationManager().func_148540_a(p);
	    	}
	    }
	    
		/**
		 * Sends a packet to all players present on the server that are in the specified dimension
		 * @param p	Packet to send
		 * @param dimension	Id of the dimension of the players to recieve this packet
		 */
	    public static void sendToPlayersInDimension(Packet p, int dimension) {
	    	if (MinecraftServer.getServer() != null) {
	    		MinecraftServer.getServer().getConfigurationManager().func_148537_a(p, dimension);
	    	}
	    }
	    
	    /**
	     * Gets a List of players in the server
	     * @return Unmodifiable List of EntityPlayerMP objects
	     */
	    public List<EntityPlayerMP> getPlayerEntities() {
	    	if (MinecraftServer.getServer() != null) {
	    		return Collections.unmodifiableList((List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList);
	    	}
	    	return Collections.unmodifiableList(Collections.EMPTY_LIST);
	    }
	}
}
