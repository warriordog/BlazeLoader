package net.acomputerdog.BlazeLoader.event;

import net.minecraft.world.WorldServer;

/**
 * Interface for mods that handle tick events
 */
public interface TickEventHandler {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

    /**
     * Called when a server-side world is ticked.
     *
     * @param world The world being ticked.
     */
    public void eventTickServerWorld(WorldServer world);

}
