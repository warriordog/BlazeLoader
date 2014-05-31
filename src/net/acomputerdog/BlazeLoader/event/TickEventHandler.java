package net.acomputerdog.BlazeLoader.event;

import com.mumfrey.liteloader.LiteMod;
import net.acomputerdog.BlazeLoader.mod.BLMod;

/**
 * Interface for mods that handle tick events
 */
public interface TickEventHandler extends BLMod {
    /**
     * Called when the game is ticked.
     */
    public void eventTick();

}
