package com.blazeloader.api.client.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * API functions for GUI-related tasks.
 */
public class ApiGuiClient {

    /**
     * Opens a GUI, triggering an eventDisplayGui in the process.
     *
     * @param gui The GUI to display.
     */
    public static void openGUI(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    /**
     * Closes the currently open GUI, returning to the bottom layer.  (Usually either the main menu or the main game interface.)
     */
    public static void closeCurrentGUI() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    /**
     * Returns the currently open GUI.  May be null.
     *
     * @return Returns the currently open GUI.
     */
    public static GuiScreen getOpenGUI() {
        return Minecraft.getMinecraft().currentScreen;
    }
}
