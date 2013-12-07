package net.acomputerdog.BlazeLoader.api.gui;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.minecraft.client.gui.GuiScreen;

/**
 * API functions for GUI-related tasks.
 */
public class ApiGui {

    /**
     * Opens a GUI, triggering an eventDisplayGui in the process.
     * @param gui The GUI to display.
     */
    public static void openGUI(GuiScreen gui){
        ApiBase.theMinecraft.displayGuiScreen(gui);
    }

    /**
     * Closes the currently open GUI, returning to the bottom layer.  (Usually either the main menu or the main game interface.)
     */
    public static void closeCurrentGUI(){
        ApiBase.theMinecraft.displayGuiScreen(null);
    }

    /**
     * Returns the currently open GUI.  May be null.
     * @return Returns the currently open GUI.
     */
    public static GuiScreen getOpenGUI(){
        return ApiBase.theMinecraft.currentScreen;
    }
}
