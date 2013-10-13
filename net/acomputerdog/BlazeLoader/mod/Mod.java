package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.WorldClient;

/**
 * Base class of mods.  Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * event... methods can be overridden to respond to game events.
 */
@SuppressWarnings({"UnusedParameters", "EmptyMethod"})
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
    public void eventLoadWorld(WorldClient world, String message){}

    /**
     * Called when the current world is unloaded.
     */
    public void eventUnloadWorld(){}

    /**
     * Called when a player logs into the game.
     * @param player The player logging in.
     */
    public void eventPlayerLogin(EntityPlayerMP player){}

    /**
     * Called when a player logs out of the game.
     * @param player The player logging out.
     */
    public void eventPlayerLogout(EntityPlayerMP player){}

    @Deprecated
    /**
     * Called when a player spawns or respawns.  Only works for OTHER players, and only in LAN games!
     * @param oldPlayer The player being respawned.
     * @param newPlayer The newly spawned player.
     * @param dimension The dimension (world) to spawn the player in.
     * @param causedByDeath Is the respawn triggered by death?
     */
    public void eventPlayerSpawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){}

    /**
     * Called when a non-local player respawns.  Only works for other players.
     * @param oldPlayer The player that died.
     * @param newPlayer The player being spawned.
     * @param dimension The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void eventOtherPlayerRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath){}

    /**
     * Called when the client player dies.
     */
    public void eventClientPlayerDeath(){}

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
