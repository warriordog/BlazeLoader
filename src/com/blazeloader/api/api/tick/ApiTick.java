package com.blazeloader.api.api.tick;

import com.blazeloader.api.main.BLMain;

/**
 * Api for function related to the world tick.
 */
public class ApiTick {

    /**
     * Returns true if the game is currently in the middle of a tick.
     *
     * @return Returns true if the game is in a tick, false if not.
     */
    public static boolean isGameInTick() {
        return BLMain.isInTick;
    }

    /**
     * Gets the total number of ticks that the game has run for.
     *
     * @return Return a long representing the number of ticks that the game has run for.
     */
    public static long getTotalTicksInGame() {
        return BLMain.numTicks;
    }
}
