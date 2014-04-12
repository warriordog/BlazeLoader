package net.acomputerdog.BlazeLoader.mod;

/**
 * Base Mod class. All Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * Event interfaces can be added to respond to certain types of events in the game.
 */
public abstract class Mod {
    /**
     * Returns the ID used to identify this mod internally,
     * even between different versions of the same mod.
     * This method should be overriden by your mod and not:
     * 
     * >> This should never be changed after an initial released <<
     *
     * @return Returns the id of the mod.
     */
    public String getModId() {
        return getClass().getName();
    }

    /**
     * Returns the user-friendly name of the mod.
     * This method should be overriden by your mod.
     * 
     * This may be changed between versions and so must NOT
     * be used as a form of identification for you mod.
     *
     * @return Returns user-friendly name of the mod.
     */
    public String getModName() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the version of the mod as an integer for choosing the newer version.
     *
     * @return Return the version of the mod as an integer.
     */
    public int getIntModVersion() {
        return 0;
    }

    /**
     * Gets the version of the mod as a String for display.
     *
     * @return Returns the version of the mod as an integer.
     */
    public String getStringModVersion() {
        return "0.0";
    }

    /**
     * Returns true if this mod is compatible with the installed version of BlazeLoader.
     * This should be checked using the Version class.
     * 
     * >>> This is called before the mod is loaded <<<
     * >>> Do not depend on Mod.load() <<<
     *
     * @return Returns true if the mod is compatible with the installed version of BlazeLoader.
     */
    public boolean isCompatibleWithBLVersion() {
        return true;
    }

    /**
     * Gets a user-friendly description of the mod.
     *
     * @return Return a String representing a user-friendly description of the mod.
     */
    public String getModDescription() {
        return "No description";
    }

    /**
     * Called when mod is loaded.  Called before game is loaded.
     * This method should ideally be used for:
     * 		1. Adding/Replacing blocks
     *      2. Adding KeyBindings
     *      3. Any other changes that should occur before the game initializes
     */
    public void load() {}

    /**
     * Called when mod is started.  Game is fully loaded and can be interacted with.
     * Blocks have already been initialized and had their Icons registered and
     * KeyBindings have been loaded from the games config file.
     * Use this method for last minute changes or for changes with wich you
     * do not want the games own initialization to interfere.
     */
    public void start() {}

    /**
     * Called when mod is stopped.
     * Game is about to shut down, use this as your chance to
     * release system resources, close streams, save stuff, etc.
     */
    public void stop() {}

    /**
     * Checks if two Mod instances are equivalent.
     * Includes a null check, and will only return true if the Id of the other mod matches that of this one.
     *
     * @param obj Object to compare to.
     * @return If obj is a mod of the same type as this mod.
     */
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof Mod && ((Mod) obj).getModId().equals(getModId());
    }

    /**
     * Returns the ID of the mod.
     *
     * @return Returns the value of getModId().
     */
    public String toString() {
        return getModId();
    }
}
