package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores and loads mods, distributes events, and allows access to loaded mods.
 */
public class ModList {
    private static final List<Mod> loadedMods = new ArrayList<Mod>();
    private static final List<Class<? extends Mod>> unloadedMods = new ArrayList<Class<? extends Mod>>();
    private static final List<ModData> modData = new ArrayList<ModData>();

    public static List<Mod> getLoadedMods() {
        return loadedMods;
    }

    public static List<Class<? extends Mod>> getUnloadedMods() {
        return unloadedMods;
    }

    public static List<ModData> getModData() {
        return modData;
    }

    private static Mod getCompatibleModFromList(Mod mod) {
        if (mod != null) {
            for (Mod m : loadedMods) {
                BlazeLoader.currActiveMod = mod;
                if (mod.getModId().equals(m.getModId())) return m;
            }
            BlazeLoader.currActiveMod = null;
        }
        return null;
    }

    public static void load() {
        ApiGeneral.theProfiler.startSection("init_mods");
        Iterator<Class<? extends Mod>> iterator = unloadedMods.iterator();
        while (iterator.hasNext()) {
            Class<? extends Mod> cls = iterator.next();
            Mod mod = null;
            try {
                mod = cls.newInstance();
                if (mod.isCompatibleWithBLVersion()) {
                    Mod sameMod = getCompatibleModFromList(mod);
                    boolean useNewMod = true;
                    if (sameMod != null) {
                        BlazeLoader.getLogger().logWarning("Duplicate mod: " + mod.getModName() + "!  Newest version will be used!");
                        if (sameMod.getIntModVersion() < mod.getIntModVersion()) {
                            sameMod.stop();
                            loadedMods.remove(sameMod);
                        } else {
                            useNewMod = false;
                        }
                    }
                    if (useNewMod) {
                        mod.load();
                        loadedMods.add(mod);
                        ModData data = new ModData(mod, cls, ModLoader.getModSource(cls.getName()), mod.getModId());
                        modData.add(data);
                        ModLoader.loadModAsResourcePack(data);
                        BlazeLoader.getLogger().logDetail("Initialized mod: [" + mod.getModName() + "] version: [" + mod.getStringModVersion() + "].");
                    }
                } else {
                    BlazeLoader.getLogger().logError("Mod " + mod.getModName() + " is not compatible!  Unloading!");
                }
                iterator.remove();
            } catch (Exception e) {
                if (mod != null) {
                    loadedMods.remove(mod);
                }
                BlazeLoader.getLogger().logError("Could not initialize mod: " + cls.getName());
                e.printStackTrace();
                iterator.remove();
            }
        }
        ApiGeneral.theProfiler.endSection();
    }

    public static void start() {
        ApiGeneral.theProfiler.startSection("start_mods");
        Iterator<Mod> iterator = loadedMods.iterator();
        while (iterator.hasNext()) {
            Mod mod = iterator.next();
            BlazeLoader.currActiveMod = mod;
            ApiGeneral.theProfiler.startSection("mod_" + mod.getModId());
            try {
                mod.start();
            } catch (Exception e) {
                iterator.remove();
                e.printStackTrace();
            }
            ApiGeneral.theProfiler.endSection();
        }
        BlazeLoader.currActiveMod = null;
        ApiGeneral.theProfiler.endSection();
    }

    public static void stop() {
        Iterator<Mod> iterator = loadedMods.iterator();
        while (iterator.hasNext()) {
            Mod mod = iterator.next();
            BlazeLoader.currActiveMod = mod;
            try {
                mod.stop();
                BlazeLoader.getLogger().logDetail("Stopped mod: " + mod.getModName());
            } catch (Exception e) {
                iterator.remove();
                BlazeLoader.getLogger().logDetail("Could not stop mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void tick() {
        ApiGeneral.theProfiler.startSection("tick_mods");
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            ApiGeneral.theProfiler.startSection("mod_" + mod.getModId());
            mod.eventTick();
            ApiGeneral.theProfiler.endSection();
        }
        BlazeLoader.currActiveMod = null;
        ApiGeneral.theProfiler.endSection();
    }

    public static boolean onGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            allowed = mod.eventDisplayGui(oldGui, newGui, allowed);
        }
        BlazeLoader.currActiveMod = null;
        return allowed;
    }

    public static void startSection(String name) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventProfilerStart(name);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void endSection(String name) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventProfilerEnd(name);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void loadWorld(WorldClient par1WorldClient, String par2Str) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void unloadWorld() {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventUnloadWorld();
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventPlayerLogin(EntityPlayerMP player) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventPlayerLogin(player);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventPlayerLogout(EntityPlayerMP player) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventPlayerLogout(player);
        }
        BlazeLoader.currActiveMod = null;
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventClientPlayerDeath() {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventClientPlayerDeath();
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventTickServerWorld(WorldServer world) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventTickServerWorld(world);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static S0EPacketSpawnObject createSpawnPacket(Entity myEntity) {
        S0EPacketSpawnObject packet = null;
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            S0EPacketSpawnObject modPacket = mod.overrideCreateSpawnPacket(myEntity, packet != null);
            if (modPacket != null) {
                packet = modPacket;
            }
        }
        BlazeLoader.currActiveMod = null;
        return packet;
    }

    public static void eventTickBlocksAndAmbiance(WorldServer server) {
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            mod.eventTickBlocksAndAmbiance(server);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed) {
        boolean allow = isAllowed;
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            allow = mod.eventPlayerLoginAttempt(username, isAllowed);
        }
        BlazeLoader.currActiveMod = null;
        return allow;
    }

    public static void addEntityToTracker(EntityTracker tracker, Entity entity) {
        boolean isHandled = false;
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            boolean didHandle = mod.overrideAddEntityToTracker(tracker, entity, isHandled);
            if (didHandle) {
                isHandled = true;
            }
        }
        BlazeLoader.currActiveMod = null;
    }

    public static EntityFX spawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3) {
        EntityFX entity = null;
        for (Mod mod : loadedMods) {
            BlazeLoader.currActiveMod = mod;
            entity = mod.overrideSpawnParticle(name, world, x, y, z, p1, p2, p3, entity);
        }
        return entity;
    }

}
