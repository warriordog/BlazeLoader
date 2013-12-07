package net.acomputerdog.BlazeLoader.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.api.tick.ApiTick;
import net.acomputerdog.BlazeLoader.main.command.CommandBL;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.acomputerdog.BlazeLoader.util.logger.BLLogger;
import net.acomputerdog.BlazeLoader.util.logger.ELogLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class of BlazeLoader.  Contains various internal fields and methods.
 */
public final class BlazeLoader {
    public static int currFreeBlockId = 1;
    public static int currFreeItemId = 1;
    public static int currFreeEntityId = 1;
    public static boolean isInTick = false;
    public static long numTicks = 0;
    public static CommandHandler commandHandler = new CommandHandler();

    private static Settings settings = new Settings();
    private static BLLogger logger = new BLLogger("BlazeLoader", true, true);
    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();
    private static File settingsFile;
    private static boolean settingsLoaded = false;
    private static boolean hasInit = false;
    private static HashMap<Class, Render> entityMap = null;

    public static Mod currActiveMod = null;

    public static void init(File mainDir){
        ApiBase.theProfiler.startSection("BL_Init");
        if(hasInit){
            throw new IllegalStateException("Attempted to load twice!");
        }else{
            hasInit = true;
        }
        try{
            ApiBase.theProfiler.startSection("SettingsAndFiles");
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
            ELogLevel level = ELogLevel.getByName(Settings.minimumLogLevelName);
            if(level != null){
                Settings.minimumLogLevel = level;
            }

            if(Settings.useVersionMods){
                Settings.modDir = "/versions/" + Version.getMinecraftVersion() + "/mods/";
                Settings.configDir = "/versions/" + Version.getMinecraftVersion() + "/config/";
            }
            ApiBase.modDir = new File(mainDir, Settings.modDir);
            if(!ApiBase.modDir.exists() || !ApiBase.modDir.isDirectory()){
                logger.logWarning("Mods folder not found!  Creating new folder...");
                logger.logDetail(ApiBase.modDir.mkdirs() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
            }
            ApiBase.configDir = new File(mainDir, Settings.configDir);
            if(!ApiBase.configDir.exists() || !ApiBase.configDir.isDirectory()){
                logger.logWarning("Config folder not found!  Creating new folder...");
                logger.logDetail(ApiBase.configDir.mkdirs() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
            }

            new CommandBL();

            ApiBase.theProfiler.endStartSection("Mod Loading");
            ApiTick.gameTimer = getTimer();
            try{
                logger.logInfo("Loading mods...");
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
            ApiBase.theProfiler.endSection();
        }catch(Exception e){
            logger.logFatal("Exception occurred while starting BlazeLoader!");
            e.printStackTrace();
            shutdown(1);
        }
        ApiBase.theProfiler.endSection();
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
        while(currFreeBlockId < Block.blocksList.length && Block.blocksList[currFreeBlockId] != null){
            currFreeBlockId++;
        }
        if(Block.blocksList[currFreeBlockId] != null){
            currFreeBlockId = 1;
            while(currFreeBlockId < Block.blocksList.length && Block.blocksList[currFreeBlockId] != null){
                currFreeBlockId++;
            }
            if(Block.blocksList[currFreeBlockId] != null){
                throw new RuntimeException("No free block IDs available!");
            }
        }
        return currFreeBlockId;
    }

    public static int resetFreeBlockId(){
        currFreeBlockId = 1;
        return updateFreeBlockId();
    }

    public static int updateFreeItemId(){
        while(currFreeItemId < Item.itemsList.length && Item.itemsList[currFreeItemId] != null){
            currFreeItemId++;
        }
        if(Item.itemsList[currFreeItemId] != null){
            currFreeItemId = 1;
            while(currFreeItemId < Item.itemsList.length && Item.itemsList[currFreeItemId] != null){
                currFreeItemId++;
            }
            if(Item.itemsList[currFreeItemId] != null){
                throw new RuntimeException("No free Item IDs available!");
            }
        }
        return currFreeItemId;
    }

    public static int resetFreeItemId(){
        currFreeItemId = 1;
        return updateFreeItemId();
    }

    public static void loadSettings(){
        settingsLoaded = true;
        try {
            settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
            if(settings == null){
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
        if(!settingsLoaded){
            settingsLoaded = true;
            loadSettings();
        }
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile)));
            gson.toJson(settings, writer);
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
        return ApiBase.theMinecraft.timer;
    }

    public static void shutdown(int code){
        try{
            Minecraft.getMinecraft().shutdown();
            Thread.currentThread().join(100);
        }catch(Exception ignored){}
        System.exit(code);
    }


    public static HashMap<Class, Render> getEntityRenderMap(){
        if(entityMap == null){
            for(Field f : RenderManager.class.getDeclaredFields()){
                if(Map.class.isAssignableFrom(f.getType())){
                    try{
                        f.setAccessible(true);
                        entityMap = (HashMap<Class, Render>)f.get(RenderManager.instance);
                    }catch(Exception e){
                        throw new RuntimeException("Could not get entity map!", e);
                    }
                }
            }
            if(entityMap == null){
                throw new RuntimeException("Could not find entity map!");
            }
        }
        return entityMap;
    }

}
