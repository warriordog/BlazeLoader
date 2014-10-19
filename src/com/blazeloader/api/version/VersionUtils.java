package com.blazeloader.api.version;

/**
 * Utilities for the Version manager
 */

public class VersionUtils {
    public static boolean isGameOBF() {
        try {
            Class.forName("net.minecraft.world.EnumSkyBlock"); //don't obfuscate this name, and don't use other classes!  Enum is used because static initialization is safe!
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean hasForge() {
        try {
            Class.forName("net.minecraftforge.common.EnumPlantType"); //don't use other classes, enum is used because static initialization is safe!
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
