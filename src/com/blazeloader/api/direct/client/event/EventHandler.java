package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.integrated.IntegratedPlayerList;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Distributes game events to mods
 */
public class EventHandler {
    public static final List<BlockEventHandler> blockEventHandlers = new ArrayList<BlockEventHandler>();
    public static final List<ClientEventHandler> clientEventHandlers = new ArrayList<ClientEventHandler>();
    public static final List<GenericEventHandler> genericEventHandlers = new ArrayList<GenericEventHandler>();
    public static final List<InventoryEventHandler> inventoryEventHandlers = new ArrayList<InventoryEventHandler>();
    public static final List<OverrideEventHandler> overrideEventHandlers = new ArrayList<OverrideEventHandler>();
    public static final List<PlayerEventHandler> playerEventHandlers = new ArrayList<PlayerEventHandler>();
    public static final List<ProfilerEventHandler> profilerEventHandlers = new ArrayList<ProfilerEventHandler>();
    public static final List<TickEventHandler> tickEventHandlers = new ArrayList<TickEventHandler>();
    public static final List<WorldEventHandler> worldEventHandlers = new ArrayList<WorldEventHandler>();
    public static final List<NetworkEventHandler> networkEventHandlers = new ArrayList<NetworkEventHandler>();

    private static void setActiveMod(Object mod) {
        if (mod instanceof BLMod) {
            BLMain.currActiveMod = (BLMod) mod;
        } else {
            BLMain.currActiveMod = null;
        }
    }

    public static void eventTick() {
        BLMain.numTicks++;
        BLMain.isInTick = true;
        BLMod prevMod = BLMain.currActiveMod;
        for (TickEventHandler mod : tickEventHandlers) {
            setActiveMod(mod);
            mod.eventTick();
        }
        BLMain.currActiveMod = prevMod;
        BLMain.isInTick = false;
    }

    public static void eventDisplayGuiScreen(EventInfo<Minecraft> event, GuiScreen gui) {
        BLMod prevMod = BLMain.currActiveMod;
        Minecraft mc = event.getSource();
        GuiScreen currentScreen = mc.currentScreen;
        for (ClientEventHandler mod : clientEventHandlers) {
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

        OverrideEventHandler.ContainerOpenedEventArgs args = new OverrideEventHandler.ContainerOpenedEventArgs(player, packet);
        for (OverrideEventHandler mod : overrideEventHandlers) {
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
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerStart(prof, name);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventEndSection(EventInfo<Profiler> event) {
        BLMod prevMod = BLMain.currActiveMod;
        Profiler prof = event.getSource();
        String lastSection = prof.getNameOfLastSection();
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerEnd(prof, lastSection);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventLoadWorld(EventInfo<Minecraft> event, WorldClient world, String message) {
        BLMod prevMod = BLMain.currActiveMod;
        Minecraft mc = event.getSource();
        WorldClient currWorld = mc.theWorld;
        for (WorldEventHandler mod : worldEventHandlers) {
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
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventClientJoinGame(netHandler, loginPacket);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventPlayerLoggedIn(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerLogin(manager, player);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventPlayerLoggedOut(EventInfo<ServerConfigurationManager> event, EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerLogout(manager, player);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventRespawnPlayer(ReturnEventInfo<IntegratedPlayerList, ?> event, EntityPlayerMP oldPlayer, int dimension, boolean didWin) { //not sure why this one is different, but it crashes with the normal system...
        BLMod prevMod = BLMain.currActiveMod;
        ServerConfigurationManager manager = event.getSource();
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventMPPlayerRespawn(manager, oldPlayer, dimension, !didWin);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventClientPlayerDeath() {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventClientPlayerDeath();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventTickServerWorld(WorldServer world) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickServerWorld(world);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static S0EPacketSpawnObject overrideCreateSpawnPacket(Entity myEntity) {
        BLMod prevMod = BLMain.currActiveMod;
        S0EPacketSpawnObject packet = null;
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) {
                packet = modPacket;
            }
        }
        BLMain.currActiveMod = prevMod;
        return packet;
    }

    public static void eventTickBlocksAndAmbiance(WorldServer server) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickBlocksAndAmbiance(server);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        BLMod prevMod = BLMain.currActiveMod;
        boolean allow = isAllowed;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            allow = mod.eventMPPlayerLoginAttempt(username, isAllowed);
        }
        BLMain.currActiveMod = prevMod;
        return allow;
    }

    public static void overrideAddEntityToTracker(EntityTracker tracker, Entity entity) {
        BLMod prevMod = BLMain.currActiveMod;
        boolean isHandled = false;
        for (OverrideEventHandler mod : overrideEventHandlers) {
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
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        BLMain.currActiveMod = prevMod;
        return entity;
    }

    public static void eventClientReceiveCustomPayload(NetHandlerPlayClient handler, S3FPacketCustomPayload packet) {
        String packetIdentifier = packet.func_149169_c();
        if (packetIdentifier != null) {
            if (packetIdentifier.indexOf("BL|") == 0) {
                NetworkEventHandler.PacketEventArgs args = new NetworkEventHandler.PacketEventArgs(packet, packetIdentifier);
                for (NetworkEventHandler mod : networkEventHandlers) {
                    if (mod.toString().equals(args.channel)) {
                        mod.eventClientRecieveCustomPayload(handler, args);
                    }
                }
            }
        }
    }

    public static void eventServerReceiveCustomPayload(NetHandlerPlayServer handler, C17PacketCustomPayload packet) {
        String packetIdentifier = packet.func_149559_c();
        if (packetIdentifier != null) {
            if (packetIdentifier.indexOf("BL|") == 0) {
                NetworkEventHandler.PacketEventArgs args = new NetworkEventHandler.PacketEventArgs(packet, packetIdentifier);
                for (NetworkEventHandler mod : networkEventHandlers) {
                    if (mod.toString().equals(args.channel)) {
                        mod.eventServerRecieveCustomPayload(handler, args);
                    }
                }
            }
        }
    }

    public static void eventStart() {
        BLMod prevMod = BLMain.currActiveMod;
        for (GenericEventHandler mod : genericEventHandlers) {
            setActiveMod(mod);
            mod.start();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventEnd() {
        BLMod prevMod = BLMain.currActiveMod;
        for (GenericEventHandler mod : genericEventHandlers) {
            setActiveMod(mod);
            mod.stop();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventWorldChanged(World world) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventWorldChanged(world);
        }
        BLMain.currActiveMod = prevMod;
    }
}
