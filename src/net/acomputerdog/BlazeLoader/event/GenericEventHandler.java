package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.mod.BLMod;

/**
 * Interface for mods that handle generic events
 */
public interface GenericEventHandler extends BLMod {
    /**
     * Called when mod is started.  Game is fully loaded and can be interacted with.
     */
    public void start();

    /**
     * Called when mod is stopped.  Game is about to begin shutting down, so mod should release system resources, close streams, etc.
     */
    public void stop();
}
