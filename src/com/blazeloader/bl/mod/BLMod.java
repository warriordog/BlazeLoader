package com.blazeloader.bl.mod;

import com.blazeloader.util.version.type.ModVersion;
import com.mumfrey.liteloader.LiteMod;

/**
 * The base interface for a Blazeloader mods. All mods should implement this.
 */
public interface BLMod extends LiteMod {
    /**
     * Returns ID used to identify this mod internally, even among different versions of the same mod.  Mods should override.
     * <p><b>--This should never be changed after the mod has been released!--</b>
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
