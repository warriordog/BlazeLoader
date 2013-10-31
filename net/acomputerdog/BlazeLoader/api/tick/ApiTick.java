package net.acomputerdog.BlazeLoader.api.tick;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Timer;

/**
 * Api for function related to the world tick.
 */
public class ApiTick {

    /**
     * The game's tick timer.
     */
    public static Timer gameTimer;

    /**
     * Gets the game's tick rate.  Uses reflection only on first run.
     * @return Returns the game's current tick rate.
     */
    public static float getTPS(){
        return ApiBase.theMinecraft.getTPS();
    }

    /**
     * Sets the game tick rate.
     * @param tps The new tick rate.
     */
    public static void setTPS(float tps){
        ApiBase.theMinecraft.setTPS(tps);
    }

    /**
     * Returns true if the game is currently in the middle of a tick.
     * @return Returns true if the game is in a tick, false if not.
     */
    public static boolean isGameInTick(){
        return BlazeLoader.isInTick;
    }

    public static long getTotalTicksInGame(){
        return BlazeLoader.ticks;
    }
}
