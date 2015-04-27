package com.blazeloader.api.world;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import com.blazeloader.util.version.Versions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * Reflection based access to world methods added by forge.
 */
public final class ForgeWorld {
	
    /**
     * Gets an accessor to Forge methods on the given Minecraft world
     */
    public static ForgeWorldAccess getForgeWorld(World w) {
    	return new ForgeWorldObj(w);
    }
    
	private static final WorldVariable<Double> MAX_ENTITY_RADIUS = new WorldVariable(double.class, "MAX_ENTITY_RADIUS");
	
	private static WorldFunction<ForgeWorldAccess> _isSideSolid;
    private static WorldFunction<ForgeWorldAccess> _getPersistentChunks;
    private static WorldFunction<ForgeWorldAccess> _countEntities;
    private static WorldFunction<ForgeWorldAccess> _getPerWorldStorage;
    private static WorldFunction<ForgeWorldAccess> _getBlockLightOpacity;
	
	protected static boolean isSideSolid(World worldObj, BlockPos pos, EnumFacing side, boolean def) {
		if (Versions.isForgeInstalled()) {
	    	if (_isSideSolid == null) {
	    		_isSideSolid = new WorldFunction(ForgeWorldAccess.class, "isSideSolid", BlockPos.class, Enum.class, boolean.class);
	    	}
	    	if (_isSideSolid.valid()) {
	    		try {
	    			return _isSideSolid.getLambda(worldObj).isSideSolid(pos, side, def);
	    		} catch (Throwable e) {
	    			_isSideSolid.invalidate();
	    		}
	    	}
		}
		return false;
	}
	
	protected static <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks(World worldObj) {
		if (Versions.isForgeInstalled()) {
	    	if (_getPersistentChunks == null) {
	    		_getPersistentChunks = new WorldFunction(ForgeWorldAccess.class, "getPersistentChunks");
	    	}
	    	if (_getPersistentChunks.valid()) {
		    	ImmutableSetMultimap<ChunkCoordIntPair, Ticket> result;
		    	try {
		    		result = _getPersistentChunks.getLambda(worldObj).getPersistentChunks();
		    	} catch (Throwable e) {
		    		_getPersistentChunks.invalidate();
		    		result = null;
		    	}
		    	return result == null ? ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of() : result;
	    	}
		}
		return ImmutableSetMultimap.<ChunkCoordIntPair, Ticket>of();
	}
	
	protected static int getBlockLightOpacity(World worldObj, BlockPos pos) {
		if (Versions.isForgeInstalled()) {
			if (_getBlockLightOpacity == null) {
				_getBlockLightOpacity = new WorldFunction(ForgeWorldAccess.class, "getBlockLightOpacity");
			}
			if (_getBlockLightOpacity.valid()) {
				try {
					return _getBlockLightOpacity.getLambda(worldObj).getBlockLightOpacity(pos);
				} catch (Throwable e) {
					_getBlockLightOpacity.invalidate();
		    	}
			}
		}
    	if (!worldObj.isValid(pos)) return 0;
        return worldObj.getChunkFromBlockCoords(pos).getBlockLightOpacity(pos);
	}
	
	protected static int countEntities(World worldObj, EnumCreatureType type, boolean forSpawnCount) {
		if (Versions.isForgeInstalled()) {
	    	if (_countEntities == null) {
	    		_countEntities = new WorldFunction(ForgeWorldAccess.class, "countEntities", EnumCreatureType.class, boolean.class);
	    	}
	    	if (_countEntities.valid()) {
		    	try {
					return _countEntities.getLambda(worldObj).countEntities(type, forSpawnCount);
				} catch (Throwable e) {
					_countEntities.invalidate();
				}
	    	}
		}
		return worldObj.countEntities(type.getCreatureClass());
	}
	
