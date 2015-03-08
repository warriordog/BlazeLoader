package com.blazeloader.api;

import com.blazeloader.bl.main.BLMain;

import java.io.File;

/**
 * General API functions
 */
public class ApiGeneral {
    /**
     * Location of Minecraft's working directory (.minecraft).
     */
    public static File mainDir = new File("./");

    /**
     * Shuts down the game with a specified error code.  Use 0 for normal shutdown.
     *
     * @param code The error code to return to the system after shutdown.
     */
    public static void shutdown(String message, int code) {
        BLMain.instance().shutdown(message, code);
    }
}
