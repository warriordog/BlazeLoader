package net.acomputerdog.BlazeLoader.mod;

/**
 * Base class of mods.  Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * event... methods can be overridden to respond to game events.
 * Event methods that reference Minecraft classes have "No-args" versions that can be used for mods that wish to avoid being specific to particular Minecraft versions.
 * If a normal event method is overridden, it's no-args version will NOT be triggered.
 */
public abstract class Mod {
    /**
     * Returns ID used to identify this mod internally, even among different versions of the same mod.  Mods should override.
     * --This should never be changed after the mod has been released!--
     *
     * @return Returns the id of the mod.
     */
    public String getModId() {
        return this.getClass().getName();
    }

    /**
     * Returns the user-friendly name of the mod.  Mods should override.
     * --Can be changed among versions, so this should not be used to ID mods!--
     *
     * @return Returns user-friendly name of the mod.
     */
    public String getModName() {
        return this.getClass().getSimpleName();
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
     * Returns true if this mod is compatible with the installed version of BlazeLoader.  This should be checked using Version.class.
     * -Called before mod is loaded!  Do not depend on Mod.load()!-
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
        return "No description!";
    }

    /**
     * Called when mod is loaded.  Called before game is loaded.
     */
    public void load() {
    }

    /**
     * Called when mod is started.  Game is fully loaded and can be interacted with.
     */
    public void start() {
    }

    /**
     * Called when mod is stopped.  Game is about to begin shutting down, so mod should release system resources, close streams, etc.
     */
    public void stop() {
    }

    /**
     * Returns true if: obj != null and obj == this or obj.getModId() == this.getModId().
     *
     * @param obj Object to compare to.
     * @return If obj is a mod of the same type as this mod.
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof Mod && ((Mod) obj).getModId().equals(this.getModId());
    }

    /**
     * Returns the ID of the mod.
     *
     * @return Return the value of this.getModId();
     */
    @Override
    public String toString() {
        return this.getModId();
    }
}
