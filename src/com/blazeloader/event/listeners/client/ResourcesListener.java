package com.blazeloader.event.listeners.client;

import net.minecraft.client.resources.IResourceManager;

import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that want to load custom resources.
 */
public interface ResourcesListener extends BLMod {
	
	/**
	 * Triggered when the game's resources get reloaded.
	 * 
	 * @param resourceManager	The game's resource manager doing the reload
	 */
	public void onResourcesReloaded(IResourceManager resourceManager);
}
