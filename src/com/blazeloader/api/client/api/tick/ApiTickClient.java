package com.blazeloader.api.client.api.tick;

import com.blazeloader.api.client.api.general.ApiGeneralClient;

/**
 * Client side tick functions
 */
public class ApiTickClient {
    /**
     * Gets the game's tick rate.  Uses reflection only on first run.
     *
     * @return Returns the game's current tick rate.
     */
    public static float getTPS() {
        return ApiGeneralClient.theMinecraft.timer.ticksPerSecond;
    }

    /**
     * Sets the game tick rate.
     *
     * @param tps The new tick rate.
     */
    public static void setTPS(float tps) {
        ApiGeneralClient.theMinecraft.timer.ticksPerSecond = tps;
    }
}
