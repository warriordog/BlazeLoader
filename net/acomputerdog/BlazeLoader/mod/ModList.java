package net.acomputerdog.BlazeLoader.mod;

import java.util.ArrayList;
import java.util.List;

public class ModList {
    private static final List<Mod> loadedMods = new ArrayList<Mod>();
    private static final List<Class> unloadedMods = new ArrayList<Class>();

    public static List<Mod> getLoadedMods(){
        return loadedMods;
    }

    public static List<Class> getUnloadedMods(){
        return unloadedMods;
    }

    public static void addMod(Mod mod){
        loadedMods.add(mod);
    }

    public static void addMod(Class mod){
        unloadedMods.add(mod);
    }
}
