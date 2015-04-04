package com.blazeloader.api.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;

/**
 * Interface for mod tileentities that provide a container and gui.
 * <p>Intended for use with APIGui.openContainer
 *
 */
public interface IModInventory extends IInventory, IInteractionObject {

}
