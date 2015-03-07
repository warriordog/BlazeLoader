package com.blazeloader.api.client.api.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;

/**
 * Interface for mod tileentities that provide a container and gui.
 * Intended for use with APIGui.openContainer
 *
 */
public interface IModInventory extends IInventory, IInteractionObject {

}
