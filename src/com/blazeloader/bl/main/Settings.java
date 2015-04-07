package com.blazeloader.bl.main;

import com.blazeloader.api.ApiGeneral;

import java.io.File;

/**
 * Global settings.
 */
public class Settings {

    /**
     * The folder where config files are stored.  Mods do not have to obey this, but should if possible.
     */
    public static String configDir = new File(ApiGeneral.mainDir.getPath(), "/BL/config/").getPath();

    /**
     * The minimum log level required for a message to be displayed.
     */
    public static String minimumLogLevel = "DEBUG";

    /**
     * If BLogger-supporting mods should log information to a file.
     */
    public static boolean logToFile = true;
}
