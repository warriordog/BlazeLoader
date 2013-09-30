package net.acomputerdog.BlazeLoader.main;

import net.acomputerdog.BlazeLoader.api.ApiBase;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;

import java.io.File;

/**
 * Main class of BlazeLoader
 */
public final class BlazeLoader {
    //private static File mcDir = new File("./");
    //private static File modDir = new File("./mods/");

    public static void init(File mainDir){
        log("Starting up...");
        try{
            ApiBase.mainDir = mainDir;
            ApiBase.modDir = new File(ApiBase.mainDir, "/mods/");
            if(!ApiBase.modDir.exists() || !ApiBase.modDir.isDirectory()){
                log("Mods folder not found!  Creating new folder...");
                log(ApiBase.modDir.mkdir() ? "Succeeded!" : "Failed! Check file permissions!");
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
        log("Loading mods from: " + ApiBase.modDir.getAbsolutePath());
        ModLoader.loadModsToList(ApiBase.modDir);
        log("Mod loading complete.");
    }

    public static void log(String message){
        System.out.println("[BlazeLoader] " + message);
    }
}
