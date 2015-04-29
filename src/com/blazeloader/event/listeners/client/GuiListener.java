package com.blazeloader.event.listeners.client;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * Interface for mods that handle gui events
 */
public interface GuiListener extends BLMod {

    /**
     * Called when a GUI is about to be displayed.
     *
     * @param minecraft The Minecraft object creating the event.
     * @param oldGui    The current GUI.
     * @param newGui    The GUI about to be displayed.
     */
    public void onGuiOpen(Minecraft minecraft, GuiScreen oldGui, GuiScreen newGui);

}
