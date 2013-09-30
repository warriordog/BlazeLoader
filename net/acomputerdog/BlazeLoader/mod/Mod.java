package net.acomputerdog.BlazeLoader.mod;

import net.minecraft.src.GuiScreen;

/**
 * Superclass to be extended by mods that wish to use BlazeLoader functionality.  Methods have default implementations, but some should be overridden.
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
     * Called when all mod is loaded.  Called before game is loaded.
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
    public void onPreTick(){}

    /**
     * Called at the end of a game tick.
     */
    public void onPostTick(){}

    /**
     *  Called when a GUI is about to be displayed.  Mods should return param gui unless they wish to override the GUI displayed.
     *  Mods can return null to block a GUI from loading.
     * @param gui The gui that is being displayed
     * @param isSet Has the display GUI been set by another mod.
     * @return Return the GUI to actually display
     */
    public GuiScreen onDisplayGui(GuiScreen gui, boolean isSet){return gui;}

    /**
     * Returns true if: obj != null and obj == this or obj.getModId() == this.getModId().
     * @param obj Object to compare to.
     * @return If obj is a mod of the same type as this mod.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(obj == null)return false;
        return obj instanceof Mod && ((Mod) obj).getModId().equals(this.getModId());
    }
}
