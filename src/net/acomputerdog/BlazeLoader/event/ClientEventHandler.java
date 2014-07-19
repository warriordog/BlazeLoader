package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

/**
 * Interface for mods that handle client-specific events
 */
public interface ClientEventHandler extends BLMod {

    /**
     * Called when a GUI is about to be displayed.
     * @param minecraft The Minecraft object creating the event.
     * @param oldGui  The current GUI.
     * @param newGui  The GUI being displayed.
     */
    public void eventDisplayGui(Minecraft minecraft, GuiScreen oldGui, GuiScreen newGui);

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
