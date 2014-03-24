package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.event.EventHandler;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;

import java.io.File;
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
                        EventHandler.addMod(mod);
                        mod.load();
                        loadedMods.add(mod);
                        ModData data = new ModData(mod, cls, new File(cls.getProtectionDomain().getCodeSource().getLocation().toURI()), mod.getModId());
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

}
