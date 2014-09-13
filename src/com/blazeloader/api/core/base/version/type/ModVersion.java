package com.blazeloader.api.core.base.version.type;

import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.core.base.version.BuildType;

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
