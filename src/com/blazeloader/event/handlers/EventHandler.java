package com.blazeloader.event.handlers;

import com.blazeloader.api.entity.EntityPropertyManager;
import com.blazeloader.api.world.UnpopulatedChunksQ;
import com.blazeloader.event.listeners.ChunkListener;
import com.blazeloader.event.listeners.EntityConstructingListener;
import com.blazeloader.event.listeners.ModEventListener;
import com.blazeloader.event.listeners.PlayerListener;
import com.blazeloader.event.listeners.TickListener;
import com.blazeloader.event.listeners.WorldListener;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.transformers.event.EventInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Side-independent event handler
 */
public class EventHandler {
    public static final HandlerList<ModEventListener> modEventHandlers = new HandlerList<ModEventListener>(ModEventListener.class);
    public static final HandlerList<TickListener> tickEventHandlers = new HandlerList<TickListener>(TickListener.class);
    public static final HandlerList<WorldListener> worldEventHandlers = new HandlerList<WorldListener>(WorldListener.class);
    public static final HandlerList<PlayerListener> playerEventHandlers = new HandlerList<PlayerListener>(PlayerListener.class);
    public static final HandlerList<ChunkListener> chunkEventHandlers = new HandlerList<ChunkListener>(ChunkListener.class);
    public static final HandlerList<EntityConstructingListener> entityEventHandlers = new HandlerList<EntityConstructingListener>(EntityConstructingListener.class);

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
        for (PlayerListener mod : playerEventHandlers) {
            allow = mod.eventMPPlayerLoginAttempt(username, isAllowed);
        }
        return allow;
    }

    public static void eventOnChunkLoad(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        if (!chunk.isTerrainPopulated()) {
            UnpopulatedChunksQ.instance().push(chunk);
        }
        chunkEventHandlers.all().onChunkLoad(chunk);
    }

    public static void eventOnChunkUnload(EventInfo<Chunk> event) {
        Chunk chunk = event.getSource();
        UnpopulatedChunksQ.instance().pop(chunk);
        chunkEventHandlers.all().onChunkUnload(chunk);
    }
    
    public static void initEntity(EventInfo<Entity> event, World w) {
    	entityEventHandlers.all().eventEntityConstructed(event.getSource());
    	EntityPropertyManager.entityinit(event.getSource());
    }
}
