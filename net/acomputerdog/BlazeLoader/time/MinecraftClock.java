package net.acomputerdog.BlazeLoader.time;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;

/**
 * A clock based on Minecraft's units of time.  It is divided up into "days" and "ticks".  1 tick is 1/20 of a second and 1 day is 24000 ticks, or 20 minutes.
 */
public class MinecraftClock {

    /**
     * Gets the number of ticks of this day.
     * @return Return the remainder of the total number of ticks divided by 24000.
     */
    public int getTicks(){
        return (int)(BlazeLoader.ticks % 24000);
    }

    /**
     * Gets the number of days since the game started.
     * @return Return the total number of ticks divided by 24000.
     */
    public int getDays(){
        return (int)(BlazeLoader.ticks / 24000);
    }

}
