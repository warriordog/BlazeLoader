package com.blazeloader.api.mod;

import com.blazeloader.api.version.type.ModVersion;
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

    @Override
    /**
     * Gets the version of the mod as a String for display.
     *
     * @return Returns the version of the mod as an integer.
     */
    public String getVersion();

    /**
     * Gets a user-friendly description of the mod.
     *
     * @return Return a String representing a user-friendly description of the mod.
     */
    public String getModDescription();

    /**
     * Gets a ModVersion that describes the version of this mod.  For performance it is recommended to create the version once and return the same each time.
     *
     * @return Return a ModVersion that describes the version of this mod.
     */
    public ModVersion getModVersion();

}
