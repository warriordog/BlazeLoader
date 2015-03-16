package com.blazeloader.event.handlers;

import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

/**
 * Side-independent event handler
 */
public class EventHandler {
    public static final HandlerList<ModEventHandler> modEventHandlers = new HandlerList<ModEventHandler>(ModEventHandler.class);
    public static final HandlerList<TickEventHandler> tickEventHandlers = new HandlerList<TickEventHandler>(TickEventHandler.class);
    public static final HandlerList<WorldEventHandler> worldEventHandlers = new HandlerList<WorldEventHandler>(WorldEventHandler.class);
    public static final HandlerList<PlayerEventHandler> playerEventHandlers = new HandlerList<PlayerEventHandler>(PlayerEventHandler.class);


    public static void eventTick() {
        tickEventHandlers.all().eventTick();
    }

    public static void eventStart() {
        modEventHandlers.all().start();
    }

    public static void eventEnd() {
        modEventHandlers.all().stop();
    }

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
        for (PlayerEventHandler mod : playerEventHandlers) {
            allow = mod.eventMPPlayerLoginAttempt(username, isAllowed);
        }
        return allow;
    }
}
