package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.event.args.ContainerOpenedEventArgs;
import net.acomputerdog.BlazeLoader.event.args.PacketEventArgs;
import net.acomputerdog.BlazeLoader.main.BLMain;
import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import sun.net.www.content.text.Generic;

import java.util.ArrayList;
import java.util.List;

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
        BLMod prevMod = BLMain.currActiveMod;
        for (TickEventHandler mod : tickEventHandlers) {
            setActiveMod(mod);
            mod.eventTick();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static boolean eventOnGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed) {
        BLMod prevMod = BLMain.currActiveMod;
        for (ClientEventHandler mod : clientEventHandlers) {
            setActiveMod(mod);
            allowed = mod.eventDisplayGui(oldGui, newGui, allowed);
        }
        BLMain.currActiveMod = prevMod;
        return allowed;
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

        ContainerOpenedEventArgs args = new ContainerOpenedEventArgs(player, packet);
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            if (mod.eventContainerOpen(player, c, args)) {
                player.openContainer.windowId = packet.func_148901_c();
                break;
            }
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventStartSection(String name) {
        BLMod prevMod = BLMain.currActiveMod;
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerStart(name);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventEndSection(String name) {
        BLMod prevMod = BLMain.currActiveMod;
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerEnd(name);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventLoadWorld(WorldClient par1WorldClient, String par2Str) {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventUnloadWorld() {
        BLMod prevMod = BLMain.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventUnloadWorld();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventPlayerLogin(EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventPlayerLogin(player);
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventPlayerLogout(EntityPlayerMP player) {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventPlayerLogout(player);
        }
        BLMain.currActiveMod = prevMod;
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath) {
        BLMod prevMod = BLMain.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
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
            allow = mod.eventPlayerLoginAttempt(username, isAllowed);
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

    public static void eventClientRecieveCustomPayload(NetHandlerPlayClient handler, S3FPacketCustomPayload packet) {
        String packetIdentifier = packet.func_149169_c();
        if (packetIdentifier != null) {
            if (packetIdentifier.indexOf("BL|") == 0) {
                PacketEventArgs args = new PacketEventArgs(packet, packetIdentifier);
                for (NetworkEventHandler mod : networkEventHandlers) {
                    if (mod.toString().equals(args.channel)) {
                        mod.eventClientRecieveCustomPayload(handler, args);
                    }
                }
            }
        }
    }

    public static void eventServerRecieveCustomPayload(NetHandlerPlayServer handler, C17PacketCustomPayload packet) {
        String packetIdentifier = packet.func_149559_c();
        if (packetIdentifier != null) {
            if (packetIdentifier.indexOf("BL|") == 0) {
                PacketEventArgs args = new PacketEventArgs(packet, packetIdentifier);
                for (NetworkEventHandler mod : networkEventHandlers) {
                    if (mod.toString().equals(args.channel)) {
                        mod.eventServerRecieveCustomPayload(handler, args);
                    }
                }
            }
        }
    }

    public static void eventKey(KeyBinding binding) {
        if (binding.getIsKeyPressed()) {
            for (ClientEventHandler mod : clientEventHandlers) {
                mod.eventKeyDown(binding);
            }
        } else {
            for (ClientEventHandler mod : clientEventHandlers) {
                mod.eventKeyUp(binding);
            }
        }
    }

    public static void eventKeyHeld() {
        for (KeyBinding i : (List<KeyBinding>) KeyBinding.keybindArray) {
            if (i.getIsKeyPressed()) {
                for (ClientEventHandler mod : clientEventHandlers) {
                    mod.eventKeyHeld(i);
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
}
