package com.blazeloader.api.api.entity;

import java.util.HashMap;

import com.mumfrey.liteloader.core.event.HandlerList;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This can probably be replaced with storing the properties in the entity class directly.
 * @author Chris Albers
 *
 */
public class EntityPropertyManager {
	
	private static final HashMap<Entity, Properties> mapping = new HashMap<Entity, Properties>();
	
	public static void registerEntityProperties(Entity e, IEntityProperties p) {
		if (!mapping.containsKey(e)) {
			mapping.put(e, new Properties(e));
		}
		mapping.get(e).registerHandler(p);
	}
	
	public static void unRegisterEntityProperties(Entity e, IEntityProperties p) {
		if (mapping.containsKey(e)) {
			mapping.get(e).unRegisterHandler(p);
		}
	}
	
	public static void entityinit(Entity e) {
		if (mapping.containsKey(e)) {
			mapping.get(e).entityInit();
		}
	}
	
	public static void readFromNBT(Entity e, NBTTagCompound t) {
		if (mapping.containsKey(e)) {
			mapping.get(e).readFromNBT(t);
		}
	}
	
	public static void writeToNBT(Entity e, NBTTagCompound t) {
		if (mapping.containsKey(e)) {
			mapping.get(e).writeToNBT(t);
		}
	}
	
	private static class Properties {
		private final Entity entity;
		
		private final HandlerList<IEntityProperties> handlers = new HandlerList<IEntityProperties>(IEntityProperties.class);
		
		public Properties(Entity e) {
			entity = e;
		}
		
		public void registerHandler(IEntityProperties handler) {
			handlers.add(handler);
		}
		
		public void unRegisterHandler(IEntityProperties handler) {
			if (handlers.contains(handler)) handlers.remove(handler);
		}
		
		public void entityInit() {
			handlers.all().entityInit(entity);
		}
		
		public void readFromNBT(NBTTagCompound t) {
			handlers.all().readFromNBT(t);
		}
		
		public void writeToNBT(NBTTagCompound t) {
			handlers.all().writeToNBT(t);
		}
	}
}
