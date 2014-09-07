package com.blazeloader.api.core.base.mod;

import java.io.File;

/**
 * Class that stores information about a loaded mod.
 */
public class ModData {
    private final Class<? extends BLMod> modClass;
    private final BLMod modInstance;
    private final String modId;
    private final File modSource;

    public ModData(BLMod mod, Class<? extends BLMod> cls, File source, String id) {
        if (mod == null || cls == null || source == null || id == null) {
            throw new IllegalArgumentException("ModData cannot have null fields!");
        }
        this.modInstance = mod;
        this.modClass = cls;
        this.modId = id;
        this.modSource = source;
    }

    public Class<? extends BLMod> getModClass() {
        return modClass;
    }

    public BLMod getModInstance() {
        return modInstance;
    }

    public String getModId() {
        return modId;
    }

    public File getModSource() {
        return modSource;
    }
}
