package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores and loads mods, distributes events, and allows access to loaded mods.
 */
public class ModList {
    private static final List<Mod> loadedMods = new ArrayList<Mod>();
    private static final List<Class> unloadedMods = new ArrayList<Class>();

    public static List<Mod> getLoadedMods(){
        return loadedMods;
    }

    public static List<Class> getUnloadedMods(){
        return unloadedMods;
    }

    private static Mod getCompatibleModFromList(Mod mod){
        if(mod != null){
            for(Mod m : loadedMods){
                BlazeLoader.currActiveMod = mod;
                if (mod.getModId().equals(m.getModId()))return m;
            }
            BlazeLoader.currActiveMod = null;
        }
        return null;
    }

    public static void load(){
        ApiBase.theProfiler.startSection("init_mods");
        Iterator<Class> iterator = unloadedMods.iterator();
        while(iterator.hasNext()){
            Class cls = iterator.next();
            Mod mod = null;
            try {
                mod = (Mod)cls.newInstance();
                if(mod.isCompatibleWithBLVersion()){
                    Mod sameMod = getCompatibleModFromList(mod);
                    boolean useNewMod = true;
                    if(sameMod != null){
                        BlazeLoader.getLogger().logWarning("Duplicate mod: " + mod.getModName() + "!  Newest version will be used!");
                        if(sameMod.getIntModVersion() < mod.getIntModVersion()){
                            sameMod.stop();
                            loadedMods.remove(sameMod);
                        }else{
                            useNewMod = false;
                        }
                    }
                    if(useNewMod){
                        mod.load();
                        loadedMods.add(mod);
                        BlazeLoader.getLogger().logDetail("Initialized mod: [" + mod.getModName() + "] version: [" + mod.getStringModVersion() + "].");
                    }
                }else{
                    BlazeLoader.getLogger().logError("Mod " + mod.getModName() + " is not compatible!  Unloading!");
                }
                iterator.remove();
            } catch (Exception e){
                if(mod != null){
                    loadedMods.remove(mod);
                }
                BlazeLoader.getLogger().logError("Could not initialize mod: " + cls.getName());
                e.printStackTrace();
                iterator.remove();
            }
        }
        ApiBase.theProfiler.endSection();
    }

    public static void start(){
        ApiBase.theProfiler.startSection("start_mods");
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            BlazeLoader.currActiveMod = mod;
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            try{
                mod.start();
            }catch(Exception e){
                iterator.remove();
                e.printStackTrace();
            }
            ApiBase.theProfiler.endSection();
        }
        BlazeLoader.currActiveMod = null;
        ApiBase.theProfiler.endSection();
    }

    public static void stop(){
        Iterator<Mod> iterator = loadedMods.iterator();
        while(iterator.hasNext()){
            Mod mod = iterator.next();
            BlazeLoader.currActiveMod = mod;
            try{
                mod.stop();
                BlazeLoader.getLogger().logDetail("Stopped mod: " + mod.getModName());
            }catch(Exception e){
                iterator.remove();
                BlazeLoader.getLogger().logDetail("Could not stop mod: " + mod.getModName());
                e.printStackTrace();
            }
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void tick(){
        ApiBase.theProfiler.startSection("tick_mods");
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            ApiBase.theProfiler.startSection("mod_" + mod.getModId());
            mod.eventTick();
            ApiBase.theProfiler.endSection();
        }
        BlazeLoader.currActiveMod = null;
        ApiBase.theProfiler.endSection();
    }

    public static GuiScreen onGui(GuiScreen gui){
        GuiScreen newGui = gui;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            if(newGui == gui){
                newGui = mod.eventDisplayGui(gui, false);
            }else{
                newGui = mod.eventDisplayGui(gui, true);
            }
        }
        BlazeLoader.currActiveMod = null;
        return newGui;
    }

    public static void startSection(String name){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventProfilerStart(name);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void endSection(String name){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventProfilerEnd(name);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void loadWorld(WorldClient par1WorldClient, String par2Str){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventLoadWorld(par1WorldClient, par2Str);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void unloadWorld(){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventUnloadWorld();
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventPlayerLogin(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventPlayerLogin(player);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventPlayerLogout(EntityPlayerMP player){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventPlayerLogout(player);
        }
        BlazeLoader.currActiveMod = null;
    }

    @Deprecated
    public static void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventOtherPlayerRespawn(oldPlayer, newPlayer, dimension, causedByDeath);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventClientPlayerDeath(){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventClientPlayerDeath();
        }
        BlazeLoader.currActiveMod = null;
    }

    public static void eventTickServerWorld(WorldServer world){
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            mod.eventTickServerWorld(world);
        }
        BlazeLoader.currActiveMod = null;
    }

    public static S0EPacketSpawnObject createSpawnPacket(Entity myEntity){
        S0EPacketSpawnObject packet = null;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            S0EPacketSpawnObject modPacket = mod.createSpawnPacket(myEntity, packet != null);
            if(modPacket != null){
                packet = modPacket;
            }
        }
        BlazeLoader.currActiveMod = null;
        return packet;
    }

    public static boolean eventTickBlocksAndAmbiance(WorldServer server){
        boolean doVanilla = true;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            boolean didHandle = mod.eventTickBlocksAndAmbiance(server, doVanilla);
            if(doVanilla){
                doVanilla = didHandle;
            }
        }
        BlazeLoader.currActiveMod = null;
        return doVanilla;
    }

    public static boolean eventPlayerLoginAttempt(String username, boolean isAllowed){
        boolean allow = isAllowed;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            allow = mod.eventPlayerLoginAttempt(username, isAllowed);
        }
        BlazeLoader.currActiveMod = null;
        return allow;
    }

    public static void addEntityToTracker(EntityTracker tracker, Entity entity){
        boolean isHandled = false;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            boolean didHandle = mod.addEntityToTracker(tracker, entity, isHandled);
            if(didHandle){
                isHandled = true;
            }
        }
        BlazeLoader.currActiveMod = null;
    }

    public static boolean eventPlayerBreakBlock(EntityPlayer player, int x, int y, int z, Block block, int data, boolean allowed){
        boolean isAllowed = allowed;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            isAllowed = mod.eventPlayerBreakBlock(player, x, y, z, block, data, allowed);
        }
        BlazeLoader.currActiveMod = null;
        return isAllowed;
    }

    public static boolean eventPlayerPlaceBlock(EntityPlayer player, int x, int y, int z, Block oldBlock, int oldData, Block newBlock, int newData, boolean allowed){
        boolean isAllowed = allowed;
        for(Mod mod : loadedMods){
            BlazeLoader.currActiveMod = mod;
            isAllowed = mod.eventPlayerPlaceBlock(player, x, y, z, oldBlock, oldData, newBlock, newData, allowed);
        }
        BlazeLoader.currActiveMod = null;
        return isAllowed;
    }

}
