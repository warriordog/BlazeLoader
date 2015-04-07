package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that handle tick events
 */
public interface TickListener extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

}
