package com.blazeloader.api.direct.base.event;

import com.blazeloader.api.core.base.mod.BLMod;

/**
 * Interface for mods that handle tick events
 */
public interface TickEventBaseHandler extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

}
