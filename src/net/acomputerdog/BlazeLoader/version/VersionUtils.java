package net.acomputerdog.BlazeLoader.version;

/**
 * Utilities for the Version manager
 */

//TODO: Use simple enums to avoid early class loading
public class VersionUtils {
    public static boolean isGameOBF() {
        try {
            Class.forName("net.minecraft.crash.CrashReport"); //do not obfuscate this name!
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean hasForge() {
        try {
            Class.forName("net.minecraftforge.oredict.ShapedOreRecipe");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
