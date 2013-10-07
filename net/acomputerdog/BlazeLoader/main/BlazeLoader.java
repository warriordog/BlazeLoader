package net.acomputerdog.BlazeLoader.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.api.tick.ApiTick;
import net.acomputerdog.BlazeLoader.fix.FixManager;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Timer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Main class of BlazeLoader.  Contains various internal fields and methods.
 */
public final class BlazeLoader {
    public static int freeBlockId = 1;
    public static int freeItemId = 1;
    public static boolean isInTick = false;

    private static File apiDir;
    private static Settings theSettings = new Settings();
    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
    private static File settingsFile;
    private static boolean hasLoaded = false;

    public static void init(File mainDir){
        log("Starting up...");
        try{
            ApiBase.mainDir = mainDir;
            apiDir = new File(mainDir, "/BL/");
            if(!apiDir.exists() && !apiDir.mkdir()){
                log("[ERROR] Could not create main API directory!");
            }
            settingsFile = new File(apiDir, "BLConfig.json");
            if(!settingsFile.exists()){
                BlazeLoader.log("Config file does not exist!  It will be created.");
                saveSettings();
            }
            loadSettings();
            saveSettings();
            ApiBase.modDir = new File(mainDir, Settings.modDir);
            ApiTick.gameTimer = getTimer();
            FixManager.onInit();
            if(!ApiBase.modDir.exists() || !ApiBase.modDir.isDirectory()){
                log("Mods folder not found!  Creating new folder...");
                log(ApiBase.modDir.mkdir() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
            }
            if(Settings.enableMods){
                loadMods();
                ModList.load();
            }else{
                log("Mods are disabled in config, skipping mod loading.");
            }
            log("Mods loaded with no issues.");
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

    @SuppressWarnings("WeakerAccess")
    public static void loadSettings(){
        hasLoaded = true;
        try {
            theSettings = gson.fromJson(new FileReader(settingsFile), Settings.class);
            if(theSettings == null){
                saveSettings();
            }
        }catch (FileNotFoundException e) {
            saveSettings();
        } catch (JsonParseException e){
            saveSettings();
        } catch (Exception e){
            BlazeLoader.log("Error occurred reading settings!");
            e.printStackTrace();
        }
    }

    public static void saveSettings(){
        if(!hasLoaded){
            hasLoaded = true;
            loadSettings();
        }
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile)));
            gson.toJson(theSettings, writer);
            writer.close();
        } catch(IOException e){
            BlazeLoader.log("[ERROR] Could not save settings!");
            e.printStackTrace();
        } finally{
            if(writer != null){
                writer.close();
            }
        }
    }

    private static Timer getTimer(){
        for(Field f : Minecraft.class.getDeclaredFields()){
            if(Timer.class.isAssignableFrom(f.getType())) try {
                f.setAccessible(true);
                return (Timer)f.get(ApiBase.theMinecraft);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not get Timer field!", e);
            }
        }
        throw new RuntimeException("Could not get Timer field!");
    }
}
