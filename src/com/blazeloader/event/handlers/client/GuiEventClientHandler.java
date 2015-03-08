package com.blazeloader.event.handlers.client;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * Interface for mods that handle client-specific events
 */
public interface GuiEventClientHandler extends BLMod {

    /**
     * Called when a GUI is about to be displayed.
     *
     * @param minecraft The Minecraft object creating the event.
     * @param oldGui    The current GUI.
     * @param newGui    The GUI being displayed.
     */
    public void eventDisplayGui(Minecraft minecraft, GuiScreen oldGui, GuiScreen newGui);

}
