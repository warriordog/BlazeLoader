package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.entity.Entity;

/**
 * 
 * Event for doing something when an entity instance is created.
 *
 */
public interface EntityConstructingListener extends BLMod {
	
	/**
	 * A wild Entity has appeared. Do something!
	 */
	public void eventEntityConstructed(Entity entity);
}
