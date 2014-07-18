package net.acomputerdog.BlazeLoader.version;

/**
 * Utilities for the Version manager
 */
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
