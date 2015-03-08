package com.blazeloader.util.version.type;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.util.version.BuildType;

/**
 * Describes the version of a BLMod.  ID and Name are obtained from the mod.
 */
public class ModVersion extends BasicVersion {
    private final BLMod mod;

    public ModVersion(BuildType buildType, BLMod mod, int... versionParts) {
        super(mod.getModId(), mod.getName(), buildType, versionParts);
        this.mod = mod;
    }

    public BLMod getMod() {
        return mod;
    }
}
