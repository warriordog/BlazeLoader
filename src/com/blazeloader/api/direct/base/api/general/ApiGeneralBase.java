package com.blazeloader.api.direct.base.api.general;

import com.blazeloader.api.core.base.main.BLMain;

import java.io.File;

/**
 * General side-independent API functions
 */
public class ApiGeneralBase {
    /**
     * Location of Minecraft's working directory (.minecraft).
     */
    public static File mainDir = new File("./");

    /**
     * Location of the working directory for mods.  Mods should load and save configurations, resources, etc. here.
     */
    public static File modDir = new File("./BL/mods/");

    /**
     * Location of the storage directory for mod Configs.  Mods do not have to obey this, but should if possible.
     */
    public static File configDir = new File("./BL/config/");

    /**
     * Shuts down the game with a specified error code.  Use 0 for normal shutdown.
     *
     * @param code The error code to return to the system after shutdown.
     */
    public static void shutdown(String message, int code) {
        BLMain.instance().shutdown(message, code);
    }
}
