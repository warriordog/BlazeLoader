package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.resource.BLModResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles detecting and loading mods.
 */
public class ModLoader {
    private static Map<String, File> sourceMap = new HashMap<String, File>();

    public static File getModSource(String clsName) {
        return sourceMap.get(clsName);
    }

    public static void loadModsToList(File searchDir) {
        loadMods(searchDir, ModList.getUnloadedMods());
    }

    public static void loadMods(File searchDir, List<Class<? extends Mod>> modList) {
        System.out.println("a");
        ApiGeneral.theProfiler.startSection("load_mods");
        if (!searchDir.exists() || !searchDir.isDirectory()) {
            BlazeLoader.getLogger().logError("Invalid mod search directory: " + searchDir.getAbsolutePath());
        } else {
            File[] contents = searchDir.listFiles();
            if (contents != null) {
                System.out.println("b");
                for (File f : contents) {
                    System.out.println("c");
                    if (f.isDirectory()) {
                        System.out.println("d1");
                        loadMods(f, modList);
                    } else {
                        System.out.println("d2");
                        String name = f.getName();
                        if (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip")) {
                            System.out.println("e");
                            List<URL> loaderURLs = new ArrayList<URL>();
                            List<String> modClassNames = new ArrayList<String>();
                            loadZip(f, modClassNames, loaderURLs);
                            ClassLoader loader = new URLClassLoader(loaderURLs.toArray(new URL[loaderURLs.size()]), ModLoader.class.getClassLoader());
                            for (String modClassName : modClassNames) {
                                System.out.println("f");
                                loadClass(modClassName, loader, modList);
                            }
                        }
                    }
                }
            }
        }
        ApiGeneral.theProfiler.endSection();
    }

    public static void loadZip(File modZip, List<String> modClassNames, List<URL> loaderURLs) {
        try {
            loaderURLs.add(modZip.toURI().toURL());
            ZipFile zipFile = new ZipFile(modZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String name = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6);
                    modClassNames.add(name);
                    sourceMap.put(name, modZip);
                }
            }
        } catch (IOException e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt zip: " + modZip.getName());
        } catch (Exception e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod in: " + modZip.getName());
        }
    }

    private static void loadClass(String className, ClassLoader loader, List<Class<? extends Mod>> modList) {
        try {
            Class modClass = loader.loadClass(className);
            if (Mod.class.isAssignableFrom(modClass) && !Mod.class.equals(modClass)) {
                modList.add(modClass);
                BlazeLoader.getLogger().logDetail("Loaded mod: " + modClass.getName() + ".");
            }
        } catch (Exception e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod.");
        }
    }

    public static void loadModAsResourcePack(ModData mod) {
        List<IResourcePack> defaultResourcePacks = Minecraft.getMinecraft().getDefaultResourcePacks();
        BLModResourcePack pack = new BLModResourcePack(mod);
        if (!defaultResourcePacks.contains(pack))
            defaultResourcePacks.add(pack);
    }
}
