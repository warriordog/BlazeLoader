package com.blazeloader.api.main;

import com.blazeloader.api.api.general.ApiGeneralBase;

import java.io.File;

/**
 * Global settings.
 */
public class Settings {

    /**
     * The folder where config files are stored.  Mods do not have to obey this, but should if possible.
     */
    public static String configDir = new File(ApiGeneralBase.mainDir.getPath(), "/BL/config/").getPath();

    /**
     * The minimum log level required for a message to be displayed.
     */
    public static String minimumLogLevel = "DEBUG";
}
