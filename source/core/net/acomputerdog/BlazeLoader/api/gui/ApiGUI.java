package net.acomputerdog.BlazeLoader.api.gui;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

/**
 * API functions for GUI-related tasks.
 */
public class ApiGUI {

    /**
     * Opens a GUI, triggering an eventDisplayGui in the process.
     *
     * @param gui The GUI to display.
     */
    public static void openGUI(GuiScreen gui) {
        ApiGeneral.theMinecraft.displayGuiScreen(gui);
    }

    /**
     * Closes the currently open GUI, returning to the bottom layer.  (Usually either the main menu or the main game interface.)
     */
    public static void closeCurrentGUI() {
        ApiGeneral.theMinecraft.displayGuiScreen(null);
    }

    /**
     * Returns the currently open GUI.  May be null.
     *
     * @return Returns the currently open GUI.
     */
    public static GuiScreen getOpenGUI() {
        return ApiGeneral.theMinecraft.currentScreen;
    }

    /**
     * Untested
     * Opens a mod added container
     *
     * @param player the player to open the container for
     * @param c      The container to open
     * @param guiLabel
     * @param addCrafters
     */
    public static void accessContainer(EntityPlayerMP player, Container c, String guiLabel, boolean addCrafters) {
        if (!player.worldObj.isClient) {
            player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(0, -1, c.getClass().getName() + ":?:" + guiLabel , 9, true));
            player.openContainer = c;
            player.openContainer.windowId = 1;
            if (addCrafters) {
            	player.openContainer.addCraftingToCrafters(player);
            }
        }
    }
}
