package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.event.args.ContainerOpenedEventArgs;
import net.acomputerdog.BlazeLoader.event.args.PacketEventArgs;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.Mod;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EventHandler {
    private static final List<BlockEventHandler> blockEventHandlers = new ArrayList<BlockEventHandler>();
    private static final List<ClientEventHandler> clientEventHandlers = new ArrayList<ClientEventHandler>();
    private static final List<GenericEventHandler> genericEventHandlers = new ArrayList<GenericEventHandler>();
    private static final List<InventoryEventHandler> inventoryEventHandlers = new ArrayList<InventoryEventHandler>();
    private static final List<OverrideEventHandler> overrideEventHandlers = new ArrayList<OverrideEventHandler>();
    private static final List<PlayerEventHandler> playerEventHandlers = new ArrayList<PlayerEventHandler>();
    private static final List<ProfilerEventHandler> profilerEventHandlers = new ArrayList<ProfilerEventHandler>();
    private static final List<TickEventHandler> tickEventHandlers = new ArrayList<TickEventHandler>();
    private static final List<WorldEventHandler> worldEventHandlers = new ArrayList<WorldEventHandler>();
    private static final List<NetworkEventHandler> networkEventHandlers = new ArrayList<NetworkEventHandler>();

    public static void addMod(Mod mod) {
        if (mod instanceof BlockEventHandler) {
            blockEventHandlers.add((BlockEventHandler) mod);
        }
        if (mod instanceof ClientEventHandler) {
            clientEventHandlers.add((ClientEventHandler) mod);
        }
        if (mod instanceof GenericEventHandler) {
            genericEventHandlers.add((GenericEventHandler) mod);
        }
        if (mod instanceof InventoryEventHandler) {
            inventoryEventHandlers.add((InventoryEventHandler) mod);
        }
        if (mod instanceof OverrideEventHandler) {
            overrideEventHandlers.add((OverrideEventHandler) mod);
        }
        if (mod instanceof PlayerEventHandler) {
            playerEventHandlers.add((PlayerEventHandler) mod);
        }
        if (mod instanceof ProfilerEventHandler) {
            profilerEventHandlers.add((ProfilerEventHandler) mod);
        }
        if (mod instanceof TickEventHandler) {
            tickEventHandlers.add((TickEventHandler) mod);
        }
        if (mod instanceof WorldEventHandler) {
            worldEventHandlers.add((WorldEventHandler) mod);
        }
        if (mod instanceof NetworkEventHandler) {
        	networkEventHandlers.add((NetworkEventHandler)mod);
        }
    }

    private static void setActiveMod(Object mod) {
        if (mod instanceof Mod) {
            BlazeLoader.currActiveMod = (Mod) mod;
        } else {
            BlazeLoader.currActiveMod = null;
        }
    }

    public static void eventTick() {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (TickEventHandler mod : tickEventHandlers) {
            setActiveMod(mod);
            mod.eventTick();
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static boolean eventOnGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (ClientEventHandler mod : clientEventHandlers) {
            setActiveMod(mod);
            allowed = mod.eventDisplayGui(oldGui, newGui, allowed);
        }
        BlazeLoader.currActiveMod = prevMod;
        return allowed;
    }

    public static void overrideOnContainerOpen(EntityClientPlayerMP player, S2DPacketOpenWindow packet) {
        Mod prevMod = BlazeLoader.currActiveMod;
        String clazzName = packet.func_148902_e().split(":?:")[0];

        Class c;
        try {
            c = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + clazzName, e);
        }

        ContainerOpenedEventArgs args = new ContainerOpenedEventArgs(player, packet);
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            if (mod.eventContainerOpen(player, c, args)) {
            	player.openContainer.windowId = packet.func_148901_c();
                break;
            }
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventStartSection(String name) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerStart(name);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventEndSection(String name) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (ProfilerEventHandler mod : profilerEventHandlers) {
            setActiveMod(mod);
            mod.eventProfilerEnd(name);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventLoadWorld(WorldClient par1WorldClient, String par2Str) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventUnloadWorld() {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventUnloadWorld();
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventPlayerLogin(EntityPlayerMP player) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventPlayerLogin(player);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventPlayerLogout(EntityPlayerMP player) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventPlayerLogout(player);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventClientPlayerDeath() {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            mod.eventClientPlayerDeath();
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static void eventTickServerWorld(WorldServer world) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickServerWorld(world);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static S0EPacketSpawnObject overrideCreateSpawnPacket(Entity myEntity) {
        Mod prevMod = BlazeLoader.currActiveMod;
        S0EPacketSpawnObject packet = null;
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) {
                packet = modPacket;
            }
        }
        BlazeLoader.currActiveMod = prevMod;
        return packet;
    }

    public static void eventTickBlocksAndAmbiance(WorldServer server) {
        Mod prevMod = BlazeLoader.currActiveMod;
        for (WorldEventHandler mod : worldEventHandlers) {
            setActiveMod(mod);
            mod.eventTickBlocksAndAmbiance(server);
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        Mod prevMod = BlazeLoader.currActiveMod;
        boolean allow = isAllowed;
        for (PlayerEventHandler mod : playerEventHandlers) {
            setActiveMod(mod);
            allow = mod.eventPlayerLoginAttempt(username, isAllowed);
        }
        BlazeLoader.currActiveMod = prevMod;
        return allow;
    }

    public static void overrideAddEntityToTracker(EntityTracker tracker, Entity entity) {
        Mod prevMod = BlazeLoader.currActiveMod;
        boolean isHandled = false;
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            boolean didHandle = mod.overrideAddEntityToTracker(tracker, entity, isHandled);
            if (didHandle) {
                isHandled = true;
            }
        }
        BlazeLoader.currActiveMod = prevMod;
    }

    public static EntityFX overrideSpawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3) {
        Mod prevMod = BlazeLoader.currActiveMod;
        EntityFX entity = null;
        for (OverrideEventHandler mod : overrideEventHandlers) {
            setActiveMod(mod);
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        BlazeLoader.currActiveMod = prevMod;
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
    	for (KeyBinding i : (List<KeyBinding>)KeyBinding.keybindArray) {
        	if (i.getIsKeyPressed()) {
        		for (ClientEventHandler mod : clientEventHandlers) {
            		mod.eventKeyHeld(i);
            	}
        	}
        }
    }
}
