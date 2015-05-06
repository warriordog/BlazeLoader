package com.blazeloader.api.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import com.blazeloader.bl.interop.Func;
import com.blazeloader.bl.interop.Var;
import com.blazeloader.util.version.Versions;
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
    
	private static final Var<World, Double> MAX_ENTITY_RADIUS = new Var(World.class, double.class, "MAX_ENTITY_RADIUS");
	
	private static Func<ForgeWorldAccess, Boolean> _isSideSolid;
    private static Func<ForgeWorldAccess, ImmutableSetMultimap> _getPersistentChunks;
    private static Func<ForgeWorldAccess, Integer> _countEntities;
    private static Func<ForgeWorldAccess, MapStorage> _getPerWorldStorage;
    private static Func<ForgeWorldAccess, Integer> _getBlockLightOpacity;
	
	protected static boolean isSideSolid(World worldObj, BlockPos pos, EnumFacing side, boolean def) {
		if (Versions.isForgeInstalled()) {
			//return worldObj.isSideSolid(pos, side, def);
	    	if (_isSideSolid == null) {
	    		_isSideSolid = new Func(ForgeWorldAccess.class, World.class, boolean.class, "isSideSolid", BlockPos.class, EnumFacing.class, boolean.class);
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
			//return worldObj.getPersistentChunks();
	    	if (_getPersistentChunks == null) {
	    		_getPersistentChunks = new Func(ForgeWorldAccess.class, World.class, ImmutableSetMultimap.class, "getPersistentChunks");
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
			//return worldObj.getBlockLightOpacity(pos);
			if (_getBlockLightOpacity == null) {
				_getBlockLightOpacity = new Func(ForgeWorldAccess.class, World.class, int.class, "getBlockLightOpacity", BlockPos.class);
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
			//return worldObj.countEntities(type, forSpawnCount);
	    	if (_countEntities == null) {
	    		_countEntities = new Func(ForgeWorldAccess.class, World.class, int.class, "countEntities", EnumCreatureType.class, boolean.class);
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
			//return worldObj.getPerWorldStorage();
			if (_getPerWorldStorage == null) {
				_getPerWorldStorage = new Func(ForgeWorldAccess.class, World.class, MapStorage.class, "getPerWorldStorage");
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
		//return worldObj.MAX_ENTITY_RADIUS;
		return MAX_ENTITY_RADIUS.get(worldObj, def);
	}
	
	protected static void setMaxEntitySize(World worldObj, double size) {
		//worldObj.MAX_ENTITY_RADIUS = size;
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
}
