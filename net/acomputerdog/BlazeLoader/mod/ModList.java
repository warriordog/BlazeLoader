package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.GuiScreen;

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

    public static void load(){
        Iterator<Class> iterator = unloadedMods.iterator();
        while(iterator.hasNext()){
            Class cls = iterator.next();
            Mod mod = null;
            try {
                mod = (Mod)cls.newInstance();
                if(mod.isCompatibleWithBLVersion()){
                    mod.load();
                    loadedMods.add(mod);
                    BlazeLoader.log("Initialized mod: " + mod.getModName());
                }else{
                    iterator.remove();
                    BlazeLoader.log("Mod " + mod.getModName() + " is not compatible!  Unloading!");
                }
            } catch (Exception e){
                if(mod != null){
                    loadedMods.remove(mod);
                }
                BlazeLoader.log("Could not initialize mod: " + cls.getName());
                e.printStackTrace();
            } finally{
                iterator.remove();
            }
        }
    }

    public static void start(){
        BlazeLoader.updateFreeBlockId();
        BlazeLoader.updateFreeItemId();
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            try{
                mod.start();
                BlazeLoader.log("Started mod: " + mod.getModName());
            }catch(Exception e){
                iterator.remove();
                BlazeLoader.log("Could not start mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
    }

    public static void stop(){
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            try{
                mod.stop();
                BlazeLoader.log("Stopped mod: " + mod.getModName());
            }catch(Exception e){
                iterator.remove();
                BlazeLoader.log("Could not stop mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
    }

    public static void tick(boolean isPreTick){
        for(Mod mod : loadedMods){
            if(isPreTick){
                mod.eventPreTick();
            }else{
                mod.eventPostTick();
            }
        }
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
}
