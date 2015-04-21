package com.blazeloader.api.world;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.blazeloader.util.version.Versions;
import com.google.common.collect.ImmutableSetMultimap;








import net.minecraft.entity.EnumCreatureType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IWorldAccess;

/**
 * Reflection based access to world methods added by forge.
 */
public final class ForgeWorld {
	
	private static Function _isSideSolid;
    public static final <ForgeDirection extends Enum> boolean isSideSolid(World w, BlockPos pos, ForgeDirection side, boolean def) {
    	if (Versions.isForgeInstalled()) {
	    	if (_isSideSolid == null) {
	    		_isSideSolid = new Function("isSideSolid", BlockPos.class, Object.class, boolean.class);
	    	}
	    	return _isSideSolid.call(w, false, pos, side, def);
    	}
    	return def;
    }
    
    private static Function _getPersistentChunks;
    public static final <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks(World w) {
    	if (Versions.isForgeInstalled()) {
	    	if (_getPersistentChunks == null) {
	    		_getPersistentChunks = new Function("getPersistentChunks");
	    	}
	    	ImmutableSetMultimap<ChunkCoordIntPair, Ticket> result = (ImmutableSetMultimap<ChunkCoordIntPair, Ticket>)_getPersistentChunks.call(w, null);
	    	return result == null ? ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of() : result;
    	}
    	return ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of();
    }
    
    private static Function _countEntities;
    public static final int countEntities(World w, EnumCreatureType type, boolean forSpawnCount) {
    	if (Versions.isForgeInstalled()) {
	    	if (_countEntities == null) {
	    		_countEntities = new Function("countEntities", EnumCreatureType.class, boolean.class);
	    	}
	    	return _countEntities.call(w, 0, type, forSpawnCount);
    	}
    	return w.countEntities(type.getCreatureClass());
    }
    
    public static final Variable<Double> MAX_ENTITY_RADIUS = new Variable(double.class, "MAX_ENTITY_RADIUS");
    
    public static class Function {
    	private Method method;
    	
    	public Function(String name, Class... pars) {
    		try {
				method = World.class.getDeclaredMethod(name, pars);
			} catch (Throwable e) {
				e.printStackTrace();
			}
    	}
    	
    	public <T> T call(World instance, T def, Object... pars) {
    		if (method != null) {
				try {
					return (T)method.invoke(instance, pars);
				} catch (Throwable e) {
					method = null;
				}
    		}
    		return def;
    	}
    }
    
    public static class Variable<T> {
    	private Field field;
    	
    	public Variable(Class<T> type, String name) {
    		if (Versions.isForgeInstalled()) {
	    		try {
					field = World.class.getDeclaredField(name);
				} catch (Throwable e) {
					field = null;
				}
    		}
    	}
    	
    	public T get(World instance, T def) {
    		if (field != null) {
				try {
					return (T)field.get(instance);
				} catch (Throwable e) {
					field = null;
				}
    		}
    		return def;
    	}
    	
    	public void set(World instance, T val) {
    		if (field != null) {
				try {
					field.set(instance, val);
				} catch (Throwable e) {
					field = null;
				}
    		}
    	}
    }
}
