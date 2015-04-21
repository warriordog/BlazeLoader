package com.blazeloader.api.world;

import com.blazeloader.api.block.UpdateType;
import com.blazeloader.bl.main.BLMain;
import com.blazeloader.util.version.Versions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A collection of useful functions relating to worlds.
 */
public class ApiWorld {
    private static List<IChunkGenerator> generators = new ArrayList<IChunkGenerator>();
    // The unmodifiable list just holds a reference to the original. I'd rather keep it instead of creating a new one for each chunk loaded.
    private static List<IChunkGenerator> unmodifiable_generators = Collections.unmodifiableList(generators);

    public static List<IChunkGenerator> getGenerators() {
        return unmodifiable_generators;
    }
    
    /**
     * Registers a chunk generator
     *
     * @param generator The generator to register
     */
    public static void registerChunkGenerator(IChunkGenerator generator) {
        generators.add(generator);
    }

    /**
     * Gets all entities in the world within a certain radius from a given point
     *
     * @param w   World
     * @param x   XCoordinate
     * @param y   YCoordinate
     * @param z   ZCoordinate
     * @param rad Maximum Radius
     * @return List of matching Entities
     */
    public static List<Entity> getEntitiesNear(World w, int x, int y, int z, double rad) {
        return getEntitiesOfTypeNear(w, null, x, y, z, rad);
    }
    
    /**
	 * Gets an entity by its Unique Identifier
	 * 
	 * @param w		The world
	 * @param uuid	A uuid for the entity
	 * @return The matching entity or null if none were found
	 */
	public static Entity getEntityByUUId(World w, UUID uuid) {
		if (w instanceof WorldServer) {
			return ((WorldServer)w).getEntityFromUuid(uuid);
		}
		for (Entity i : (List<Entity>)w.loadedEntityList) {
			if (i.getUniqueID().equals(uuid)) {
				return i;
			}
		}
		return null;
	}
    
    /**
     * Gets entities of type in the world within a certain radius from a given point
     * Acts like getEntitiesNear() when Entity Type is null
     *
     * @param w   World
     * @param c   Entity Type
     * @param x   XCoordinate
     * @param y   YCoordinate
     * @param z   ZCoordinate
     * @param rad Maximum Radius
     * @return List of matching Entities
     */
    public static List<Entity> getEntitiesOfTypeNear(World w, Class<? extends Entity> c, int x, int y, int z, double rad) {
        List<Entity> result = new ArrayList<Entity>();
        for (Entity i : ((List<Entity>) w.loadedEntityList)) {
            if (c == null || i.getClass() == c) {
                if (i.posX >= x - rad && i.posX <= x + rad) {
                    if (i.posY >= y - rad && i.posY <= y + rad) {
                        if (i.posZ >= z - rad && i.posZ <= z + rad) result.add(i);
                    }
                }
            }
        }

        return result;
    }
    
