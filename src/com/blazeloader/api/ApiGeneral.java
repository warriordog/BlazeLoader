package com.blazeloader.api;

import com.blazeloader.bl.main.BLMain;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.launch.LoaderEnvironment;

import java.io.File;

/**
 * General API functions
 */
public class ApiGeneral {

    private static final boolean isClient = LiteLoader.getEnvironmentType() == LoaderEnvironment.EnvironmentType.CLIENT;

    /**
     * Location of Minecraft's working directory.
     * <br><br>%APPDATA%/.minecraft/ for windows.
     */
    public static final File mainDir = new File("./");

    /**
     * Shuts down the game with a specified error code.
     * <br><br>Use <b>0</b> for normal shutdown.
     *
     * @param code The error code to return to the system after shutdown.
     */
    public static void shutdown(String message, int code) {
        BLMain.instance().shutdown(message, code);
    }

    /**
     * Checks if the game is a client instance
     *
     * @return return true if the game is a client
     */
    public static boolean isClient() {
        return isClient;
    }

    /**
     * Checks if the game is a dedicated server instance (an actualy server with no client, not an integrated server).  Will return false on client.
     *
     * @return return true if the game is a dedicated server
     */
    public static boolean isServer() {
        return !isClient;
    }
}
