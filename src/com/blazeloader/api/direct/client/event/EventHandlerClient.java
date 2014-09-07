package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.direct.base.event.EventHandlerBase;
import com.blazeloader.api.direct.base.event.TickEventBaseHandler;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Distributes game events to mods
 */
public class EventHandlerClient extends EventHandlerBase {
    public static final List<BlockEventHandler> blockEventHandlers = new ArrayList<BlockEventHandler>();
    public static final List<GuiEventClientHandler> clientEventHandlers = new ArrayList<GuiEventClientHandler>();
    public static final List<InventoryEventHandler> inventoryEventHandlers = new ArrayList<InventoryEventHandler>();
    public static final List<OverrideEventClientHandler> overrideEventHandlers = new ArrayList<OverrideEventClientHandler>();
    public static final List<PlayerEventClientHandler> playerEventHandlers = new ArrayList<PlayerEventClientHandler>();
    public static final List<ProfilerEventClientHandler> profilerEventHandlers = new ArrayList<ProfilerEventClientHandler>();
    public static final List<WorldEventClientHandler> worldEventHandlers = new ArrayList<WorldEventClientHandler>();

    public static void eventTick() {
        BLMod prevMod = BLMain.currActiveMod;
        for (TickEventBaseHandler mod : tickEventHandlers) {
            setActiveMod(mod);
            mod.eventTick();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventDisplayGuiScreen(EventInfo<Minecraft> event, GuiScreen gui) {
        BLMod prevMod = BLMain.currActiveMod;
        Minecraft mc = event.getSource();
        GuiScreen currentScreen = mc.currentScreen;
        for (GuiEventClientHandler mod : clientEventHandlers) {
            setActiveMod(mod);
            mod.eventDisplayGui(mc, currentScreen, gui);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void overrideOnContainerOpen(EntityClientPlayerMP player, S2DPacketOpenWindow packet) {
        BLMod prevMod = BLMain.currActiveMod;
        String clazzName = packet.func_148902_e().split(":?:")[0];

        Class c;
        try {
            c = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + clazzName, e);
        }

        OverrideEventClientHandler.ContainerOpenedEventArgs args = new OverrideEventClientHandler.ContainerOpenedEventArgs(player, packet);
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            if (mod.overrideContainerOpen(player, c, args)) {
                player.openContainer.windowId = packet.func_148901_c();
                break;
            }
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventStartSection(EventInfo<Profiler> event, String name) {
        BLMod prevMod = BLMain.currActiveMod;
        Profiler prof = event.getSource();
        for (ProfilerEventClientHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerStart(prof, name);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventEndSection(EventInfo<Profiler> event) {
        BLMod prevMod = BLMain.currActiveMod;
        Profiler prof = event.getSource();
        String lastSection = prof.getNameOfLastSection();
        for (ProfilerEventClientHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerEnd(prof, lastSection);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventLoadWorld(EventInfo<Minecraft> event, WorldClient world, String message) {
        BLMod prevMod = BLMain.currActiveMod;
        Minecraft mc = event.getSource();
        WorldClient currWorld = mc.theWorld;
        for (WorldEventClientHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            if (world != null) {
                mod.eventLoadWorld(mc, world, message);
            } else {
                mod.eventUnloadWorld(mc, currWorld, message);
            }
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventClientHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventClientJoinGame(netHandler, loginPacket);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventClientPlayerDeath() {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventClientHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventClientPlayerDeath();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static S0EPacketSpawnObject overrideCreateSpawnPacket(Entity myEntity) {
        BLMod prevMod = BLMain.currActiveMod;
        S0EPacketSpawnObject packet = null;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) {
                packet = modPacket;
            }
        }
        BLMain.currActiveMod = prevMod;
        return packet;
    }

    public static void overrideAddEntityToTracker(EntityTracker tracker, Entity entity) {
        BLMod prevMod = BLMain.currActiveMod;
        boolean isHandled = false;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            boolean didHandle = mod.overrideAddEntityToTracker(tracker, entity, isHandled);
            if (didHandle) {
                isHandled = true;
            }
        }
        BLMain.currActiveMod = prevMod;
    }

    public static EntityFX overrideSpawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3) {
        BLMod prevMod = BLMain.currActiveMod;
        EntityFX entity = null;
        for (OverrideEventClientHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        BLMain.currActiveMod = prevMod;
        return entity;
    }

    public static void eventWorldChanged(World world) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventClientHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventWorldChanged(world);
        }
        BLMain.currActiveMod = prevMod;
    }
}
