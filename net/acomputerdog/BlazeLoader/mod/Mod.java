package net.acomputerdog.BlazeLoader.mod;

import net.minecraft.src.*;

/**
 * Base class of mods.  Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * event... methods can be overridden to respond to game events.
 * Event methods that reference Minecraft classes have "No-args" versions that can be used for mods that wish to avoid being specific to particular Minecraft versions.
 * If a normal event method is overridden, it's no-args version will NOT be triggered.
 */
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
     * Called when the game is ticked.
     */
    public void eventTick(){}

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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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
     * Called when WorldServer.tickBlocksAndAmbiance is called.  Return true to disable the vanilla handling.
     * @param server The server calling tickBlocksAndAmbiance
     * @param isHandled Set to true if a mod has already handled this event (so this mod does not have to re-handle vanilla behavior)
     * @return Return true to disable the vanilla event handling.
     */
    public boolean eventTickBlocksAndAmbiance(WorldServer server, boolean isHandled){
        return this.eventTickBlocksAndAmbiance(isHandled);
    }

    /**
     * No-args version of eventTickBlocksAndAmbiance(WorldServer, boolean)
     * @param isHandled Set to true if a mod has already handled this event (so this mod does not have to re-handle vanilla behavior)
     * @return Return true to disable the vanilla event handling.
     */
    public boolean eventTickBlocksAndAmbiance(boolean isHandled){
        return false;
    }

    /**
     * Called when a player attempts to log in.  This is after the game has already checked if the user is valid.
     * @param username The username of the player attempting to join.
     * @param isAllowed The result of the game's user check.  True if the player is allowed to join.
     * @return Return true to allow the player to join, false to prevent it.
     */
    public boolean eventPlayerLoginAttempt(String username, boolean isAllowed){
        return isAllowed;
    }

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

    /**
     * Creates a spawn packet for the given entity.
     * @param entity The entity to create the spawn packet for.
     * @param isHandled True if another mod has already created a packet for this entity.
     * @return Return a spawn packet for the given entity, or null if none exists.
     */
    public Packet23VehicleSpawn createSpawnPacket(Entity entity, boolean isHandled){
        return null;
    }

    /**
     * Adds an entity to an entity tracker.
     * @param tracker The tracker to add the entity to.
     * @param entity The entity to add.
     * @param isHandled True if another mod has already handled the event.
     * @return Return true if the entity was added, false otherwise.
     */
    public boolean addEntityToTracker(EntityTracker tracker, Entity entity, boolean isHandled){
        return false;
    }
}
