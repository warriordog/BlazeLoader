package com.blazeloader.api.direct.server.api.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

/**
 * Server-side GUI functions
 */
public class ApiGuiServer {
    /**
     * Untested
     * Opens a mod added container
     *
     * @param player      the player to open the container for
     * @param c           The container to open
     * @param guiLabel
     * @param addCrafters
     */
    public static void openClientContainer(EntityPlayerMP player, Container c, String guiLabel, boolean addCrafters) {
        if (!player.worldObj.isRemote) {

            if (player.openContainer != player.inventoryContainer) {
                player.closeScreen();
            }

            player.currentWindowId = player.currentWindowId % 100 + 1;

            player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, -1, c.getClass().getName() + ":?:" + guiLabel, c.getInventory().size(), true));
            player.openContainer = c;
            player.openContainer.windowId = player.currentWindowId;
            if (addCrafters) {
                player.openContainer.addCraftingToCrafters(player);
            }
        }
    }
}
