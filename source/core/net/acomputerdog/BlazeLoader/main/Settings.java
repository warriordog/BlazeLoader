package net.acomputerdog.BlazeLoader.main;

import net.acomputerdog.core.logger.ELogLevel;

/**
 * Global settings.  Saved to .minecraft/BL/BLConfig.json.  BlazeLoader.class uses Gson library to handle serialization.
 */
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
    public static String configDir = "/BL/config/";

    /**
     * Option to use the version folder for mods instead of the base BL folder.
     */
    public static boolean useVersionMods = true;

    /**
     * The minimum log level required for a message to be displayed.
     */
    public static transient ELogLevel minimumLogLevel = ELogLevel.DEBUG;
    protected static String minimumLogLevelName = "debug";

    /**
     * If disabled, BlazeLoader will not load mods from the classpath.
     */
    public static boolean loadModsFromClasspath = true;
}
