package net.acomputerdog.BlazeLoader.mod;

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

public class ModLoader {

    public static Class[] loadMods(File searchDir){
        List<Class> mods = new ArrayList<Class>();
        loadMods(searchDir, mods);
        return (Class[])mods.toArray();
    }

    public static void loadMods(File searchDir, List<Class> modList){
        if(!searchDir.exists() || !searchDir.isDirectory()){
            BlazeLoader.log("Invalid mod search directory: " + searchDir.getAbsolutePath());
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
                            //System.out.println(modClass.getName() + ", " + Mod.class.isAssignableFrom(modClass));
                            if(Mod.class.isAssignableFrom(modClass)){
                                modList.add(modClass);
                                BlazeLoader.log("Loaded mod: " + modClass.getName() + " from zip: " + modZip.getName());
                            }
                        }
                    }
                }catch(IOException e){
                    BlazeLoader.log("Skipping corrupt zip: " + modZip.getName());
                }catch(ReflectiveOperationException e){
                    BlazeLoader.log("Skipping corrupt mod in: " + modZip.getName());
                }
            }
        }
        //BlazeLoader.log("Loaded " + ModList.sizeUnloaded() + " mods.");
    }

    public static void loadModsToList(File searchDir){
        loadMods(searchDir, ModList.getUnloadedMods());
    }

}
