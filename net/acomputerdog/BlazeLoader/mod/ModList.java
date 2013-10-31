package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores and loads mods, distributes events, and allows access to loaded mods.
 */
public class ModList {
    private static final List<Mod> loadedMods = new ArrayList<Mod>();
    private static final List<Class> unloadedMods = new ArrayList<Class>();

    public static List<Mod> getLoadedMods(){
        return loadedMods;
    }

    public static List<Class> getUnloadedMods(){
        return unloadedMods;
    }

    private static Mod getCompatibleModFromList(Mod mod){
        if(mod != null){
            for(Mod m : loadedMods){
                BlazeLoader.activeMod = mod;
                if (mod.getModId().equals(m.getModId()))return m;
            }
            BlazeLoader.activeMod = null;
        }
        return null;
    }

    public static void load(){
        ApiBase.theProfiler.startSection("init_mods");
        Iterator<Class> iterator = unloadedMods.iterator();
        while(iterator.hasNext()){
            Class cls = iterator.next();
            Mod mod = null;
            try {
                mod = (Mod)cls.newInstance();
                if(mod.isCompatibleWithBLVersion()){
                    Mod sameMod = getCompatibleModFromList(mod);
                    boolean useNewMod = true;
                    if(sameMod != null){
                        BlazeLoader.getLogger().logWarning("Duplicate mod: " + mod.getModName() + "!  Newest version will be used!");
                        if(sameMod.getIntModVersion() < mod.getIntModVersion()){
                            sameMod.stop();
                            loadedMods.remove(sameMod);
                        }else{
                            useNewMod = false;
                        }
                    }
                    if(useNewMod){
                        mod.load();
                        loadedMods.add(mod);
                        BlazeLoader.getLogger().logDetail("Initialized mod: [" + mod.getModName() + "] version: [" + mod.getStringModVersion() + "].");
                    }
                }else{
                    BlazeLoader.getLogger().logError("Mod " + mod.getModName() + " is not compatible!  Unloading!");
                }
                iterator.remove();
            } catch (Exception e){
                if(mod != null){
                    loadedMods.remove(mod);
                }
                BlazeLoader.getLogger().logError("Could not initialize mod: " + cls.getName());
                e.printStackTrace();
                iterator.remove();
            }
        }
        ApiBase.theProfiler.endSection();
    }

    public static void start(){
        ApiBase.theProfiler.startSection("start_mods");
        BlazeLoader.updateFreeBlockId();
        BlazeLoader.updateFreeItemId();
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            BlazeLoader.activeMod = mod;
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            try{
                mod.start();
            }catch(Exception e){
                iterator.remove();
                e.printStackTrace();
            }
            ApiBase.theProfiler.endSection();
        }
        BlazeLoader.activeMod = null;
        ApiBase.theProfiler.endSection();
    }

    public static void stop(){
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            BlazeLoader.activeMod = mod;
            try{
                mod.stop();
                BlazeLoader.getLogger().logDetail("Stopped mod: " + mod.getModName());
            }catch(Exception e){
                iterator.remove();
                BlazeLoader.getLogger().logDetail("Could not stop mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
        BlazeLoader.activeMod = null;
    }

    public static void tick(){
        ApiBase.theProfiler.startSection("tick_mods");
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            mod.eventTick();
            ApiBase.theProfiler.endSection();
        }
        BlazeLoader.activeMod = null;
        ApiBase.theProfiler.endSection();
    }

    public static GuiScreen onGui(GuiScreen gui){
        GuiScreen newGui = gui;
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            if(newGui == gui){
                newGui = mod.eventDisplayGui(gui, false);
            }else{
                newGui = mod.eventDisplayGui(gui, true);
            }
        }
        BlazeLoader.activeMod = null;
        return newGui;
    }

    public static void startSection(String name){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventProfilerStart(name);
        }
        BlazeLoader.activeMod = null;
    }

    public static void endSection(String name){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventProfilerEnd(name);
        }
        BlazeLoader.activeMod = null;
    }

    public static void loadWorld(WorldClient par1WorldClient, String par2Str){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
        BlazeLoader.activeMod = null;
    }

    public static void unloadWorld(){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventUnloadWorld();
        }
        BlazeLoader.activeMod = null;
    }

    public static void eventPlayerLogin(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventPlayerLogin(player);
        }
        BlazeLoader.activeMod = null;
    }

    public static void eventPlayerLogout(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventPlayerLogout(player);
        }
        BlazeLoader.activeMod = null;
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
        }
        BlazeLoader.activeMod = null;
    }

    public static void eventClientPlayerDeath(){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventClientPlayerDeath();
        }
        BlazeLoader.activeMod = null;
    }

    public static void eventTickServerWorld(WorldServer world){
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            mod.eventTickServerWorld(world);
        }
        BlazeLoader.activeMod = null;
    }

    public static Packet23VehicleSpawn createSpawnPacket(Entity myEntity){
        Packet23VehicleSpawn packet = null;
        for(Mod mod : loadedMods){
            BlazeLoader.activeMod = mod;
            Packet23VehicleSpawn modPacket = mod.createSpawnPacket(myEntity, packet != null);
            if(modPacket != null){
                packet = modPacket;
            }
        }
        BlazeLoader.activeMod = null;
        return packet;
    }
}
