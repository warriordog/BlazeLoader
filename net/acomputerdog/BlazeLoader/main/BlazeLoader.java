package net.acomputerdog.BlazeLoader.main;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;

import java.io.File;

public final class BlazeLoader {
    private static File mcDir = new File("./");
    private static File modDir = new File("./mods/");

    public static void init(File mainDir){
        log("Starting up...");
        try{
            mcDir = mainDir;
            modDir = new File(mcDir, "/mods/");
            if(!modDir.exists() || !modDir.isDirectory()){
                log("Mods folder not found!  Creating new folder...");
                log(modDir.mkdir() ? "Succeeded!" : "Failed! Check file permissions!");
            }else{
                loadMods();
                ModList.loadAllMods();
            }
            log("Done loading with no issues.");
        }catch(Exception e){
            log("Exception occurred while starting BlazeLoader!");
            e.printStackTrace();
        }
    }

    private static void loadMods(){
        log("Loading mods from: " + modDir.getAbsolutePath());
        ModLoader.loadModsToList(modDir);
        log("Mod loading complete.");
    }

    public static void log(String message){
        System.out.println("[BlazeLoader] " + message);
    }
}
