package com.blazeloader.api.client.api.gui;

import net.minecraft.world.ILockableContainer;

public interface IModLockableInventory extends ILockableContainer {
	
	/**
	 * Used to get the localization string for the message to be printed when a player fails to open this gui.
	 * 
	 * The default string used by chests is "container.isLocked"
	 * 
	 * @return String of unlocalized message
	 */
	public String getLockMessageString();
	
	/**
	 * Used to get the sound played when a player fails to open this gui.
	 * 
	 * The default sound used by chests is "random.door_close"
	 * 
	 * @return String id of the sound
	 */
	public String getLockSoundString();
}