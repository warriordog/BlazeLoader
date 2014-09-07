package com.blazeloader.api.direct.client.api.general;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.profiler.Profiler;

/**
 * Basic API.  Contains simple functions and references to commonly used single-instance classes.
 */
public class ApiGeneralClient {

    /**
     * The instance of Minecraft
     */
    public static Minecraft theMinecraft = null;

    /**
     * Local player controller
     */
    public static EntityClientPlayerMP localPlayer = null;

    /**
     * The game profiler.  Mods that wish to include profiler support can use this.
     */
    public static Profiler theProfiler = null;


}
