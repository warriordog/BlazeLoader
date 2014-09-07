package com.blazeloader.api.api.network;

import com.blazeloader.api.api.general.ApiGeneral;
import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.direct.client.event.NetworkEventClientHandler;
import com.blazeloader.api.direct.server.event.NetworkEventServerHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class ApiNetwork {

    private static String getChannel(BLMod mod, String[] args) {
        String channel = "BL|" + mod.getModId();
        for (String i : args) {
            channel += "|" + i;
        }
        return channel;
    }

    /**
     * With this class you are the client sending packets to the server.
     * The server can either be the intergrated server or a remote server.
     */
    public static class Client {

        /**
         * Gets a CustomPayload packet that can be handled by this mod when received on the server side
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         * @return a S3FPacketCustomPayload with a predefined channel and payload
         */
        public static Packet getCustomPayloadPacketClient(NetworkEventClientHandler sender, byte[] data, String... args) {
            return new C17PacketCustomPayload(getChannel(sender, args), data);
        }

        /**
         * Gets a CustomPayload packet that can be handled by this mod when received on the server side
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         * @return a S3FPacketCustomPayload with a predefined channel and payload
         */
        public static Packet getCustomPayloadPacketClient(NetworkEventClientHandler sender, ByteBuf data, String... args) {
            return new C17PacketCustomPayload(getChannel(sender, args), data.array());
        }

        /**
         * Sends a custom payload packet to be handled by this mod on the server side.
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         */
        public static void sendCustomPayloadClient(NetworkEventClientHandler sender, byte[] data, String... args) {
            ApiGeneral.theMinecraft.getNetHandler().addToSendQueue(Client.getCustomPayloadPacketClient(sender, data, args));
        }

        /**
         * Sends a custom payload packet to be handled by this mod on the server side.
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         */
        public static void sendCustomPayloadClient(NetworkEventClientHandler sender, ByteBuf data, String... args) {
            sendCustomPayloadClient(sender, data.array(), args);
        }

        /**
         * Simply sends a packet.
         *
         * @param p
         */
        public static void sendPacketClient(Packet p) {
            ApiGeneral.theMinecraft.getNetHandler().addToSendQueue(p);
        }
    }

    /**
     * You are the server sending packets to the client.
     */
    public static class Server {

        /**
         * Gets a CustomPayload packet that can be handled by this mod when received on the client side
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         * @return a S3FPacketCustomPayload with a predefined channel and payload
         */
        public static Packet getCustomPayloadPacketServer(NetworkEventServerHandler sender, byte[] data, String... args) {
            return new S3FPacketCustomPayload(getChannel(sender, args), data);
        }

        /**
         * Gets a CustomPayload packet that can be handled by this mod when received on the client side
         *
         * @param sender Mod sending the packet
         * @param data   The data to be sent
         * @param args   Additional channel information
         * @return a S3FPacketCustomPayload with a predefined channel and payload
         */
        public static Packet getCustomPayloadPacketServer(NetworkEventServerHandler sender, ByteBuf data, String... args) {
            return new S3FPacketCustomPayload(getChannel(sender, args), data.array());
        }

        /**
         * Sends a custom payload packet to be handled by this mod on the client side.
         *
         * @param sender   Mod sending the packet
         * @param reciever The player to recieve the packet
         * @param data     The data to be sent
         * @param args     Additional channel information
         */
        public static void sendCustomPayloadServer(NetworkEventServerHandler sender, EntityPlayerMP reciever, byte[] data, String... args) {
            reciever.playerNetServerHandler.sendPacket(Server.getCustomPayloadPacketServer(sender, data, args));
        }

        /**
         * Sends a custom payload packet to be handled by this mod on the client side.
         *
         * @param sender   Mod sending the packet
         * @param reciever The player to recieve the packet
         * @param data     The data to be sent
         * @param args     Additional channel information
         */
        public static void sendCustomPayloadServer(NetworkEventServerHandler sender, EntityPlayerMP reciever, ByteBuf data, String... args) {
            sendCustomPayloadServer(sender, reciever, data.array(), args);
        }

        /**
         * Sends a packet to all players present on the server
         *
         * @param p Packet to send
         */
        public static void sendToAllPlayersServer(Packet p) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(p);
            }
        }

        /**
         * Sends a packet to all players present on the server that are in the specified dimension
         *
         * @param p         Packet to send
         * @param dimension Id of the dimension of the players to recieve this packet
         */
        public static void sendToPlayersInDimensionServer(Packet p, int dimension) {
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(p, dimension);
            }
        }

        /**
         * Gets a List of players in the server
         *
         * @return Unmodifiable List of EntityPlayerMP objects
         */
        public List<EntityPlayerMP> getPlayerEntitiesServer() {
            if (MinecraftServer.getServer() != null) {
                return Collections.unmodifiableList((List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList);
            }
            return Collections.unmodifiableList(Collections.EMPTY_LIST);
        }
    }
}
