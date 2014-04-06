package net.acomputerdog.BlazeLoader.main;

/**
 * Contains methods for obtaining information about the installed version of BlazeLoader.
 * -MUST remain backward compatible!-
 */
public final class Version {
    private static final boolean isOBF = isGameOBF();
    private static final boolean isForgeInstalled = hasForge();

    /**
     * Gets the global version of BlazeLoader.  A change here will RESET the status of the other two update counters.
     * Mods must check this value first before determining if the version of BL is correct.
     * Generally a change here is either a Minecraft update or a large restructuring.
     *
     * @return Return the global version of BlazeLoader.
     */
    public static int getGlobalVersion() {
        return 4;
    }

    /**
     * Gets the version of the API features of BlazeLoader.  Incremented with changes to API features.
     * Changes here may affect mods.
     *
     * @return Get the version of the API features of BlazeLoader
     */
    public static int getApiVersion() {
        return 1;
    }

    /**
     * Gets the version of the internal features of BlazeLoader.
     * Mods that only use API features should be unaffected by changes here.
     *
     * @return Return an int representing the version of BL's internal components.
     */
    public static int getInternalVersion() {
        return 0;
    }

    /**
     * Gets the version of BlazeLoader as a string formatted for display.  Example return: "0.1.234"
     *
     * @return Returns the version of BlazeLoader as a String formatted for display.
     */
    public static String getStringVersion() {
        return getGlobalVersion() + "." + getApiVersion() + "." + getInternalVersion();
    }

    /**
     * Gets the version of Minecraft that is running.
     *
     * @return Returns a String representing the version of Minecraft, ex. "1.6.4".
     */
    public static String getMinecraftVersion() {
        return "1.7.2";
    }

    /**
     * Detects if the game is obfuscated.
     * @return Return true if the game is obfuscated.
     */
    public static boolean isGameObfuscated() {
        return isOBF;
    }

    /**
     * Detects if forge is installed.
     * @return Return true if forge is installed
     */
    public static boolean isForgeInstalled() {
        return isForgeInstalled;
    }

    /**
     * Gets the build environment of this BlazeLoader build.
     * @return Return the build environment of this BlazeLoader build.
     */
    public static EBuildType getBuildType() {
        return EBuildType.DEV;
    }

    private static boolean isGameOBF() {
        try {
            Class.forName("net.minecraft.client.Minecraft");
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    private static boolean hasForge() {
        try {
            Class.forName("net.minecraftforge.oredict.ShapedOreRecipe");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
