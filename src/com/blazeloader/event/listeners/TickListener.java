package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that want to perform basic actions on each game tick.
 * 
 * If you need more information per tick then I would recommend using {@code Tickable} or {@code ServerTickable} instead.
 */
public interface TickListener extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void onTick();

}
