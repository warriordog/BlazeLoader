package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

/**
 * Interface for mods that handle client-specific events
 */
public interface ClientEventHandler extends BLMod {

    /**
     * Called when a GUI is about to be displayed.
     *
     * @param oldGui  The current GUI.
     * @param newGui  The GUI being displayed.
     * @param allowed Set to true if the GUI will be displayed, false if another mod has disabled it.
     * @return Return true to allow the GUI, false to block it.
     */
    public boolean eventDisplayGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed);

    /**
     * Triggered when the user presses a bound key.
     *
     * @param key KeyBinding object for the pressed key
     */
    public void eventKeyDown(KeyBinding key);

    /**
     * Triggered when the user releases a bound key.
     *
     * @param key KeyBinding object for the released key
     */
    public void eventKeyUp(KeyBinding key);

    /**
     * Triggered every tick for the duration that a key is held down
     *
     * @param key KeyBinding object for the pressed key
     */
    public void eventKeyHeld(KeyBinding key);
}
