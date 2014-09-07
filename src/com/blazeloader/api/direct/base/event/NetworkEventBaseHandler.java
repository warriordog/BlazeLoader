package com.blazeloader.api.direct.base.event;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.network.Packet;

public interface NetworkEventBaseHandler extends BLMod {
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