	protected static MapStorage getPerWorldStorage(World worldObj) {
		if (Versions.isForgeInstalled()) {
			if (_getPerWorldStorage == null) {
				_getPerWorldStorage = new WorldFunction(ForgeWorldAccess.class, "getPerWorldStorage");
			}
			if (_getPerWorldStorage.valid()) {
				try {
					return _getPerWorldStorage.getLambda(worldObj).getPerWorldStorage();
				} catch (Throwable e) {
					_getPerWorldStorage.invalidate();
				}
			}
		}
		return worldObj.getMapStorage();
	}
	
	protected static double getMaxEntitySize(World worldObj, double def) {
		return MAX_ENTITY_RADIUS.get(worldObj, def);
	}
	
	protected static void setMaxEntitySize(World worldObj, double size) {
		MAX_ENTITY_RADIUS.set(worldObj, size);
	}
    
    protected static final class ForgeWorldObj implements ForgeWorldAccess {
		private final World worldObj;
		
		private ForgeWorldObj(World w) {
			worldObj = w;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side) {
			return isSideSolid(pos, side, false);
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def) {
			return ForgeWorld.isSideSolid(worldObj, pos, side, def);
		}

		@Override
		public <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
			return ForgeWorld.getPersistentChunks(worldObj);
		}

		@Override
		public int getBlockLightOpacity(BlockPos pos) {
			return ForgeWorld.getBlockLightOpacity(worldObj, pos);
		}

		@Override
		public int countEntities(EnumCreatureType type, boolean forSpawnCount) {
			return ForgeWorld.countEntities(worldObj, type, forSpawnCount);
		}

		@Override
		public MapStorage getPerWorldStorage() {
			return ForgeWorld.getPerWorldStorage(worldObj);
		}

		@Override
		public double getMaxEntitySize(double def) {
			return ForgeWorld.getMaxEntitySize(worldObj, def);
		}

		@Override
		public void setMaxEntitySize(double size) {
			ForgeWorld.setMaxEntitySize(worldObj, size);
		}
    }

    private static class WorldFunction<T> {
    	private Function function;
    	
    	public WorldFunction(Class<T> clazz, String name, Class... pars) {
    		Method method;
    		try {
				method = World.class.getDeclaredMethod(name, pars);
			} catch (Throwable e) {
				method = null;
			}
    		if (method != null) {
    			try {
    				MethodHandles.Lookup lookup = MethodHandles.lookup();
	    			MethodHandle methodhandle = lookup.unreflect(method);
	    			CallSite site = (CallSite)LambdaMetafactory.metafactory(lookup, name, MethodType.methodType(clazz), methodhandle.type(), methodhandle, methodhandle.type()).getTarget().invokeExact();
	    			function = (Function)site.getTarget().invoke();
    			} catch (Throwable e) {
    				function = null;
    			}
    		}
    		
    	}
    	
    	public T getLambda(World instance) throws Throwable {
    		return (T)function.apply(instance);
    	}
    	
    	public void invalidate() {
    		function = null;
    	}
    	
    	public boolean valid() {
    		return function != null;
    	}
    }
    
    public static class WorldVariable<T> {
    	
    	private boolean valueSet = false;
    	private T catchedValue;
    	
    	private MethodHandle get;
    	private MethodHandle set;
    	
    	public WorldVariable(Class<T> type, String name) {
    		if (Versions.isForgeInstalled()) {
    			Field field;
	    		try {
					field = World.class.getDeclaredField(name);
				} catch (Throwable e) {
					field = null;
				}
	    		if (field != null) {
	    			MethodHandles.Lookup lookup = MethodHandles.lookup();
	    			try {
						get = lookup.findGetter(World.class, name, type);
						set = lookup.findSetter(World.class, name, type);
					} catch (Throwable e) {
						get = set = null;
					}
	    		}
    		}
    	}
    	
    	public T get(World instance, T def) {
    		if (get != null) {
				try {
					return (T)get.invoke(instance);
				} catch (Throwable e) {
					get = null;
				}
    		}
    		return valueSet ? catchedValue : def;
    	}
    	
    	public void set(World instance, T val) {
    		valueSet = true;
    		catchedValue = val;
    		if (set != null) {
				try {
					set.invoke(instance, val);
				} catch (Throwable e) {
					set = null;
				}
    		}
    	}
    }
}
