package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.resource.BLModResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;

import java.io.File;
import java.io.FilenameFilter;
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
        ApiBase.theProfiler.startSection("load_mods");
        if (!searchDir.exists() || !searchDir.isDirectory()) {
            BlazeLoader.getLogger().logError("Invalid mod search directory: " + searchDir.getAbsolutePath());
        } else {
            ApiBase.theProfiler.startSection("find_jars");
            File[] zips = searchDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");
                }
            });
            ApiBase.theProfiler.endStartSection("scan_jars");
            List<URL> loaderURLs = new ArrayList<URL>();
            List<String> modClassNames = new ArrayList<String>();
            for (File modZip : zips) {
                ApiBase.theProfiler.startSection("jar_" + modZip.getName());
                loadZip(modZip, modClassNames, loaderURLs);
                ApiBase.theProfiler.endSection();
            }
            ClassLoader loader = new URLClassLoader(loaderURLs.toArray(new URL[loaderURLs.size()]), ModLoader.class.getClassLoader());
            for (String modClassName : modClassNames) {
                loadClass(modClassName, loader, modList);
            }
            ApiBase.theProfiler.endSection();
        }
        ApiBase.theProfiler.endSection();
    }

    private static void loadZip(File modZip, List<String> modClassNames, List<URL> loaderURLs) {
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
            if (Mod.class.isAssignableFrom(modClass)) {
                modList.add(modClass);
                BlazeLoader.getLogger().logDetail("Loaded mod: " + modClass.getName() + ".");
            }
        } catch (Exception e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod.");
        }
    }

    public static void loadModAsResourcePack(ModData mod)
    {
        List<IResourcePack> defaultResoucePacks = Minecraft.getMinecraft().getDefaultResourcePacks();
        BLModResourcePack pack = new BLModResourcePack(mod);
        if (!defaultResoucePacks.contains(pack))
            defaultResoucePacks.add(pack);
    }
}
