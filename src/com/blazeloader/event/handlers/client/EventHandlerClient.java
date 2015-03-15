package com.blazeloader.event.handlers.client;

import com.blazeloader.event.handlers.EventHandler;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.transformers.event.EventInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;

/**
 * Distributes game events to mods
 */
public class EventHandlerClient extends EventHandler {
    public static final HandlerList<BlockEventClientHandler> blockEventClients = new HandlerList<BlockEventClientHandler>(BlockEventClientHandler.class);
    public static final HandlerList<GuiEventClientHandler> guiEventClients = new HandlerList<GuiEventClientHandler>(GuiEventClientHandler.class);
    public static final HandlerList<InventoryEventClientHandler> inventoryEventClients = new HandlerList<InventoryEventClientHandler>(InventoryEventClientHandler.class);
    public static final HandlerList<OverrideEventClientHandler> overrideEventClients = new HandlerList<OverrideEventClientHandler>(OverrideEventClientHandler.class, ReturnLogicOp.OR_BREAK_ON_TRUE);
    public static final HandlerList<PlayerEventClientHandler> playerEventClients = new HandlerList<PlayerEventClientHandler>(PlayerEventClientHandler.class);
    public static final HandlerList<ProfilerEventClientHandler> profilerEventClients = new HandlerList<ProfilerEventClientHandler>(ProfilerEventClientHandler.class);
    public static final HandlerList<WorldEventClientHandler> worldEventClients = new HandlerList<WorldEventClientHandler>(WorldEventClientHandler.class);

    public static void eventDisplayGuiScreen(EventInfo<Minecraft> event, GuiScreen gui) {
        Minecraft mc = event.getSource();
        guiEventClients.all().eventDisplayGui(mc, mc.currentScreen, gui);
    }

    public static void overrideOnContainerOpen(AbstractClientPlayer player, S2DPacketOpenWindow packet) {
        OverrideEventClientHandler.ContainerOpenedEventArgs args = new OverrideEventClientHandler.ContainerOpenedEventArgs(player, packet);
        if (overrideEventClients.all().overrideContainerOpen(player, args)) {
            player.openContainer.windowId = packet.getWindowId();
        }
        /*TODO: This one remains as an iteration for the time being as it requires ReturnLogicOp.OR_BREAK_ON_TRUE.
         * Switched away from iterator. May require some testing.
         */
        /*for (OverrideEventClientHandler mod : overrideEventClients) {
            if (mod.overrideContainerOpen(player, c, args)) {
                player.openContainer.windowId = packet.func_148901_c();
                break;
            }
        }*/
    }

    public static void eventStartSection(EventInfo<Profiler> event, String name) {
        profilerEventClients.all().eventProfilerStart(event.getSource(), name);
    }

    public static void eventEndSection(EventInfo<Profiler> event) {
        Profiler prof = event.getSource();
        profilerEventClients.all().eventProfilerEnd(prof, prof.getNameOfLastSection());
    }

    public static void eventLoadWorld(EventInfo<Minecraft> event, WorldClient world, String message) {
        Minecraft mc = event.getSource();
        if (world != null) {
            worldEventClients.all().eventLoadWorld(mc, world, message);
        } else {
            worldEventClients.all().eventUnloadWorld(mc, mc.theWorld, message);
        }
    }

    public static void eventClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
        playerEventClients.all().eventClientJoinGame(netHandler, loginPacket);
    }

    public static void eventClientPlayerDeath() {
        playerEventClients.all().eventClientPlayerDeath();
    }

    public static S0EPacketSpawnObject overrideCreateSpawnPacket(Entity myEntity) {
        S0EPacketSpawnObject packet = null;
        for (OverrideEventClientHandler mod : overrideEventClients) {
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) packet = modPacket;
        }
        return packet;
    }

    public static void overrideAddEntityToTracker(EntityTracker tracker, Entity entity) {
        boolean isHandled = false;
        for (OverrideEventClientHandler mod : overrideEventClients) {
            isHandled |= mod.overrideAddEntityToTracker(tracker, entity, isHandled);
        }
    }

    public static EntityFX overrideSpawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3) {
        EntityFX entity = null;
        for (OverrideEventClientHandler mod : overrideEventClients) {
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        return entity;
    }

    public static void eventWorldChanged(World world) {
        worldEventClients.all().eventWorldChanged(world);
    }
}
