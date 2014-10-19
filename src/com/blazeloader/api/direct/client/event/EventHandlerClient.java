package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.direct.base.event.EventHandlerBase;
import com.mumfrey.liteloader.core.event.HandlerList;
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
public class EventHandlerClient extends EventHandlerBase {
    public static final HandlerList<BlockEventHandler> blockEventHandlers = new HandlerList<BlockEventHandler>(BlockEventHandler.class);
    public static final HandlerList<GuiEventClientHandler> clientEventHandlers = new HandlerList<GuiEventClientHandler>(GuiEventClientHandler.class);
    public static final HandlerList<InventoryEventHandler> inventoryEventHandlers = new HandlerList<InventoryEventHandler>(InventoryEventHandler.class);
    public static final HandlerList<OverrideEventClientHandler> overrideEventHandlers = new HandlerList<OverrideEventClientHandler>(OverrideEventClientHandler.class);
    public static final HandlerList<PlayerEventClientHandler> playerEventHandlers = new HandlerList<PlayerEventClientHandler>(PlayerEventClientHandler.class);
    public static final HandlerList<ProfilerEventClientHandler> profilerEventHandlers = new HandlerList<ProfilerEventClientHandler>(ProfilerEventClientHandler.class);
    public static final HandlerList<WorldEventClientHandler> worldEventHandlers = new HandlerList<WorldEventClientHandler>(WorldEventClientHandler.class);

    public static void eventTick() {
        tickEventHandlers.all().eventTick();
    }

    public static void eventDisplayGuiScreen(EventInfo<Minecraft> event, GuiScreen gui) {
        Minecraft mc = event.getSource();
        clientEventHandlers.all().eventDisplayGui(mc, mc.currentScreen, gui);
    }

    public static void overrideOnContainerOpen(AbstractClientPlayer player, S2DPacketOpenWindow packet) {
        String clazzName = packet.func_148902_e().split(":?:")[0];
        Class c;
        try {
            c = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + clazzName, e);
        }

        OverrideEventClientHandler.ContainerOpenedEventArgs args = new OverrideEventClientHandler.ContainerOpenedEventArgs(player, packet);
        /*if (overrideEventHandlers.all().overrideContainerOpen(player, c, args)) {
            player.openContainer.windowId = packet.func_148901_c();
        }*/
        //TODO: This one remains as an iteration for the time being as it requires ReturnLogicOp.OR_BREAK_ON_TRUE.
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            if (mod.overrideContainerOpen(player, c, args)) {
                player.openContainer.windowId = packet.func_148901_c();
                break;
            }
        }
    }

    public static void eventStartSection(EventInfo<Profiler> event, String name) {
        profilerEventHandlers.all().eventProfilerStart(event.getSource(), name);
    }

    public static void eventEndSection(EventInfo<Profiler> event) {
        Profiler prof = event.getSource();
        profilerEventHandlers.all().eventProfilerEnd(prof, prof.getNameOfLastSection());
    }

    public static void eventLoadWorld(EventInfo<Minecraft> event, WorldClient world, String message) {
        Minecraft mc = event.getSource();
        if (world != null) {
            worldEventHandlers.all().eventLoadWorld(mc, world, message);
        } else {
            worldEventHandlers.all().eventUnloadWorld(mc, mc.theWorld, message);
        }
    }

    public static void eventClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
        playerEventHandlers.all().eventClientJoinGame(netHandler, loginPacket);
    }

    public static void eventClientPlayerDeath() {
        playerEventHandlers.all().eventClientPlayerDeath();
    }

    public static S0EPacketSpawnObject overrideCreateSpawnPacket(Entity myEntity) {
        S0EPacketSpawnObject packet = null;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) packet = modPacket;
        }
        return packet;
    }

    public static void overrideAddEntityToTracker(EntityTracker tracker, Entity entity) {
        boolean isHandled = false;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            isHandled |= mod.overrideAddEntityToTracker(tracker, entity, isHandled);
        }
    }

    public static EntityFX overrideSpawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3) {
        EntityFX entity = null;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        return entity;
    }

    public static void eventWorldChanged(World world) {
        worldEventHandlers.all().eventWorldChanged(world);
    }
}
