package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;

/**
 * Interface for mods that handle network events
 */
public interface NetworkEventHandler extends BLMod {

    /**
     * Triggered when the client recieves a Custom packet sent in the name of this mod
     *
     * @param args Packet arguments
     */
    public void eventClientRecieveCustomPayload(NetHandlerPlayClient handler, PacketEventArgs args);

    /**
     * Triggered when the server recieves a Custom packet sent in the name of this mod
     *
     * @param args Packet arguments
     */
    public void eventServerRecieveCustomPayload(NetHandlerPlayServer handler, PacketEventArgs args);

    /**
     * Contains args for a packet event
     */
    public static class PacketEventArgs {

        public final Packet packet;
        public final String channel;
        public final String[] args;

        public PacketEventArgs(Packet pack, String packetId) {
            packet = pack;

            String[] splitId = packetId.split("\\|");
            if (splitId.length > 1) {
                channel = splitId[1];
                if (splitId.length > 2) {
                    args = new String[splitId.length - 2];
                    System.arraycopy(splitId, 2, args, 0, splitId.length - 2);
                } else {
                    args = new String[0];
                }
            } else {
                channel = splitId[0];
                args = new String[0];
            }
        }
    }
}
