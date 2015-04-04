package com.blazeloader.api.client.tick;

import net.minecraft.client.Minecraft;

/**
 * Client side tick functions
 *
 * WARNING:  These functions may not actually do what they seem
 *
 */
public class ApiTickClient {
    /**
     * Gets the game's tick rate.  Uses reflection only on first run.
     *
     * @return Returns the game's current tick rate.
     */
    public static float getTPS() {
        return Minecraft.getMinecraft().timer.ticksPerSecond;
    }

    /**
     * Sets the game tick rate.
     *
     * @param tps The new tick rate.
     */
    public static void setTPS(float tps) {
        Minecraft.getMinecraft().timer.ticksPerSecond = tps;
    }
}
