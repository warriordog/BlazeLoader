package com.blazeloader.api.direct.client.api.tick;

import com.blazeloader.api.direct.client.api.general.ApiGeneralClient;

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
