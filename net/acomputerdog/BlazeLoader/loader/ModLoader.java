package net.acomputerdog.BlazeLoader.loader;

import net.acomputerdog.BlazeLoader.mod.Mod;

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
    public static Class[] loadPlugins(File searchDir){
        if(!searchDir.exists() || !searchDir.isDirectory()){
            System.out.println("[BlazeLoader] Invalid mod search directory: " + searchDir.getAbsolutePath());
            return new Class[0];
        }else{
            File[] potentialPlugins = searchDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");
                }
            });
            List<Class> mods = new ArrayList<Class>();
            for(File potentialPluginFile : potentialPlugins){
                try{
                    ClassLoader loader = new URLClassLoader(new URL[]{potentialPluginFile.toURI().toURL()}, ModLoader.class.getClassLoader());
                    ZipFile zipFile = new ZipFile(potentialPluginFile);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while(entries.hasMoreElements()){
                        ZipEntry entry = entries.nextElement();
                        if(entry.getName().endsWith(".class")){
                            try{
                                Class modClass = loader.loadClass(entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6));
                                if(modClass.isAssignableFrom(Mod.class)){
                                    mods.add(modClass);
                                    System.out.println("[BlazeLoader] Loaded plugin: " + modClass.getName());
                                }
                            }catch(ReflectiveOperationException e){
                                System.out.println("[BlazeLoader] Skipping corrupt mod in: " + potentialPluginFile.getAbsolutePath());
                            }
                        }
                    }
                }catch(IOException e){
                    System.out.println("[BlazeLoader] Skipping corrupt zip: " + potentialPluginFile.getAbsolutePath());
                }
            }
            return (Class[])mods.toArray();
        }
    }
}
