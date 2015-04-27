package com.blazeloader.api.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.storage.MapStorage;

import com.google.common.collect.ImmutableSetMultimap;

public interface ForgeWorldAccess {
	
	/**
	 * Checks if the given side of a block is solid.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param pos	The location
	 * @param side	The face
	 */
	public boolean isSideSolid(BlockPos pos, EnumFacing side);
	
	/**
	 * Checks if the given side of a block is solid.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param pos	The location
	 * @param side	The face
	 */
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def);
	
	/**
	 * Gets the set of chunks persisted by Forge Modloader.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param <Ticket> A forge chunk manager ticket.
	 */
	public <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks();
	
	/**
	 * Gets the amount of light a block will allow through
	 */
	public int getBlockLightOpacity(BlockPos pos);
	
	/**
	 * Counts the number of entities with the given creature type.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param type			Type of entity
	 * @param forSpawnCount	True if we are checking for spawn count limits
	 */
	public int countEntities(EnumCreatureType type, boolean forSpawnCount);
	
	/**
	 * Gets the per-world map storage introduced by forge
	 */
	public MapStorage getPerWorldStorage();
	
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
