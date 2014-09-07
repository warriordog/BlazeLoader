package com.blazeloader.api.api.general;

import com.blazeloader.api.core.base.main.BLMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.profiler.Profiler;

import java.io.File;

/**
 * Basic API.  Contains simple functions and references to commonly used single-instance classes.
 */
public class ApiGeneral {

    /**
     * The instance of Minecraft
     */
    public static Minecraft theMinecraft = null;

    /**
     * Local player controller
     */
    public static EntityClientPlayerMP localPlayer = null;

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
     * The game profiler.  Mods that wish to include profiler support can use this.
     */
    public static Profiler theProfiler = null;

    /**
     * Shuts down the game with a specified error code.  Use 0 for normal shutdown.
     *
     * @param code The error code to return to the system after shutdown.
     */
    public static void shutdown(String message, int code) {
        BLMain.instance().shutdown(message, code);
    }

}
