package net.acomputerdog.BlazeLoader.main;

/**
 * Contains methods for obtaining information about the installed version of BlazeLoader.
 * -MUST remain backward compatible!-
 */
public class Version {

    /**
     * Gets the global version of BlazeLoader.  A change here will RESET the status of the other two update counters.
     * Mods will be unable to check the status of the other two counters so changes here must be taken into account.
     * Generally a change here is either a Minecraft update or a large restructuring.
     * @return Return the global version of BlazeLoader.
     */
    public static int getGlobalVersion(){
        return 0;
    }

    /**
     * Gets the version of the API features of BlazeLoader.  Incremented with changes to package "api", package "util", and Class Mod.
     * Changes here may affect mods.
     * @return Get the version of the API features of BlazeLoader
     */
    public static int getApiVersion(){
        return 1;
    }

    /**
     * Gets the version of the internal features of BlazeLoader.
     * Mods that only use package "api", package "util", and Class Mod should be unaffected by version changes here.
     * @return Return an int representing the version of BL's internal components.
     */
    public static int getInternalVersion(){
        return 1;
    }

    /**
     * Gets the version of BlazeLoader as an integer.  Incremented by one with every commit.
     * Should to be used to compare versions.
     * -Will be removed soon!-
     * @return Returns the version of BlazeLoader as an integer.
     */
    public static int getIntVersion(){
        return 9;
    }

    /**
     * Gets the version of BlazeLoader as a string formatted for display.  Example return: "0.1.234"
     * @return Returns the version of BlazeLoader as a String formatted for display.
     */
    public static String getStringVersion(){
        return getGlobalVersion() + "." + getApiVersion() + "." + getInternalVersion();
    }

    /**
     * Gets the version of Minecraft that this version of BlazeLoader is intended for.
     * @return Returns a String representing the version of Minecraft, ex. "1.6.4".
     */
    public static String getMinecraftVersion(){
        return "1.6.4";
    }
}
