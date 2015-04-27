package com.blazeloader.util.version;

/**
 * Utilities for the Version manager
 */

public class VersionUtils {
    public static boolean isGameOBF() {
        try {
        	//don't obfuscate this name, and don't use other classes!  Enum is used because static initialization is safe!
            Class.forName("net.minecraft.world.EnumSkyBlock");
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean hasForge() {
        try {
        	//don't use other classes, enum is used because static initialization is safe!
            Class.forName("net.minecraftforge.common.EnumPlantType");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
