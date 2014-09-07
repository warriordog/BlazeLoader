package com.blazeloader.api.direct.server.event;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.direct.base.event.EventHandlerBase;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class EventHandlerServer extends EventHandlerBase {
    public static final List<WorldEventServerHandler> worldEventHandlers = new ArrayList<WorldEventServerHandler>();
    public static final List<PlayerEventServerHandler> playerEventHandlers = new ArrayList<PlayerEventServerHandler>();

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

    public static void eventPlayerLoggedIn(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventServerHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerLogin(manager, player);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventPlayerLoggedOut(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventServerHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerLogout(manager, player);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventRespawnPlayer(EventInfo<ServerConfigurationManager> event, EntityPlayerMP oldPlayer, int dimension, boolean didWin) {
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventServerHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerRespawn(manager, oldPlayer, dimension, !didWin);
        }
        BLMain.currActiveMod = prevMod;
    }


    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        BLMod prevMod = BLMain.currActiveMod;
        boolean allow = isAllowed;
        for (PlayerEventServerHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            allow = mod.eventMPPlayerLoginAttempt(username, isAllowed);
        }
        BLMain.currActiveMod = prevMod;
        return allow;
    }
}
