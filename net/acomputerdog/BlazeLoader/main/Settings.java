package net.acomputerdog.BlazeLoader.main;

/**
 * Global settings.  Saved to .minecraft/BL/BLConfig.json.  BlazeLoader.class uses Gson library to handle serialization.
 */
@SuppressWarnings("CanBeFinal")
public class Settings {

    /**
     * The folder where mods are loaded from.  Relative to minecraft's working directory.
     */
    public static String modDir = "/BL/mods/";

    /**
     * Option to disable loading mods.  Set to false and modloading will be skipped.
     */
    public static boolean enableMods = true;

    /**
     * The folder where config files are stored.  Mods do not have to obey this, but should if possible.
     */
    public static String configDir = "/BL/mods/";
}
