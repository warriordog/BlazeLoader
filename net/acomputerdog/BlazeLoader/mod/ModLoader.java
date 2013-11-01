package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles detecting and loading mods.
 */
public class ModLoader {
    public static void loadModsToList(File searchDir){
        loadMods(searchDir, ModList.getUnloadedMods());
    }

    public static void loadMods(File searchDir, List<Class> modList){
        ApiBase.theProfiler.startSection("load_mods");
        if(!searchDir.exists() || !searchDir.isDirectory()){
            ApiBase.theProfiler.startSection("create_dir");
            BlazeLoader.getLogger().logError("Invalid mod search directory: " + searchDir.getAbsolutePath());
            ApiBase.theProfiler.endSection();
        }else{
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
            for(File modZip : zips){
                ApiBase.theProfiler.startSection("jar_" + modZip.getName());
                loadZip(modZip, modClassNames, loaderURLs);
                ApiBase.theProfiler.endSection();
            }
            ClassLoader loader = new URLClassLoader(loaderURLs.toArray(new URL[loaderURLs.size()]), ModLoader.class.getClassLoader());
            for(String modClassName : modClassNames){
                loadClass(modClassName, loader, modList);
            }
            ApiBase.theProfiler.endSection();
        }
        ApiBase.theProfiler.endSection();
    }

    private static void loadZip(File modZip, List<String> modClassNames, List<URL> loaderURLs){
        try{
            loaderURLs.add(modZip.toURI().toURL());
            ZipFile zipFile = new ZipFile(modZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class")){
                    modClassNames.add(entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6));
                }
            }
        }catch(IOException e){
            BlazeLoader.getLogger().logWarning("Skipping corrupt zip: " + modZip.getName());
        }catch(Exception e){
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod in: " + modZip.getName());
        }
    }

    private static void loadClass(String className, ClassLoader loader, List<Class> modList){
        try{
            Class modClass = loader.loadClass(className);
            if(Mod.class.isAssignableFrom(modClass)){
                modList.add(modClass);
                BlazeLoader.getLogger().logDetail("Loaded mod: " + modClass.getName() + ".");
            }
        }catch(Exception e){
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod.");
        }
    }

}
