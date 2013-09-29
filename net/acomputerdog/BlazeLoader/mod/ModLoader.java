package net.acomputerdog.BlazeLoader.mod;

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

public class ModLoader {

    public static Class[] loadMods(File searchDir){
        List<Class> mods = new ArrayList<Class>();
        loadMods(searchDir, mods);
        return (Class[])mods.toArray();
    }

    public static void loadMods(File searchDir, List<Class> modList){
        if(!searchDir.exists() || !searchDir.isDirectory()){
            System.out.println("[BlazeLoader] Invalid mod search directory: " + searchDir.getAbsolutePath());
        }else{
            File[] zips = searchDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");
                }
            });
            //List<Class> mods = new ArrayList<Class>();
            for(File modZip : zips){
                try{
                    ClassLoader loader = new URLClassLoader(new URL[]{modZip.toURI().toURL()}, ModLoader.class.getClassLoader());
                    ZipFile zipFile = new ZipFile(modZip);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while(entries.hasMoreElements()){
                        ZipEntry entry = entries.nextElement();
                        if(entry.getName().endsWith(".class")){
                            Class modClass = loader.loadClass(entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6));
                            if(modClass.isAssignableFrom(Mod.class)){
                                modList.add(modClass);
                                System.out.println("[BlazeLoader] Loaded mod: " + modClass.getName() + " from zip: " + modZip.getName());
                            }
                        }
                    }
                }catch(IOException e){
                    System.out.println("[BlazeLoader] Skipping corrupt zip: " + modZip.getName());
                }catch(ReflectiveOperationException e){
                    System.out.println("[BlazeLoader] Skipping corrupt mod in: " + modZip.getName());
                }
            }
        }
    }

    public static void loadModsToList(File searchDir){
        loadMods(searchDir, ModList.getUnloadedMods());
    }

}
