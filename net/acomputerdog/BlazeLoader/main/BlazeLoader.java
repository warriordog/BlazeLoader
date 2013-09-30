package net.acomputerdog.BlazeLoader.main;

import net.acomputerdog.BlazeLoader.api.ApiBase;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.minecraft.src.Block;
import net.minecraft.src.Item;

import java.io.File;

/**
 * Main class of BlazeLoader
 */
public final class BlazeLoader {
    public static int freeBlockId = 1;
    public static int freeItemId = 1;

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
                ModList.load();
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

    public static int updateFreeBlockId(){
        while(freeBlockId < Block.blocksList.length && Block.blocksList[freeBlockId] != null){
            freeBlockId++;
        }
        if(Block.blocksList[freeBlockId] != null){
            freeBlockId = 1;
            while(freeBlockId < Block.blocksList.length && Block.blocksList[freeBlockId] != null){
                freeBlockId++;
            }
            if(Block.blocksList[freeBlockId] != null){
                throw new RuntimeException("No free block IDs available!");
            }
        }
        return freeBlockId;
    }

    public static int resetFreeBlockId(){
        freeBlockId = 1;
        return updateFreeBlockId();
    }

    public static int updateFreeItemId(){
        while(freeItemId < Item.itemsList.length && Item.itemsList[freeItemId] != null){
            freeItemId++;
        }
        if(Item.itemsList[freeItemId] != null){
            freeItemId = 1;
            while(freeItemId < Item.itemsList.length && Item.itemsList[freeItemId] != null){
                freeItemId++;
            }
            if(Item.itemsList[freeItemId] != null){
                throw new RuntimeException("No free Item IDs available!");
            }
        }
        return freeItemId;
    }

    public static int resetFreeItemId(){
        freeItemId = 1;
        return updateFreeItemId();
    }

}
