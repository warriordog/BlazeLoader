package com.blazeloader.api.world;

import java.util.Arrays;

import net.minecraft.world.WorldType;

/**
 * Inbetween class for mod added World Types.
 */
public class ModWorldType extends WorldType {
	private final String plainName;
	private final String modName;
	
	private boolean createable = false;
	private boolean versioned = false;
	private boolean hasNotificationData = false;
	
	protected ModWorldType(String mod, String name, int version) {
		super(getNextWorldTypeId(), mod + "." + name, version);
		modName = mod;
		plainName = name;
	}
	
	private static int getNextWorldTypeId() {
		for (int i = 0; i < WorldType.worldTypes.length; i++) {
			if (WorldType.worldTypes[i] == null) return i;
		}
		int nextWorldTypeId = WorldType.worldTypes.length;
		WorldType.worldTypes = Arrays.copyOf(WorldType.worldTypes, WorldType.worldTypes.length + 16);
		return nextWorldTypeId;
	}
	
    public String getTranslateName() {
        return modName + ".generator." + plainName;
    }
    
    /**
     * Gets the modname this world type was registered with.
     */
    public String getModName() {
    	return modName;
    }
    
    /**
     * Gets the name of this world type without the name of the mod.
     */
    public String getPlainName() {
    	return plainName;
    }
    
    /**
     * getTranslateInfoName()
     * Gets the translation key for the information section of this world type.
     */
    public String func_151359_c() {
        return super.func_151359_c();
    }

    /**
     * Sets canBeCreated to the provided value, and returns this.
     */
    public WorldType setCanBeCreated(boolean val) {
    	createable = val;
        return this;
    }
    
    public boolean getCanBeCreated() {
        return createable;
    }

    /**
     * Flags this world type as having an associated version.
     */
    public WorldType setVersioned() {
    	versioned = true;
        return this;
    }
    
    public boolean isVersioned() {
        return versioned;
    }
    
    public boolean showWorldInfoNotice() {
        return hasNotificationData;
    }

    /**
     * enables the display of generator.[worldtype].info message on the customize world menu
     */
    public WorldType setNotificationData() {
    	hasNotificationData = true;
        return this;
    }
}
