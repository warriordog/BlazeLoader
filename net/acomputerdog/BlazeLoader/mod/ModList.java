package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

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
                if (mod.getModId().equals(m.getModId()))return m;
            }
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
                    iterator.remove();
                    BlazeLoader.getLogger().logError("Mod " + mod.getModName() + " is not compatible!  Unloading!");
                }
            } catch (Exception e){
                if(mod != null){
                    loadedMods.remove(mod);
                }
                BlazeLoader.getLogger().logError("Could not initialize mod: " + cls.getName());
                e.printStackTrace();
            } finally{
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
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            try{
                mod.start();
            }catch(Exception e){
                iterator.remove();
                e.printStackTrace();
            }
            ApiBase.theProfiler.endSection();
        }
        ApiBase.theProfiler.endSection();
    }

    public static void stop(){
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            try{
                mod.stop();
                BlazeLoader.getLogger().logDetail("Stopped mod: " + mod.getModName());
            }catch(Exception e){
                iterator.remove();
                BlazeLoader.getLogger().logDetail("Could not stop mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
    }

    public static void tick(boolean isPreTick){
        ApiBase.theProfiler.startSection("tick_mods");
        for(Mod mod : loadedMods){
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            if(isPreTick){
                mod.eventPreTick();
            }else{
                mod.eventPostTick();
            }
            ApiBase.theProfiler.endSection();
        }
        ApiBase.theProfiler.endSection();
    }

    public static GuiScreen onGui(GuiScreen gui){
        GuiScreen newGui = gui;
        for(Mod mod : loadedMods){
            if(newGui == gui){
                newGui = mod.eventDisplayGui(gui, false);
            }else{
                newGui = mod.eventDisplayGui(gui, true);
            }
        }
        return newGui;
    }

    public static void startSection(String name){
        for(Mod mod : loadedMods){
            mod.eventProfilerStart(name);
        }
    }

    public static void endSection(String name){
        for(Mod mod : loadedMods){
            mod.eventProfilerEnd(name);
        }
    }

    public static void loadWorld(WorldClient par1WorldClient, String par2Str){
        for(Mod mod : loadedMods){
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
    }

    public static void unloadWorld(){
        for(Mod mod : loadedMods){
            mod.eventUnloadWorld();
        }
    }

    public static void eventPlayerLogin(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            mod.eventPlayerLogin(player);
        }
    }

    public static void eventPlayerLogout(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            mod.eventPlayerLogout(player);
        }
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){
        for(Mod mod : loadedMods){
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
        }
    }

    public static void eventClientPlayerDeath(){
        for(Mod mod : loadedMods){
            mod.eventClientPlayerDeath();
        }
    }

    public static void eventTickServerWorld(WorldServer world){
        for(Mod mod : loadedMods){
            mod.eventTickServerWorld(world);
        }
    }
}
