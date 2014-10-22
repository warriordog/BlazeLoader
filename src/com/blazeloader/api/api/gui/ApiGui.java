package com.blazeloader.api.api.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.ChatComponentText;

/**
 * GUI functions
 */
public class ApiGui {
    /**
     * MOST LIKELY BROKEN, USE AT YOUR OWN RISK!!!!!
     *
     * Opens a mod added container
     *
     * @param player      the player to open the container for
     * @param c           The container to open
     * @param guiLabel The title of the GUI
     * @param addCrafters If true, add crafting support to the container
     */
    public static void openClientContainer(EntityPlayerMP player, Container c, String guiLabel, boolean addCrafters) {
        if (!player.worldObj.isRemote) {

            if (player.openContainer != player.inventoryContainer) {
                player.closeScreen();
            }

            player.currentWindowId = player.currentWindowId % 100 + 1;

            //TODO:  No idea what the arguments are, so this probably does not work.
            player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, c.getClass().getName() + ":?:" + guiLabel, new ChatComponentText("INVENTORY"), c.getInventory().size()));
            player.openContainer = c;
            player.openContainer.windowId = player.currentWindowId;
            if (addCrafters) {
                player.openContainer.addCraftingToCrafters(player);
            }
        }
    }
}
