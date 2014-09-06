package com.blazeloader.api.direct.event;

import com.blazeloader.api.core.mod.BLMod;

/**
 * Interface for mods that handle tick events
 */
public interface TickEventHandler extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

}
