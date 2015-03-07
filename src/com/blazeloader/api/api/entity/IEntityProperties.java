package com.blazeloader.api.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for entity extension classes (NYI)
 */
public interface IEntityProperties {
	
	/**
	 * Occurs whilst the entity is being constructed.
	 */
	public void entityInit(Entity e);
	
	/**
	 * @param tagCompound
	 */
	public void writeToNBT(NBTTagCompound tagCompound);
	
	/**
	 * 
	 * @param tagCompund
	 */
	public void readFromNBT(NBTTagCompound tagCompund);
}
