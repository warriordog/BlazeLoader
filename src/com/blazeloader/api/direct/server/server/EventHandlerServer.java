package com.blazeloader.api.direct.server.server;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.direct.base.event.EventHandlerBase;
import com.blazeloader.api.direct.client.event.NetworkEventHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class EventHandlerServer extends EventHandlerBase {
    public static final List<NetworkEventServerHandler> networkEventHandlers = new ArrayList<NetworkEventServerHandler>();
    public static final List<WorldEventServerHandler> worldEventHandlers = new ArrayList<WorldEventServerHandler>();

    public static void eventTickBlocksAndAmbiance(WorldServer server) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventServerHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickBlocksAndAmbiance(server);
        }
        BLMain.currActiveMod = prevMod;
    }


    public static void eventTickServerWorld(WorldServer world) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventServerHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickServerWorld(world);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventServerReceiveCustomPayload(NetHandlerPlayServer handler, C17PacketCustomPayload packet) {
        String packetIdentifier = packet.func_149559_c();
        if (packetIdentifier != null) {
            if (packetIdentifier.indexOf("BL|") == 0) {
                NetworkEventHandler.PacketEventArgs args = new NetworkEventHandler.PacketEventArgs(packet, packetIdentifier);
                for (NetworkEventServerHandler mod : networkEventHandlers) {
                    if (mod.toString().equals(args.channel)) {
                        mod.eventServerRecieveCustomPayload(handler, args);
                    }
                }
            }
        }
    }
}
