package com.blazeloader.api.entity;

import java.util.HashMap;

import com.blazeloader.bl.main.BLMain;
import com.mumfrey.liteloader.core.event.HandlerList;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * This can probably be replaced with storing the properties in the entity class directly.
 *
 */
public class EntityPropertyManager {
	
	private static final HashMap<Entity, Properties> mapping = new HashMap<Entity, Properties>();
	
	public static void registerEntityProperties(Entity e, IEntityProperties p) {
		if (!mapping.containsKey(e)) {
			mapping.put(e, new Properties());
		}
		mapping.get(e).registerHandler(p);
	}
	
	public static void unRegisterEntityProperties(Entity e, IEntityProperties p) {
		if (mapping.containsKey(e)) {
			mapping.get(e).unRegisterHandler(p);
		}
	}
	
	public static IEntityProperties getEntityPropertyObject(Entity e, Class<? extends IEntityProperties> c) {
		if (mapping.containsKey(e)) {
			for (IEntityProperties i : mapping.get(e).handlers) {
				if (i.getClass() == c) return i;
			}
		}
		return null;
	}
	
	public static void copyToEntity(Entity source, Entity destination) {
		if (mapping.containsKey(source)) {
			mapping.put(destination, mapping.get(source));
		}
	}
	
	public static void entityDestroyed(Entity e) {
		if (mapping.containsKey(e)) {
			mapping.remove(e);
		}
	}
	
	public static void entityinit(Entity e) {
		if (mapping.containsKey(e)) {
			mapping.get(e).entityInit(e, e.worldObj);
		}
	}
	
	public static void readFromNBT(Entity e, NBTTagCompound t) {
		if (mapping.containsKey(e)) {
			NBTTagCompound modsTag;
			if (t.hasKey("BlazeLoader")) {
				modsTag = t.getCompoundTag("BlazeLoader");
			} else {
				modsTag = new NBTTagCompound();
			}
			Properties p = mapping.get(e);
			try {
				p.readFromNBT(modsTag);
			} catch (Throwable er) {
				BLMain.LOGGER_MAIN.logFatal("Failed in reading entity NBT into (" + p.getClass().getCanonicalName() + ").", er);
			}
		}
	}
	
	public static void writeToNBT(Entity e, NBTTagCompound t) {
		if (mapping.containsKey(e)) {
			NBTTagCompound modsTag = new NBTTagCompound();
			t.setTag("BlazeLoader", modsTag);
			Properties p = mapping.get(e);
			try {
				p.writeToNBT(modsTag);
			} catch (Throwable er) {
				BLMain.LOGGER_MAIN.logFatal("Failed in writing entity NBT from (" + p.getClass().getCanonicalName() + ").", er);
			}
		}
	}
	
	private static class Properties {
		private final HandlerList<IEntityProperties> handlers = new HandlerList<IEntityProperties>(IEntityProperties.class);
		
		public Properties() {}
		
		public void registerHandler(IEntityProperties handler) {
			for (IEntityProperties i : handlers) {
				if (i.getClass().equals(handler.getClass())) {
					BLMain.LOGGER_MAIN.logWarning("Attempted to register duplicate Properties object (" + handler.getClass().getCanonicalName() + "). Only one instance allowed per entity. Handler was not registered.");
					return;
				}
			}
			handlers.add(handler);
		}
		
		public void unRegisterHandler(IEntityProperties handler) {
			if (handlers.contains(handler)) handlers.remove(handler);
		}
		
		public void entityInit(Entity entity, World world) {
			handlers.all().entityInit(entity, world);
		}
		
		public void readFromNBT(NBTTagCompound t) {
			handlers.all().readFromNBT(t);
		}
		
		public void writeToNBT(NBTTagCompound t) {
			handlers.all().writeToNBT(t);
		}
	}
}
