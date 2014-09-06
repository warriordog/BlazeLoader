package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;

/**
 * Interface for mods that handle tick events
 */
public interface TickEventHandler extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

}
