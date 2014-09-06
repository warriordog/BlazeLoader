package com.blazeloader.api.core.mod;

import com.mumfrey.liteloader.LiteMod;

/**
 * Base class of mods.  Mods should extend this class.
 * Methods have default implementations, but can be overridden.
 * event... methods can be overridden to respond to game events.
 * Event methods that reference Minecraft classes have "No-args" versions that can be used for mods that wish to avoid being specific to particular Minecraft versions.
 * If a normal event method is overridden, it's no-args version will NOT be triggered.
 */
public interface BLMod extends LiteMod {
    /**
     * Returns ID used to identify this mod internally, even among different versions of the same mod.  Mods should override.
     * --This should never be changed after the mod has been released!--
     *
     * @return Returns the id of the mod.
     */
    public String getModId();

    @Override
    /**
     * Returns the user-friendly name of the mod.  Mods should override.
     * --Can be changed among versions, so this should not be used to ID mods!--
     *
     * @return Returns user-friendly name of the mod.
     */
    public String getName();

    /**
     * Compares two mods of the same type to determine which is newer.  Mods should override this and implement their own comparison logic.
     *
     * @param otherMod The mod to compare
     * @return Return the newer mod
     */
    public BLMod getNewerVersion(BLMod otherMod);

    @Override
    /**
     * Gets the version of the mod as a String for display.
     *
     * @return Returns the version of the mod as an integer.
     */
    public String getVersion();

    /**
     * Returns true if this mod is compatible with the installed version of BlazeLoader.  This should be checked using Version.class.
     * -Called before mod is loaded!  Do not depend on Mod.load()!-
     *
     * @return Returns true if the mod is compatible with the installed version of BlazeLoader.
     */
    public boolean isCompatibleWithEnvironment();

    /**
     * Gets a user-friendly description of the mod.
     *
     * @return Return a String representing a user-friendly description of the mod.
     */
    public String getModDescription();

}
