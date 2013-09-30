package net.acomputerdog.BlazeLoader.main;

import net.acomputerdog.BlazeLoader.api.ApiBase;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.minecraft.src.Block;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Main class of BlazeLoader
 */
public final class BlazeLoader {
    //private static File mcDir = new File("./");
    //private static File modDir = new File("./mods/");
    public static int freeBlockIndex = 0;

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

    public static int updateFreeBlockSlot(){
        while(freeBlockIndex < Block.blocksList.length && Block.blocksList[freeBlockIndex] != null){
            freeBlockIndex++;
        }
        if(Block.blocksList[freeBlockIndex] != null){
            freeBlockIndex = 0;
            while(freeBlockIndex < Block.blocksList.length && Block.blocksList[freeBlockIndex] != null){
                freeBlockIndex++;
            }
            if(Block.blocksList[freeBlockIndex] != null){
                throw new RuntimeException("No free block IDs available!");
            }
            //Block[] newBlockList = new Block[Block.blocksList.length + 8];
            //System.arraycopy(Block.blocksList, 0, newBlockList, 0, Block.blocksList.length);
            //setBlockList(newBlockList);
            //freeBlockIndex++;
        }
        return freeBlockIndex;
    }

    private static void setBlockList(Block[] newList){
        try{
            Field[] fields = Block.class.getDeclaredFields();
            for(Field f : fields){
                if(Block[].class.isAssignableFrom(f.getType())){
                    f.setAccessible(true);
                    f.set(null, newList);
                }
            }
        }catch(ReflectiveOperationException e){
            throw new RuntimeException("Could not set BlockList!", e);
        }
    }

    public static int resetFreeBlockSlot(){
        freeBlockIndex = 0;
        return updateFreeBlockSlot();
    }
}
