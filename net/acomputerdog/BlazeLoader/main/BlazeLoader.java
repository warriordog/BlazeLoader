package net.acomputerdog.BlazeLoader.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.api.tick.ApiTick;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.acomputerdog.BlazeLoader.util.BLLogger;
import net.minecraft.src.*;

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
    public static long ticks = 0;

    private static Settings theSettings = new Settings();
    private static BLLogger logger = new BLLogger("BlazeLoader", true, true);
    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();
    private static File settingsFile;
    private static boolean hasLoaded = false;

    public static void init(File mainDir){
        try{
            logger.logInfo("BlazeLoader version " + Version.getMinecraftVersion() + "/" + Version.getStringVersion() + " is starting...");
            ApiBase.mainDir = mainDir;
            File apiDir = new File(mainDir, "/BL/");
            if(!apiDir.exists() && !apiDir.mkdir()){
                logger.logError("Could not create main API directory!");
            }
            settingsFile = new File(apiDir, "BLConfig.json");
            if(!settingsFile.exists()){
                logger.logWarning("Config file does not exist!  It will be created.");
                saveSettings();
            }
            loadSettings();
            saveSettings();
            ApiBase.modDir = new File(mainDir, Settings.modDir);
            ApiBase.configDir = new File(mainDir, Settings.configDir);
            ApiTick.gameTimer = getTimer();
            try{
                logger.logInfo("Loading mods...");
                if(!ApiBase.modDir.exists() || !ApiBase.modDir.isDirectory()){
                    logger.logWarning("Mods folder not found!  Creating new folder...");
                    logger.logDetail(ApiBase.modDir.mkdir() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
                }
                if(Settings.enableMods){
                    loadMods();
                    ModList.load();
                }else{
                    logger.logDetail("Mods are disabled in config, skipping mod loading.");
                }
                logger.logInfo("Mods loaded with no issues.");
            }catch(Exception e){
                logger.logError("Caught exception loading mods!");
                e.printStackTrace();
            }
        }catch(Exception e){
            logger.logFatal("Exception occurred while starting BlazeLoader!");
            e.printStackTrace();
            shutdown(1);
        }

    }

    private static void loadMods(){
        logger.logDetail("Loading mods from: " + ApiBase.modDir.getAbsolutePath());
        ModLoader.loadModsToList(ApiBase.modDir);
        logger.logInfo("Mod loading complete.");
    }

    public static BLLogger getLogger(){
        return logger;
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
            logger.logWarning("Format error in settings file; reloading.");
            saveSettings();
        } catch (Exception e){
            logger.logError("Error occurred reading settings!");
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
            logger.logError("Could not save settings!");
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

    public static void shutdown(int code){
        try{
            Minecraft.getMinecraft().shutdown();
            Thread.currentThread().join(100);
        }catch(Exception ignored){}
        System.exit(code);
    }
}