    /**
     * Gets he closest player withing a certain distance relative to a given location.
     * 
     * @param w				World to look in
     * @param pos			BlockPos for the origin to look from
     * @param distance		Distance up to which we will search
     * @return	Closest player of null
     */
    public static EntityPlayer getClosestPlayer(World w, BlockPos pos, double distance) {
    	return w.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), distance);
    }
    
	/**
	 * Selects a random spawn list entry for the given creature type and location
	 * 
	 * @param w   World
	 * @param creatureType	The type of creature to spawn
	 * @param pos			The location
	 */
	public static BiomeGenBase.SpawnListEntry getSpawnListEntryForTypeAt(World w, EnumCreatureType creatureType, BlockPos pos) {
		List possibleTypes = w.getChunkProvider().getPossibleCreatures(creatureType, pos);
        return possibleTypes != null && !possibleTypes.isEmpty() ? (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(w.rand, possibleTypes) : null;
	}
	
	/**
	 * Checks if the given spawn list entry is allowed as a possible spawn for the given creature type and location
	 * 
	 * @param w   World
	 * @param creatureType		The type of creature to spawn
	 * @param spawnListEntry	The entry we would like to spawn
	 * @param pos				The location we would like to spawn at
	 * @return	True if we can spawn here
	 */
	public static boolean canSpawnHere(World w, EnumCreatureType creatureType, BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos) {
		List possibleTypes = w.getChunkProvider().getPossibleCreatures(creatureType, pos);
        return possibleTypes != null && !possibleTypes.isEmpty() ? possibleTypes.contains(spawnListEntry) : false;
	}

    /**
     * Sets the block at a specified location. And triggers a block update
     *
     * @param world The world to change the block in.
     * @param x     The X-coordinate to change.
     * @param y     The Y-coordinate to change.
     * @param z     The Z-coordinate to change.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block) {
        setBlockAt(world, new BlockPos(x, y, z), block);
    }
	
    /**
     * Sets the block at a specified location.  And triggers a block update
     *
     * @param world The world to change the block in.
     * @param pos   The position of the block.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block) {
    	setBlockAt(world, pos, block, UpdateType.UPDATE_AND_NOTIFY);
    }
    

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param x          The X-coordinate to change.
     * @param y          The Y-coordinate to change.
     * @param z          The Z-coordinate to change.
     * @param block      the block to set
     * @param notifyFlag The notification flags.
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block, UpdateType notifyFlag) {
        setBlockAt(world, new BlockPos(x, y, z), block, notifyFlag);
    }
    
    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param pos        The position of the block.
     * @param block      the block to set
     * @param notifyFlag The notification flags.
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block, UpdateType notifyFlag) {
        world.setBlockState(pos, block, notifyFlag.getValue());
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     	the world containing the block
     * @param x         	XCoordinate
     * @param y         	YCoordinate
     * @param z         	ZCoordinate
     * @param dropItems		true if the block must drop as an item, or it's contained items
     */
    public static void destroyBlock(World world, int x, int y, int z, boolean dropItems) {
        destroyBlock(world, new BlockPos(x, y, z), dropItems);
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     	the world containing the block
     * @param pos       	the position of the block.
     * @param dropItems		true if the block must drop as an item, or it's contained items
     */
    public static void destroyBlock(World world, BlockPos pos, boolean dropItems) {
        world.destroyBlock(pos, dropItems);
    }

    /**
     * Play sound and particle effect for a block being broken without removing
     * the block
     *
     * @param w World
     * @param x XCoordinate
     * @param y YCoordinate
     * @param z ZCoordinate
     */
    public static void playBlockDestructionEffect(World w, int x, int y, int z) {
        playBlockDestructionEffect(w, new BlockPos(x, y, z));
    }

    /**
     * Play sound and particle effect for a block being broken without removing
     * the block
     *
     * @param w   World
     * @param pos The position of the block.
     */
    public static void playBlockDestructionEffect(World w, BlockPos pos) {
        if (!w.isRemote) {
            playAuxSFX(w, AuxilaryEffects.BLOCK_BREAK, pos, Block.getStateId(w.getBlockState(pos)));
        }
    }
    
    /**
     * Spawns dispenser particles in front of the block at the given location in the indicated direction
     *
     * @param w  		World
     * @param pos 		The position of the block.
     * @param direction	The direction the block is facing
     */
    public static void spawnDispenserParticles(World w, BlockPos pos, EnumFacing direction) {
        playAuxSFX(w, AuxilaryEffects.DISPENSE_PARTICLES, pos, direction.getFrontOffsetX() + 1 + (direction.getFrontOffsetZ() + 1) * 3);
    }
    
	/**
	 * Plays a standard sound effect at the given location.
	 * 
	 * @param w  		World
	 * @param soundType	The effect to spawn 
	 * @param pos		The location
	 * @param volume	Volume
	 */
	public static void playAuxSFX(World w, AuxilaryEffects soundType, BlockPos pos, int volume) {
		w.playAuxSFX(soundType.getId(), pos, volume);
	}
	
	/**
	 * Plays a standard sound effect at the given location to the given player.
	 * 
	 * @param w  		World
	 * @param player	The player to recieve the sound
	 * @param soundType	The effect to spawn
	 * @param pos		The location
	 * @param volume	Volume
	 */
	public static void playAuxSFX(World w, EntityPlayer player, AuxilaryEffects soundType, BlockPos pos, int volume) {
		w.playAuxSFXAtEntity(player, soundType.getId(), pos, volume);
	}
    
    /**
     * Gets the IntegratedServer.worldServers[] index of the specified world. 
     *
     * @param dimensionLevel The dimension to get the index of.
     * @return Return the index of the dimension.
     */
    public static int getDimensionIndex(int dimensionLevel) {
        if (dimensionLevel == -1) return 1;
        if (dimensionLevel == 1) return 2;
        return dimensionLevel;
    }

    /**
     * Gets the world for the specified dimension.  Should be a dimension index returned by getDimensionIndex.
     *
     * @param dimension The dimension to get.
     * @return The WorldServer for the specified index.
     */
    public static WorldServer getServerWorldForDimension(int dimension) {
    	WorldServer[] worldServers = null;
    	if (Versions.isClient()) {
	    	try {
	    		if (net.minecraft.client.Minecraft.getMinecraft().isSingleplayer()) {
	    			worldServers = net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().worldServers;
	    		}
	    	} catch (Exception e) {
	    		BLMain.LOGGER_FULL.logError("Exception in fetching worldservers for side CLIENT. Please submit a bug report to Blazeloader devs.", e);
	    	}
    	} else {
	    	try {
	    		worldServers = net.minecraft.server.MinecraftServer.getServer().worldServers;
	    	} catch (Throwable e) {
	    		BLMain.LOGGER_FULL.logError("Exception in fetching worldservers for side SERVER. Please submit a bug report to Blazeloader devs.", e);
	    	}
    	}
    	if (worldServers != null) {
    		dimension = getDimensionIndex(dimension);
    		if (dimension < 0 && dimension >= worldServers.length) {
    			dimension = 0;
    			BLMain.LOGGER_FULL.logWarning("Unsupported dimension index. Make sure you pass in the index of the dimension you want, not the dimension code.");
    		}
    		return worldServers[dimension];
    	}
        return null;
    }
    
    /**
     * Gets the state of the block found at the specified location.
     *
     * @param world The world to get the block from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the blockstate Mapping at the specified location.
     */
    public static IBlockState getBlockAt(World world, int x, int y, int z) {
        return getBlockAt(world, new BlockPos(x, y, z));
    }

    /**
     * Gets the state of the block found at the specified location.
     *
     * @param world The world to get the block from.
     * @param pos   The position of the block.
     * @return Return the blockstate Mapping at the specified location.
     */
    public static IBlockState getBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos);
    }
    
}