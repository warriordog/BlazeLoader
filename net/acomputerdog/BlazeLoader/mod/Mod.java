package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

/**
 * Base class of mods.  Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * event... methods can be overridden to respond to game events.
 * Event methods that reference Minecraft classes have "No-args" versions that can be used for mods that wish to avoid being specific to particular Minecraft versions.
 * If a normal event method is overridden, it's no-args version will NOT be triggered.
 */
@Beta(stable = true)
public abstract class Mod {
    /**
     *  Returns ID used to identify this mod internally, even among different versions of the same mod.  Mods should override.
     *  --This should never be changed after the mod has been released!--
     * @return Returns the id of the mod.
     */
    public String getModId(){
        return this.getClass().getName();
    }

    /**
     * Returns the user-friendly name of the mod.  Mods should override.
     * --Can be changed among versions, so this should not be used to ID mods!--
     * @return Returns user-friendly name of the mod.
     */
    public String getModName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Gets the version of the mod as an integer for choosing the newer version.
     * @return Return the version of the mod as an integer.
     */
    public int getIntModVersion(){
        return 0;
    }

    /**
     * Gets the version of the mod as a String for display.
     * @return Returns the version of the mod as an integer.
     */
    public String getStringModVersion(){
        return "0.0";
    }

    /**
     * Returns true if this mod is compatible with the installed version of BlazeLoader.  This should be checked using Version.class.
     * -Called before mod is loaded!  Do not depend on Mod.load()!-
     * @return Returns true if the mod is compatible with the installed version of BlazeLoader.
     */
    public boolean isCompatibleWithBLVersion(){
        return true;
    }

    /**
     * Gets a user-friendly description of the mod.
     * @return Return a String representing a user-friendly description of the mod.
     */
    public String getModDescription(){
        return "No description!";
    }

    /**
     * Called when mod is loaded.  Called before game is loaded.
     */
    public void load(){}

    /**
     * Called when mod is started.  Game is fully loaded and can be interacted with.
     */
    public void start(){}

    /**
     * Called when mod is stopped.  Game is about to begin shutting down, so mod should release system resources, close streams, etc.
     */
    public void stop(){}

    /**
     * Called at the start of a game tick.
     * -Currently DISABLED-
     */
    public void eventPreTick(){}

    /**
     * Called at the end of a game tick.
     */
    public void eventPostTick(){}

    /**
     *  Called when a GUI is about to be displayed.  Mods should return param gui unless they wish to override the GUI displayed.
     *  Mods can return null to block a GUI from loading.
     * @param gui The gui that is being displayed
     * @param isSet Has the display GUI been set by another mod.
     * @return Return the GUI to actually display
     */
    public GuiScreen eventDisplayGui(GuiScreen gui, boolean isSet){
        this.eventDisplayGui();
        return gui;
    }

    /**
     * Called when a profiler section is started.  Mods are notified BEFORE profiler.
     * @param sectionName Name of the profiler section started.
     */
    public void eventProfilerStart(String sectionName){}

    /**
     * Called when a profiler section is ended.  Mods are notified AFTER profiler.
     * @param sectionName Name of the profiler section ended.
     */
    public void eventProfilerEnd(String sectionName){}

    /**
     * Called when a world is loaded.
     * @param world The world being loaded.
     * @param message The message displayed to the user on the loading screen.
     */
    public void eventLoadWorld(WorldClient world, String message){
        this.eventLoadWorld();
    }

    /**
     * Called when the current world is unloaded.
     */
    public void eventUnloadWorld(){}

    /**
     * Called when a player logs into the game.
     * @param player The player logging in.
     */
    public void eventPlayerLogin(EntityPlayerMP player){
        this.eventPlayerLogin();
    }

    /**
     * Called when a player logs out of the game.
     * @param player The player logging out.
     */
    public void eventPlayerLogout(EntityPlayerMP player){
        this.eventPlayerLogout();
    }

    @Beta(stable = false)
    /**
     * Called when a non-local player respawns.  Only works for other players.
     * @param oldPlayer The player that died.
     * @param newPlayer The player being spawned.
     * @param dimension The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void eventOtherPlayerRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){
        this.eventOtherPlayerRespawn();
    }

    /**
     * Called when the client player dies.
     */
    public void eventClientPlayerDeath(){}

    /**
     * Called when a GUI is about to be displayed.  No-args version of eventDisplayGui(GuiScreen, boolean)
     */
    public void eventDisplayGui(){}

    /**
     * Called when a world is loaded.  No-args version of eventLoadWorld(WorldClient, String)
     */
    public void eventLoadWorld(){}

    /**
     * Called when a player logs into the game.  No-args version of eventPlayerLogin(EntityPlayerMP)
     */
    public void eventPlayerLogin(){}

    /**
     * Called when a player logs out of the game. No-args version of eventPlayerLogout(EntityPlayerMP)
     */
    public void eventPlayerLogout(){}

    /**
     * Called when a non-local player respawns.  Only works for other players.
     * No-args version of eventOtherPlayerRespawn(EntityPlayerMP, EntityPlayerMP, int, boolean)
     */
    public void eventOtherPlayerRespawn(){}

    /**
     * Called when a server-side world is ticked.
     * @param world The world being ticked.
     */
    public void eventTickServerWorld(WorldServer world){
        this.eventTickServerWorld();
    }

    /**
     * Called when a server-side world is ticked.  No-args version of eventTickServerWorld(WorldServer)
     */
    public void eventTickServerWorld(){}

    /**
     * Returns true if: obj != null and obj == this or obj.getModId() == this.getModId().
     * @param obj Object to compare to.
     * @return If obj is a mod of the same type as this mod.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(obj == null)return false;
        if(!(obj instanceof Mod))return false;
        return ((Mod)obj).getModId().equals(this.getModId());
    }

    /**
     * Returns the ID of the mod.
     * @return Return the value of this.getModId();
     */
    @Override
    public String toString() {
        return this.getModId();
    }
}
