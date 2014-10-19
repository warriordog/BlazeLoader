package com.blazeloader.api.event;

import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

public class EventHandlerServer extends EventHandlerBase {
    public static final HandlerList<WorldEventServerHandler> worldEventHandlers = new HandlerList<WorldEventServerHandler>(WorldEventServerHandler.class);
    public static final HandlerList<PlayerEventServerHandler> playerEventHandlers = new HandlerList<PlayerEventServerHandler>(PlayerEventServerHandler.class);

    public static void eventTickBlocksAndAmbiance(WorldServer server) {
        worldEventHandlers.all().eventTickBlocksAndAmbiance(server);
    }


    public static void eventTickServerWorld(WorldServer world) {
        worldEventHandlers.all().eventTickServerWorld(world);
    }

    public static void eventPlayerLoggedIn(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().eventMPPlayerLogin(event.getSource(), player);
    }

    public static void eventPlayerLoggedOut(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        playerEventHandlers.all().eventMPPlayerLogout(event.getSource(), player);
    }

    public static void eventRespawnPlayer(EventInfo<ServerConfigurationManager> event, EntityPlayerMP oldPlayer, int dimension, boolean didWin) {
        playerEventHandlers.all().eventMPPlayerRespawn(event.getSource(), oldPlayer, dimension, !didWin);
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        boolean allow = isAllowed;
        for (PlayerEventServerHandler mod : playerEventHandlers) {
            allow = mod.eventMPPlayerLoginAttempt(username, isAllowed);
        }
        return allow;
    }
}
