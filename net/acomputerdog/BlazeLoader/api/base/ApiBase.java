package net.acomputerdog.BlazeLoader.api.base;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.ILogAgent;
import net.minecraft.src.Minecraft;

import java.io.File;

/**
 * Basic API.  Contains simple functions and references to commonly used single-instance classes.
 */
public class ApiBase {

    /**
     * The instance of Minecraft
     */
    public static Minecraft theMinecraft = null;

    /**
     * The game logger
     */
    public static ILogAgent globalLogger = null;

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
    public static File modDir = new File("./mods/");
}
