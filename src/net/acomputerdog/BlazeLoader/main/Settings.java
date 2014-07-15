package net.acomputerdog.BlazeLoader.main;

/**
 * Global settings.  Saved to .minecraft/BL/BLConfig.json.  BlazeLoader.class uses Gson library to handle serialization.
 */
public class Settings {

    /**
     * The folder where config files are stored.  Mods do not have to obey this, but should if possible.
     */
    public static String configDir = "/BL/config/";

    /**
     * The minimum log level required for a message to be displayed.
     */
    public static String minimumLogLevel = "DEBUG";
}
