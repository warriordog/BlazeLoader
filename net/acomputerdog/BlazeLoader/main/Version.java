package net.acomputerdog.BlazeLoader.main;

/**
 * Contains methods for obtaining information about the installed version of BlazeLoader.
 * -MUST remain backward compatible!-
 */
public class Version {

    /**
     * Gets the version of BlazeLoader as an integer.  Incremented by one with every commit.
     * Should to be used to compare versions.
     * @return Returns the version of BlazeLoader as an integer.
     */
    public static int getIntVersion(){
        return 4;
    }

    /**
     * Gets the version of BlazeLoader as a string formatted for display.  Example return: "dev_0.1.234"
     * @return Returns the version of BlazeLoader as a String formatted for display.
     */
    public static String getStringVersion(){
        return "dev_0.0.004";
    }

    /**
     * Gets the version of Minecraft that this version of BlazeLoader is intended for.
     * @return Returns a String representing the version of Minecraft, ex. "1.6.4".
     */
    public static String getMinecraftVersion(){
        return "1.6.4";
    }
}
