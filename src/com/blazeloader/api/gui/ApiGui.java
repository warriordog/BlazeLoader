package com.blazeloader.api.gui;

import com.blazeloader.api.client.gui.IModInventory;
import com.blazeloader.api.client.gui.IModLockableInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.ChatComponentTranslation;

/**
 * GUI functions
 */
public class ApiGui {
	/**
	 * Opens a mod added container.
	 * 
	 * @param player      The player to open the container for
	 * @param inventory	  The tile entity providing the inventory
	 */
    public static void openContainer(EntityPlayerMP player, IModInventory inventory) {
        if (player.openContainer != player.inventoryContainer) player.closeScreen();

        if (inventory instanceof IModLockableInventory) {
            IModLockableInventory lockable = (IModLockableInventory)inventory;
            if (lockable.isLocked() && !player.canOpen(lockable.getLockCode()) && !player.isSpectator()) {
                player.playerNetServerHandler.sendPacket(new S02PacketChat(new ChatComponentTranslation(lockable.getLockMessageString(), new Object[] {inventory.getDisplayName()}), (byte)2));
                player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(lockable.getLockSoundString(), player.posX, player.posY, player.posZ, 1.0F, 1.0F));
                return;
            }
        }
        
        player.getNextWindowId();
        Container container = inventory.createContainer(player.inventory, player);
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, inventory.getGuiID(), inventory.getDisplayName(), inventory.getSizeInventory()));
        player.openContainer = container;
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.onCraftGuiOpened(player);
    }
}
