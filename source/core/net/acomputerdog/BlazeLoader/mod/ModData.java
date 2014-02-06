package net.acomputerdog.BlazeLoader.mod;

import java.io.File;

/**
 * Class that stores information about a loaded mod.
 */
public class ModData {
    private final Class<? extends Mod> modClass;
    private final Mod modInstance;
    private final String modId;
    private final File modSource;

    public ModData(Mod mod, Class<? extends Mod> cls, File source, String id) {
        if (mod == null || cls == null || source == null || id == null) {
            throw new IllegalArgumentException("ModData cannot have null fields!");
        }
        this.modInstance = mod;
        this.modClass = cls;
        this.modId = id;
        this.modSource = source;
    }

    public Class<? extends Mod> getModClass() {
        return modClass;
    }

    public Mod getModInstance() {
        return modInstance;
    }

    public String getModId() {
        return modId;
    }

    public File getModSource() {
        return modSource;
    }
}
