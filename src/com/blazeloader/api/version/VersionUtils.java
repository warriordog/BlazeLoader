package com.blazeloader.api.version;

/**
 * Utilities for the Version manager
 */

public class VersionUtils {
    public static boolean isGameOBF() {
        try {
            Class.forName("net.minecraft.world.EnumSkyBlock"); //do not obfuscate this name!
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean hasForge() {
        try {
            Class.forName("net.minecraftforge.common.EnumPlantType");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
