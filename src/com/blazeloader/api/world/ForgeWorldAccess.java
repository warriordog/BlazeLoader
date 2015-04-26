package com.blazeloader.api.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

import com.google.common.collect.ImmutableSetMultimap;

//TODO: Check that these method signatures are correct for 1.8 Forge
public interface ForgeWorldAccess {
	/**
	 * Checks if the given side of a block is solid.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param pos	The location
	 * @param side	The face (taken as EnumFacing and parse internally to ForgeDirection for forge)
	 */
	public <ForgeDirection extends Enum> boolean isSideSolid(BlockPos pos, ForgeDirection side, boolean def);
	
	/**
	 * Gets the set of chunks persisted by Forge Modloader.
	 * 
	 * @forge This is part of the Forge API specification
	 */
	public <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks();
	
	/**
	 * Counts the number of entities with the given creature type.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param type			Type of entity
	 * @param forSpawnCount	True if we are checking for spawn count limits
	 */
	public int countEntities(EnumCreatureType type, boolean forSpawnCount);
	
	/**
	 * Gets the maximum entity size. Used when checking if an entity is within a given region.
	 * 
	 * @forge This is an accessor method for {@code World.MAX_ENTITY_RADIUS} in the Forge API specification
	 * @param def	A default value to return if none others are found
	 */
	public double getMaxEntitySize(double def);
	
	/**
	 * Sets the maximum entity size. Used when checking if an entity is within a given region.
	 * 
	 * @forge This is an accessor method for {@code World.MAX_ENTITY_RADIUS} in the Forge API specification
	 * @param size	The maximum entity size
	 */
	public void setMaxEntitySize(double size);
}
